package org.bds.languageServer;

import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.DidChangeWatchedFilesParams;
import org.eclipse.lsp4j.RenameFilesParams;
import org.eclipse.lsp4j.services.WorkspaceService;

public class WorkspaceServiceBds implements WorkspaceService {

    LsClientLogger clientLogger;
    private LanguageServerBds languageServer;

    public WorkspaceServiceBds(LanguageServerBds languageServer) {
        this.languageServer = languageServer;
        clientLogger = LsClientLogger.getInstance();
    }

    @Override
    public void didChangeConfiguration (DidChangeConfigurationParams didChangeConfigurationParams){
        this.clientLogger.logMessage("Operation 'workspace/didChangeConfiguration' Ack");
    }

    @Override
    public void didChangeWatchedFiles (DidChangeWatchedFilesParams didChangeWatchedFilesParams){
        this.clientLogger.logMessage("Operation 'workspace/didChangeWatchedFiles' Ack");
    }

    @Override
    public void didRenameFiles (RenameFilesParams params){
        this.clientLogger.logMessage("Operation 'workspace/didRenameFiles' Ack");
    }

}
