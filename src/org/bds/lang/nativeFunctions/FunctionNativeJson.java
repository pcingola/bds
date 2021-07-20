package org.bds.lang.nativeFunctions;

import org.bds.data.Data;
import org.bds.lang.Parameters;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.lang.value.*;
import org.bds.run.BdsThread;
import org.bds.scope.Scope;
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
public class FunctionNativeJson extends FunctionNative {

    private static final long serialVersionUID = 6415936745404236449L;

    public FunctionNativeJson() {
        super();
    }

    @Override
    protected void initFunction() {
        functionName = "json";
        returnType = Types.STRING;

        String argNames[] = {"fileName"};
        Type argTypes[] = {Types.STRING};
        parameters = Parameters.get(argTypes, argNames);
        addNativeFunction();
    }

    void parseJson(String fileName, BdsThread bdsThread) {
        log("Setting variables from JSON file '" + fileName + "'");
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
            log("Variable '" + varName + "' not found, skipping");
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
                    for(JsonValue jv : jl) {
                        Gpr.debug("VALUE (from LIST): " + jv);
                       // TODO: Create new bds Value
                       // TODO: Append value to list
                    }
                } else
                    bdsThread.error("Error parsing JSON file '" + fileName + "', cannot convert JSON entry '" + varName + "' type '" + jval.getValueType() + "' to '" + val.getType() + "'");
                return;

            default:
                bdsThread.error("Error parsing JSON file '" + fileName + "', cannot convert JSON entry '" + varName + "' type '" + jval.getValueType() + "' to '" + val.getType() + "'");
        }
    }

    @Override
    protected Object runFunctionNative(BdsThread bdsThread) {
        String name = bdsThread.getString("fileName");
        Data data = bdsThread.data(name);

        // Download remote file
        if (data.isRemote() //
                && !data.isDownloaded() //
                && !data.download() //
        ) return ""; // Download error

        // Read local copy of the data
        parseJson(data.getLocalPath(), bdsThread);

        // Return raw JSON as a String
        return Gpr.readFile(data.getLocalPath());
    }

}
