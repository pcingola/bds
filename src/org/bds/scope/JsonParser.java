package org.bds.scope;

import org.bds.data.Data;
import org.bds.lang.type.Type;
import org.bds.lang.type.TypeList;
import org.bds.lang.value.*;
import org.bds.run.BdsThread;
import org.bds.util.Gpr;
import org.bds.util.GprString;

import javax.json.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

/**
 * Parse a JSON file and set environment
 * This is used in bds functions:
 * json(fileName): Set current scope with JSON values
 * json(fileName, bdsObject): Set 'bdsObject' with JSON values
 * <p>
 * <p>
 * parse
 * scope, obj
 * scope.getNames
 *
 * @author pcingola
 */
public class JsonParser {

    ValueObject bdsObject;  // Object from bds runtime that we are trying to set, i.e. json(fileName, object)
    BdsThread bdsThread;  // Current bdsThread (this is invoked at runtime by bdsVM)
    String fileName;  // JSON file to parse
    Data jsonData;  // JSON data from file
    String jsonTxt;  // Full JSON text from file
    Scope scope;  // Current bds scope
    List<String> subFieldNames; // List of sub-field from JSON file to use.

    public JsonParser(BdsThread bdsThread, String jsonFileName, ValueObject bdsObject, ValueList subFieldNames) {
        this.bdsThread = bdsThread;
        this.scope = bdsThread.getScope();
        this.bdsObject = bdsObject;
        this.fileName = jsonFileName;

        // Convert the list of subFieldNames to strings
        if (subFieldNames != null && subFieldNames.size() > 0) {
            this.subFieldNames = new ArrayList<String>(subFieldNames.size());
            for (Value v : subFieldNames) this.subFieldNames.add(v.toString());
        }
    }

    /**
     * Download a file (if it's non-local)
     *
     * @return True on success, False on error
     */
    public boolean downloadData() {
        jsonData = bdsThread.data(fileName);

        // Download remote file
        return !jsonData.isRemote() //
                || jsonData.isDownloaded() //
                || jsonData.download(); // Download error
    }

    public String getJsonTxt() {
        if (jsonTxt == null)
            jsonTxt = Gpr.readFile(jsonData.getLocalPath());
        return jsonTxt;
    }

    /**
     * Find a 'name' that matches a set of names
     *
     * @return Original name from 'names' that matches or null if not found
     */
    String findMatchingName(ValuesGetSet valuesGetSet, String name) {
        Collection<String> names = valuesGetSet.getNames();

        // First rule: Exact match
        if (names.contains(name)) return name;

        // Second rule: Lower case match
        var lname = name.toLowerCase();
        Optional<String> found = names.stream().filter(n -> n.toLowerCase().equals(lname)).findFirst();
        if (found.isPresent()) return found.get();

        // Third rule: Remove non-alphanumeric chars and lowercase
        var anName = GprString.removeNonAlphaNumeric(name).toLowerCase();
        found = names.stream().filter(n -> GprString.removeNonAlphaNumeric(n).toLowerCase().equals(anName)).findFirst();
        if (found.isPresent()) return found.get();

        // Not found
        return null;
    }

    /**
     * Parse JSON file and set scope or object
     *
     * @return The JSON text from the file (side effect: set global context / object)
     */
    public String parse() {
        if (!downloadData()) {
            bdsThread.fatalError("Failed to download data from file '" + fileName + "'");
            return "";
        }
        if (bdsObject != null && !bdsObject.getType().isClass()) {
            bdsThread.fatalError("Cannot populate non-object type '" + bdsObject.getType() + "'");
            return "";
        }
        parseJsonFile();
        return getJsonTxt();
    }

    /**
     * Read JSON (local) file, parse it and set environment
     */
    void parseJsonFile() {
        bdsThread.log("Setting variables from JSON file '" + fileName + "'");
        try {
            // Read JSON file (or local copy of downloaded file)
            InputStream is = new FileInputStream(new File(fileName));
            JsonReader rdr = Json.createReader(is);
            JsonObject jobj = rdr.readObject();
            // We either set an object (if provided) or the scope (if no object was provided)
            ValuesGetSet valuesGetSet = bdsObject != null ? bdsObject : bdsThread.getScope();
            // Search in the json object values according to the list of subFieldNames
            if (subFieldNames != null) {
                String currentFieldName = "";
                for (String s : subFieldNames) {
                    currentFieldName += (currentFieldName.isEmpty() ? "" : ".") + s;
                    // Check that subfield exists
                    if (!jobj.containsKey(s)) throw new RuntimeException("JSON object from file '" + fileName + "' does not contain field '" + currentFieldName + "'");
                    var val = jobj.get(s);
                    // Check that subfield is an object
                    if (!val.getValueType().equals(JsonValue.ValueType.OBJECT)) throw new RuntimeException("JSON object from file '" + fileName + "', field '" + currentFieldName + "' is not an Object (value type '" + val.getValueType() + "')");
                    // Update current json object
                    jobj = jobj.getJsonObject(s);
                }
            }
            // Set all values in JSON file recursively
            for (Map.Entry<String, JsonValue> e : jobj.entrySet())
                setValue(valuesGetSet, e.getKey(), e.getValue());
        } catch (FileNotFoundException e) {
            bdsThread.runtimeError("Exception while parsing JSON file '" + fileName + "'", e);
        }
    }

    /**
     * Create a new (primitive) value from JSON value 'jval' and make sure it matches the bds vale 'val'
     *
     * @param val:      BDS value to match in type
     * @param jvarName: Original JSON value name (only used for error reporting)
     * @param jval:     JSON value to convert to bds value
     * @return A primitive value or null if it cannot be converted
     */
    Value getNewPrimitiveValue(Value val, String jvarName, JsonValue jval) {
        switch (jval.getValueType()) {
            case NULL:
                return null; // Nothing to do, we ignore NULL values

            case TRUE:
                if (val instanceof ValueBool) return ValueBool.TRUE;
                break;

            case FALSE:
                if (val instanceof ValueBool) return ValueBool.FALSE;
                break;

            case STRING:
                if (val instanceof ValueString) {
                    var js = (JsonString) jval;
                    return new ValueString(js.getString());
                }
                break;

            case NUMBER:
                var jn = (JsonNumber) jval;
                if (val instanceof ValueInt) return new ValueInt(jn.longValue());
                else if (val instanceof ValueReal) return new ValueReal(jn.doubleValue());
                break;
        }
        bdsThread.error("Error parsing JSON file '" + fileName + "', cannot convert JSON entry '" + jvarName + "' type '" + jval.getValueType() + "' to '" + val.getType() + "'");
        return null;
    }

    /**
     * Set value from JSON that matches 'jvarName'
     *
     * @param valuesGetSet: bds values to set. Can be either object or scope
     * @param jvarName:     Variable name (from JSON file)
     * @param jval:         JSON value
     * @return A Value that matches the JSON value 'jval' and bds value 'val'
     */
    void setValue(ValuesGetSet valuesGetSet, String jvarName, JsonValue jval) {
        // Find a matching bds name
        var bdsName = findMatchingName(valuesGetSet, jvarName);
        if (bdsName == null) {
            String typeStr = valuesGetSet instanceof Scope ? "Scope" : "Object type '" + ((ValueObject) valuesGetSet).getType() + "'";
            bdsThread.debug(typeStr + " does not have any name/field matching '" + jvarName + "'");
            return;
        }

        // Get bds value
        var val = valuesGetSet.getValue(bdsName);

        // Set value
        Value valueToSet = null;
        switch (jval.getValueType()) {
            case NULL:
            case TRUE:
            case FALSE:
            case STRING:
            case NUMBER:
                // Create a new primitive value
                valueToSet = getNewPrimitiveValue(val, jvarName, jval);
                break;

            case OBJECT:
                // Object: We are using the existing object to set a value, not creating a new one
                valueToSet = setValueObject(val, jvarName, (JsonObject) jval);
                break;

            case ARRAY:
                // List: We are using the existing list to set a values, not creating a new one
                valueToSet = setValueList(val, jvarName, (JsonArray) jval);
                break;

            default:
                break;
        }

        if (valueToSet == null) {
            bdsThread.error("Error parsing JSON file '" + fileName + "', cannot convert JSON entry '" + jvarName + "' type '" + jval.getValueType() + "' to '" + val.getType() + "'");
            return;
        }

        valuesGetSet.setValue(bdsName, valueToSet);
    }

    /**
     * Set a bds list 'valList' from JSON array 'jl'
     */
    Value setValueList(Value val, String jvarName, JsonArray jlist) {
        if (!(val instanceof ValueList)) return null; // Not a list? Nothing to do
        var bdsList = (ValueList) val;
        TypeList listType = bdsList.getType();
        Type itemType = listType.getElementType();
        // Create, set and add items to the list
        int i = 0;
        for (JsonValue jv : jlist) {
            // Create new bds Value
            var bdsItemVal = itemType.newDefaultValue();
            var jitemName = jvarName + "[" + i + "]";

            // Set value from JSON
            if (itemType.isClass()) {
                // List item is an object, we recurse to set fields
                if (jv instanceof JsonObject) setValueObject(bdsItemVal, jitemName, (JsonObject) jv);
                else
                    bdsThread.error("Could not set list value for type " + itemType + " from JSON file '" + fileName + "', field '" + jitemName + "'. Not a JSON object, JSON value: " + jv);
            } else if (itemType.isList()) {
                // List item is a list, we recurse to set list items (e.g. each list item can be an object)
                if (jv instanceof JsonArray) setValueList(bdsItemVal, jitemName, (JsonArray) jv);
                else
                    bdsThread.error("Could not set list value for type " + itemType + " from JSON file '" + fileName + "', field '" + jitemName + "'. Not a JSON array, JSON value: " + jv);
            } else if (itemType.isPrimitive()) {
                // List item is a primitive value, we recurse to set list items (e.g. each list item can be an object)
                bdsItemVal = getNewPrimitiveValue(bdsItemVal, jitemName, jv);
            } else {
                bdsThread.error("Could not set list value for type " + itemType + " from JSON file '" + fileName + "', field '" + jitemName + "'. JSON value: " + jv);
            }

            // Append value to list
            bdsList.setValue(i, bdsItemVal);
            i++;
        }
        return bdsList;
    }

    /**
     * Set bds object 'valObj' from JSON vobject 'jobj'
     */
    Value setValueObject(Value val, String jvarName, JsonObject jobj) {
        if (!(val instanceof ValueObject)) return null;
        var bdsObj = (ValueObject) val;

        // If the object is 'null' (i.e. not initialized) initialized fields
        if (bdsObj.isNull()) bdsObj.initializeFields();

        // Set every field in the object recursively
        for (Map.Entry<String, JsonValue> e : jobj.entrySet()) {
            var jsonFieldName = e.getKey();
            var jsonValue = e.getValue();
            setValue(bdsObj, jsonFieldName, jsonValue);
        }

        return bdsObj;
    }
}
