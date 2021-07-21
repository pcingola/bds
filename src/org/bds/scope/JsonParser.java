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

    public JsonParser(BdsThread bdsThread, String jsonFileName) {
        this.bdsThread = bdsThread;
        this.scope = bdsThread.getScope();
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
                setScope(scope, e.getKey(), e.getValue(), fileName, bdsThread);
            }
        } catch (FileNotFoundException e) {
            bdsThread.runtimeError("Exception while parsing JSON file '" + fileName + "'", e);
        }
    }

    /**
     * Set value in scope
     */
    void setScope(Scope scope, String varName, JsonValue jval, String fileName, BdsThread bdsThread) {
        if (!scope.hasValue(varName)) {
            bdsThread.log("Variable '" + varName + "' not found, skipping");
            return;
        }

        Value val = scope.getValue(varName);
        setValue(val, varName, jval, fileName, bdsThread);
    }

    /**
     * Set bds Value from JSON value
     */
    void setValue(Value val, String varName, JsonValue jval, String fileName, BdsThread bdsThread) {
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
                if (val instanceof ValueClass) {
                    var jobj = (JsonObject) jval;
                    var valClass = (ValueClass) val;
                    for (Map.Entry<String, JsonValue> e : jobj.entrySet()) {
                        var fieldName = e.getKey();
                        var fieldValue = valClass.getValue(fieldName);
                        if (fieldValue != null) setValue(fieldValue, fieldName, e.getValue(), fileName, bdsThread);
                        else
                            bdsThread.error("Error parsing JSON file '" + fileName + "', variable '" + varName + "', class '" + valClass.getType() + "', does not have field '" + fieldName + "'");
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
                        var itemVal = itemType.newValue();
                        // Set value
                        setValue(itemVal, varName + "[" + i + "]", jv, fileName, bdsThread);
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
        ) {
            bdsThread.fatalError("Failed to download data from file '" + fileName + "'");
            return false; // Download error
        }

        return true;
    }

    /**
     * Parse JSON file and set scope
     */
    public String parse() {
        if (!downloadData()) return "";
        parseJsonFile();
        return getJsonTxt();
    }
}
