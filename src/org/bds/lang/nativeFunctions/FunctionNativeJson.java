package org.bds.lang.nativeFunctions;

import org.bds.lang.Parameters;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.run.BdsThread;
import org.bds.scope.JsonParser;

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

        String[] argNames = {"fileName"};
        Type[] argTypes = {Types.STRING};
        parameters = Parameters.get(argTypes, argNames);
        addNativeFunction();
    }

    @Override
    protected Object runFunctionNative(BdsThread bdsThread) {
        String jsonFileName = bdsThread.getString("fileName");
        var jsonParser = new JsonParser(bdsThread, jsonFileName, null, null);
        return jsonParser.parse();
    }

}
