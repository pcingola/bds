package org.bds.languageServer;

import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.services.TextDocumentService;

public class TextDocumentServiceBds implements TextDocumentService {

    private LanguageServerBds languageServer;
    private final LsClientLogger clientLogger;

    public TextDocumentServiceBds(LanguageServerBds languageServer) {
        this.languageServer = languageServer;
        this.clientLogger = LsClientLogger.getInstance();
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams didOpenTextDocumentParams) {
        this.clientLogger.logMessage("Operation 'text/didOpen" //
                + "' {fileUri: '" + didOpenTextDocumentParams.getTextDocument().getUri() + "'} opened");
    }

    @Override
    public void didChange(DidChangeTextDocumentParams didChangeTextDocumentParams) {
        this.clientLogger.logMessage("Operation 'text/didChange" //
                + "' {fileUri: '" + didChangeTextDocumentParams.getTextDocument().getUri() + "'} opened");
    }

    @Override
    public void didClose(DidCloseTextDocumentParams didCloseTextDocumentParams) {
        this.clientLogger.logMessage("Operation 'text/didClose" //
                + "' {fileUri: '" + didCloseTextDocumentParams.getTextDocument().getUri() + "'} opened");

    }

    @Override
    public void didSave(DidSaveTextDocumentParams didSaveTextDocumentParams) {
        this.clientLogger.logMessage("Operation 'text/didSave" //
                + "' {fileUri: '" + didSaveTextDocumentParams.getTextDocument().getUri() + "'} opened");
    }
}
