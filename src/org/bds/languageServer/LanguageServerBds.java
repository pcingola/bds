package org.bds.languageServer;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * This is an implementation of Language Server Protocol (LSP) for bds
 * LSP is used by editors for extended functionality (e.g. auto-complete, debugging, etc.)
 * <p>
 * References:
 * - https://medium.com/ballerina-techblog/practical-guide-for-the-language-server-protocol-3091a122b750
 * - https://github.com/malinthar/lsp-tutorial
 * - https://github.com/eclipse/lsp4j
 * - https://www.typefox.io/blog/eclipse-lsp4j-is-here
 * - https://ncona.com/2021/12/implementing-a-language-server-protocol-client
 */
public class LanguageServerBds implements LanguageServer, LanguageClientAware {

    private TextDocumentService textDocumentService;
    private WorkspaceService workspaceService;
    private ClientCapabilities clientCapabilities;
    private LanguageClient languageClient;
    private int shutdown = 1;

    public LanguageServerBds() {
        this.textDocumentService = new TextDocumentServiceBds(this);
        this.workspaceService = new WorkspaceServiceBds(this);
    }

    @Override
    public void cancelProgress(WorkDoneProgressCancelParams params) {
        LanguageServer.super.cancelProgress(params);
    }

    @Override
    public void connect(LanguageClient languageClient) {
        this.languageClient = languageClient;
        LsClientLogger.getInstance().initialize(this.languageClient);
    }

    @Override
    public void exit() {
        System.exit(shutdown);
    }

    @Override
    public TextDocumentService getTextDocumentService() {
        return this.textDocumentService;
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        return this.workspaceService;
    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams initializeParams) {
        final InitializeResult response = new InitializeResult(new ServerCapabilities());
        // Set the document synchronization capabilities to full.
        response.getCapabilities().setTextDocumentSync(TextDocumentSyncKind.Full);
        this.clientCapabilities = initializeParams.getCapabilities();

        /* Check if dynamic registration of completion capability is allowed by the client. If so we don't register the capability.
           Else, we register the completion capability.
         */
        if (!isDynamicCompletionRegistration()) {
            response.getCapabilities().setCompletionProvider(new CompletionOptions());
        }
        return CompletableFuture.supplyAsync(() -> response);
    }

    @Override
    public void initialized(InitializedParams params) {
        // Check if dynamic completion support is allowed, if so register.
        if (isDynamicCompletionRegistration()) {
            CompletionRegistrationOptions completionRegistrationOptions = new CompletionRegistrationOptions();
            Registration completionRegistration = new Registration(UUID.randomUUID().toString(), "textDocument/completion", completionRegistrationOptions);
            languageClient.registerCapability(new RegistrationParams(List.of(completionRegistration)));
        }
    }

    private boolean isDynamicCompletionRegistration() {
        TextDocumentClientCapabilities textDocumentCapabilities = clientCapabilities.getTextDocument();
        return textDocumentCapabilities != null && textDocumentCapabilities.getCompletion() != null && Boolean.FALSE.equals(textDocumentCapabilities.getCompletion().getDynamicRegistration());
    }

    @Override
    public void setTrace(SetTraceParams params) {
        LanguageServer.super.setTrace(params);
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        shutdown = 0;
        return CompletableFuture.supplyAsync(Object::new);
    }
}
