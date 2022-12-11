package org.bds;

import org.bds.data.Data;
import org.bds.data.DataHttp;

public class Zzz {
    public static void main(String[] args) {
        System.out.println("Start");

        Exception e;
        Error err;

        Config config = new Config();
        config.setVerbose(true);
        config.setDebug(true);

        DataHttp d = new DataHttp("http://ftp.ensembl.org/pub/release-75/fasta/homo_sapiens/dna/");
        for (Data data : d.list()) {
            System.out.println(data);
        }
        System.out.println("End");
    }
}
