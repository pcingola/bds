package org.bds.languageServer;

import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.services.LanguageClient;

/**
 * Class to send log messages to the client.
 * Reference: https://github.com/malinthar/lsp-tutorial/blob/master/ballerina-language-server/src/main/java/LSClientLogger.java
 */
public class LsClientLogger {

    private static LsClientLogger instance;
    private LanguageClient client;

    private LsClientLogger() {
    }

    public static LsClientLogger getInstance() {
        if (instance == null) {
            instance = new LsClientLogger();
        }
        return instance;
    }

    public void initialize(LanguageClient languageClient) {
        this.client = languageClient;
    }

    public void logMessage(String message) {
        if (client == null) return;
        client.logMessage(new MessageParams(MessageType.Info, message));
    }
}
