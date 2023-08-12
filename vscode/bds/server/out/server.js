"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const node_1 = require("vscode-languageserver/node");
const vscode_languageserver_textdocument_1 = require("vscode-languageserver-textdocument");
const definitionLogic_1 = require("./definitionLogic");
const fs_1 = require("fs");
const path_1 = require("path");
const url_1 = require("url");
let connection = (0, node_1.createConnection)(node_1.ProposedFeatures.all);
let documents = new node_1.TextDocuments(vscode_languageserver_textdocument_1.TextDocument);
let hasWorkspaceFolderCapability = false;
let definitionLogic = new definitionLogic_1.DefinitionLogic();
let indexer = definitionLogic["indexer"];
connection.onInitialize((params) => {
    var _a;
    hasWorkspaceFolderCapability =
        !!((_a = params.capabilities.workspace) === null || _a === void 0 ? void 0 : _a.workspaceFolders);
    return {
        capabilities: {
            textDocumentSync: node_1.TextDocumentSyncKind.Incremental,
            definitionProvider: true,
        },
    };
});
connection.onInitialized(() => {
    if (hasWorkspaceFolderCapability) {
        connection.workspace.getWorkspaceFolders().then((folders) => {
            folders === null || folders === void 0 ? void 0 : folders.forEach(indexBDSFilesInWorkspace);
        });
    }
});
function indexBDSFilesInWorkspace(folder) {
    const folderUri = (0, url_1.fileURLToPath)(folder.uri);
    const bdsFiles = findAllBDSFiles(folderUri);
    bdsFiles.forEach((file) => {
        const content = (0, fs_1.readFileSync)(file, "utf8");
        const document = vscode_languageserver_textdocument_1.TextDocument.create(file, "bds", 1, content);
        indexer.parseAndIndexDocument(document);
    });
}
function findAllBDSFiles(dir, fileList = []) {
    const files = (0, fs_1.readdirSync)(dir);
    for (const file of files) {
        const filePath = (0, path_1.join)(dir, file);
        if ((0, fs_1.statSync)(filePath).isDirectory()) {
            fileList = findAllBDSFiles(filePath, fileList);
        }
        else if ((0, path_1.extname)(file) === ".bds") {
            fileList.push(filePath);
        }
    }
    return fileList;
}
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
