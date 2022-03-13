package org.bds.lang.nativeFunctions;

import org.bds.lang.Parameters;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.lang.value.Value;
import org.bds.lang.value.ValueObject;
import org.bds.run.BdsThread;
import org.bds.scope.JsonParser;

/**
 * Parse a JSON file and set environment
 *
 * @author pcingola
 */
public class FunctionNativeJsonObj extends FunctionNative {

    private static final long serialVersionUID = 6415936745404236449L;

    public FunctionNativeJsonObj() {
        super();
    }

    @Override
    protected void initFunction() {
        functionName = "json";
        returnType = Types.STRING;

        String argNames[] = {"fileName", "object"};
        Type argTypes[] = {Types.STRING, Types.ANY};
        parameters = Parameters.get(argTypes, argNames);
        addNativeFunction();
    }

    @Override
    protected Object runFunctionNative(BdsThread bdsThread) {
        String jsonFileName = bdsThread.getString("fileName");
        Value bdsObject = bdsThread.getValue("object");
        if (bdsObject instanceof ValueObject) {
            var jsonParser = new JsonParser(bdsThread, jsonFileName, (ValueObject) bdsObject);
            return jsonParser.parse();
        }
        bdsThread.error("JSON: Cannot set non-object type '" + bdsObject.getType() + "' with JSON values from file '" + jsonFileName + "'");
        return "";
    }

}
