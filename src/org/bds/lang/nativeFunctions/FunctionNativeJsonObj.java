package org.bds.lang.nativeFunctions;

import org.bds.lang.Parameters;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.lang.value.*;
import org.bds.run.BdsThread;
import org.bds.scope.JsonParser;
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
        var jsonParser = new JsonParser(bdsThread, jsonFileName, bdsObject);
        return jsonParser.parse();
    }

}
