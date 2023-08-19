package org.bds.languageServer;

import org.bds.compile.CompilerMessages;
import org.bds.lang.ProgramUnit;

import com.google.gson.Gson;

public class LspServices {
    
    private ProgramUnit programUnit;
    private CompilerMessages compilerMessages;
    
    public LspServices(ProgramUnit programUnit, CompilerMessages compilerMessages) {
        this.programUnit = programUnit;
        this.compilerMessages = compilerMessages;
    }

    public String getCompilerMessagesAsJsonString() {
        return new Gson().toJson(compilerMessages);
    }
}
