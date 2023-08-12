"use strict";
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    var desc = Object.getOwnPropertyDescriptor(m, k);
    if (!desc || ("get" in desc ? !m.__esModule : desc.writable || desc.configurable)) {
      desc = { enumerable: true, get: function() { return m[k]; } };
    }
    Object.defineProperty(o, k2, desc);
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __importStar = (this && this.__importStar) || function (mod) {
    if (mod && mod.__esModule) return mod;
    var result = {};
    if (mod != null) for (var k in mod) if (k !== "default" && Object.prototype.hasOwnProperty.call(mod, k)) __createBinding(result, mod, k);
    __setModuleDefault(result, mod);
    return result;
};
Object.defineProperty(exports, "__esModule", { value: true });
const node_1 = require("vscode-languageserver/node");
const vscode_languageserver_textdocument_1 = require("vscode-languageserver-textdocument");
const definitionLogic_1 = require("./definitionLogic");
const fs = __importStar(require("fs"));
const path = __importStar(require("path"));
const url = __importStar(require("url"));
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
connection.onInitialized(() => {
    if (hasWorkspaceFolderCapability) {
        connection.workspace.getWorkspaceFolders().then((folders) => {
            if (folders) {
                folders.forEach((folder) => {
                    indexBDSFilesInWorkspace(folder);
                });
            }
        });
    }
});
function indexBDSFilesInWorkspace(folder) {
    const folderUri = url.fileURLToPath(folder.uri); // Convert the URI to a path
    const bdsFiles = findAllBDSFiles(folderUri);
    bdsFiles.forEach((file) => {
        const content = fs.readFileSync(file, "utf8");
        const document = vscode_languageserver_textdocument_1.TextDocument.create(file, "bds", 1, content);
        indexer.parseAndIndexDocument(document);
    });
}
function findAllBDSFiles(dir, fileList = []) {
    const files = fs.readdirSync(dir);
    files.forEach((file) => {
        if (fs.statSync(path.join(dir, file)).isDirectory()) {
            fileList = findAllBDSFiles(path.join(dir, file), fileList);
        }
        else if (path.extname(file) === ".bds") {
            fileList.push(path.join(dir, file));
        }
    });
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
