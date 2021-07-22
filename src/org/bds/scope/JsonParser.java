package org.bds.scope;

import org.bds.data.Data;
import org.bds.lang.type.Type;
import org.bds.lang.type.TypeList;
import org.bds.lang.value.*;
import org.bds.run.BdsThread;
import org.bds.util.Gpr;

import javax.json.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

/**
 * Parse a JSON file and set environment
 *
 * @author pcingola
 */
public class JsonParser {

    BdsThread bdsThread;
    Scope scope;
    String fileName;
    Data jsonData;
    String jsonTxt;
    Value bdsObject;

    public JsonParser(BdsThread bdsThread, String jsonFileName) {
        this(bdsThread, jsonFileName, null);
    }

    public JsonParser(BdsThread bdsThread, String jsonFileName, Value bdsObject) {
        this.bdsThread = bdsThread;
        this.scope = bdsThread.getScope();
        this.bdsObject = bdsObject;
        this.fileName = jsonFileName;
    }

    public String getJsonTxt() {
        if (jsonTxt == null)
            jsonTxt = Gpr.readFile(jsonData.getLocalPath());
        return jsonTxt;
    }

    /**
     * Read JSON (local) file, parse it and set environment
     */
    void parseJsonFile() {
        bdsThread.log("Setting variables from JSON file '" + fileName + "'");
        try {
            InputStream is = new FileInputStream(new File(fileName));
            JsonReader rdr = Json.createReader(is);
            JsonObject jobj = rdr.readObject();
            Scope scope = bdsThread.getScope();
            for (Map.Entry<String, JsonValue> e : jobj.entrySet()) {
                if (bdsObject != null) setObject(e.getKey(), e.getValue());
                else setScope(e.getKey(), e.getValue());
            }
        } catch (FileNotFoundException e) {
            bdsThread.runtimeError("Exception while parsing JSON file '" + fileName + "'", e);
        }
    }

    /**
     * Set values from scope
     */
    void setObject(String varName, JsonValue jval) {
        Value val = ((ValueObject) bdsObject).getFieldValue(varName);
        if (val == null) {
            bdsThread.log("Field '" + varName + "' not found, in object type '" + val.getType() + "' skipping");
            return;
        }
        setValue(val, varName, jval);
    }

    /**
     * Set values from scope
     */
    void setScope(String varName, JsonValue jval) {
        if (!scope.hasValue(varName)) {
            bdsThread.log("Variable '" + varName + "' not found, skipping");
            return;
        }

        Value val = scope.getValue(varName);
        setValue(val, varName, jval);
    }

    /**
     * Set bds Value from JSON value
     */
    void setValue(Value val, String varName, JsonValue jval) {
        switch (jval.getValueType()) {
            case NULL:
                return;
            case TRUE:
                if (val instanceof ValueBool) ((ValueBool) val).setValue(ValueBool.TRUE);
                else
                    bdsThread.error("Error parsing JSON file '" + fileName + "', cannot convert JSON entry '" + varName + "' type '" + jval.getValueType() + "' to '" + val.getType() + "'");
                return;

            case FALSE:
                if (val instanceof ValueBool) ((ValueBool) val).setValue(ValueBool.FALSE);
                else
                    bdsThread.error("Error parsing JSON file '" + fileName + "', cannot convert JSON entry '" + varName + "' type '" + jval.getValueType() + "' to '" + val.getType() + "'");
                return;

            case STRING:
                if (val instanceof ValueString) {
                    var js = (JsonString) jval;
                    var newval = new ValueString(js.getString());
                    val.setValue(newval);
                } else
                    bdsThread.error("Error parsing JSON file '" + fileName + "', cannot convert JSON entry '" + varName + "' type '" + jval.getValueType() + "' to '" + val.getType() + "'");
                return;

            case NUMBER:
                var jn = (JsonNumber) jval;
                if (val instanceof ValueInt) {
                    var newval = new ValueInt(jn.longValue());
                    val.setValue(newval);
                } else if (val instanceof ValueReal) {
                    var newval = new ValueReal(jn.doubleValue());
                    val.setValue(newval);
                } else
                    bdsThread.error("Error parsing JSON file '" + fileName + "', cannot convert JSON entry '" + varName + "' type '" + jval.getValueType() + "' to '" + val.getType() + "'");
                return;

            case OBJECT:
                if (val instanceof ValueObject) {
                    var jobj = (JsonObject) jval;
                    var valObj = (ValueObject) val;
                    // If the object is 'null' (i.e. not initialized) initialized fields
                    if( valObj.isNull()) valObj.initializeFields();
                    for (Map.Entry<String, JsonValue> e : jobj.entrySet()) {
                        var fieldName = e.getKey();
                        // Get field's value, create new value if null
                        if (valObj.hasField(fieldName)) {
                            var fieldValue = valObj.getFieldValue(fieldName);
                            if(fieldValue==null) {
                                Gpr.debug("CREATE FIELD: " + fieldName + ", TYPE " + valObj.getFieldType(fieldName) );
                                fieldValue = valObj.getFieldType(fieldName).newValue();
                            }
                            setValue(fieldValue, fieldName, e.getValue());
                        } else
                            bdsThread.error("Error parsing JSON file '" + fileName + "', variable '" + varName + "', class '" + valObj.getType() + "', does not have field '" + fieldName + "'");
                    }
                } else
                    bdsThread.error("Error parsing JSON file '" + fileName + "', cannot convert JSON entry '" + varName + "' type '" + jval.getValueType() + "' to '" + val.getType() + "'");
                return;

            case ARRAY:
                if (val instanceof ValueList) {
                    var jl = (JsonArray) jval;
                    var valList = (ValueList) val;
                    TypeList listType = valList.getType();
                    Type itemType = listType.getElementType();
                    // Create, set and add items to the list
                    int i = 0;
                    for (JsonValue jv : jl) {
                        // Create new bds Value
                        var itemVal = itemType.newDefaultValue();
                        // Set value
                        setValue(itemVal, varName + "[" + i + "]", jv);
                        // Append value to list
                        valList.setValue(i, itemVal);
                        i++;
                    }
                } else
                    bdsThread.error("Error parsing JSON file '" + fileName + "', cannot convert JSON entry '" + varName + "' type '" + jval.getValueType() + "' to '" + val.getType() + "'");
                return;

            default:
                bdsThread.error("Error parsing JSON file '" + fileName + "', cannot convert JSON entry '" + varName + "' type '" + jval.getValueType() + "' to '" + val.getType() + "'");
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
        if (jsonData.isRemote() //
                && !jsonData.isDownloaded() //
                && !jsonData.download() //
        ) return false; // Download error

        return true;
    }

    /**
     * Parse JSON file and set scope
     */
    public String parse() {
        if (!downloadData()) {
            bdsThread.fatalError("Failed to download data from file '" + fileName + "'");
            return "";
        }
        if (bdsObject!=null && !bdsObject.getType().isClass()) {
            bdsThread.fatalError("Cannot populate non-object type '" + bdsObject.getType() + "'");
            return "";
        }
        parseJsonFile();
        return getJsonTxt();
    }
}
