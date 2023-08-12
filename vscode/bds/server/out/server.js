"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const node_1 = require("vscode-languageserver/node");
const vscode_languageserver_textdocument_1 = require("vscode-languageserver-textdocument");
const definitionLogic_1 = require("./definitionLogic");
let connection = (0, node_1.createConnection)(node_1.ProposedFeatures.all);
let documents = new node_1.TextDocuments(vscode_languageserver_textdocument_1.TextDocument);
let hasWorkspaceFolderCapability = false;
let definitionLogic = new definitionLogic_1.DefinitionLogic();
let indexer = definitionLogic["indexer"];
connection.onInitialize((params) => {
    hasWorkspaceFolderCapability = !!(params.capabilities.workspace &&
        params.capabilities.workspace.workspaceFolders);
    return {
        capabilities: {
            textDocumentSync: node_1.TextDocumentSyncKind.Incremental,
            definitionProvider: true,
        },
    };
});
documents.onDidChangeContent((change) => {
    indexer.parseAndIndexDocument(change.document);
});
connection.onDefinition((textDocumentPosition) => {
    const document = documents.get(textDocumentPosition.textDocument.uri);
    return document
        ? definitionLogic.getDefinition(document, textDocumentPosition.position)
        : null;
});
documents.listen(connection);
connection.listen();
