package org.bds;

import org.bds.util.Gpr;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

public class Zzz {

    public static boolean debug = true;
    public static boolean log = true;
    public static boolean verbose = true;

    public static void main(String[] args) throws Exception {
        System.out.println("Start");

        var fileName = Gpr.HOME + "/z.json";
        System.out.println(fileName);
        try (InputStream is = new FileInputStream(new File(fileName));
             JsonReader rdr = Json.createReader(is)) {
            // JsonStructure stuct = rdr.read();
//            System.out.println("STRUCT: " + stuct.getValueType());
//            System.out.println("STRUCT: " + stuct);
            JsonObject jobj = rdr.readObject();
            System.out.println("OBJ: " + jobj);
            for (Map.Entry<String, JsonValue> e : jobj.entrySet()) {
                var val = e.getValue();
                System.out.println(val.getValueType() + "\t" + e);
            }
        }

        System.out.println("End");
    }
}