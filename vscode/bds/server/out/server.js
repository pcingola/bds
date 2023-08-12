"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const node_1 = require("vscode-languageserver/node");
const vscode_languageserver_textdocument_1 = require("vscode-languageserver-textdocument");
const definitionLogic_1 = require("./definitionLogic");
// Create a connection for the server. The connection uses Node's IPC as a transport.
let connection = (0, node_1.createConnection)(node_1.ProposedFeatures.all);
// Create a manager for open text documents.
let documents = new node_1.TextDocuments(vscode_languageserver_textdocument_1.TextDocument);
let hasWorkspaceFolderCapability = false;
let definitionLogic = new definitionLogic_1.DefinitionLogic();
let indexer = definitionLogic["indexer"]; // Accessing indexer from DefinitionLogic
connection.onInitialize((params) => {
    let capabilities = params.capabilities;
    // Check if the client supports workspace folders
    hasWorkspaceFolderCapability = !!(capabilities.workspace && !!capabilities.workspace.workspaceFolders);
    const result = {
        capabilities: {
            textDocumentSync: node_1.TextDocumentSyncKind.Incremental,
            // Tell the client that the server supports Go To Definition
            definitionProvider: true,
        },
    };
    return result;
});
// The content of a text document has changed. This event is emitted
// when the text document is first opened or when its content has changed.
documents.onDidChangeContent((change) => {
    // Update the indexer when document content changes
    indexer.parseAndIndexDocument(change.document);
});
// Register the definition provider
connection.onDefinition((textDocumentPosition) => {
    const document = documents.get(textDocumentPosition.textDocument.uri);
    if (!document) {
        return null;
    }
    return definitionLogic.getDefinition(document, textDocumentPosition.position);
});
// Make the text document manager listen on the connection
documents.listen(connection);
// Listen on the connection
connection.listen();
