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
exports.deactivate = exports.activate = void 0;
const vscode = __importStar(require("vscode"));
const definitionProvider_1 = require("./definitionProvider");
const simpleIndex_1 = require("./simpleIndex");
function activate(context) {
    registerDefinitionProvider(context);
    listenForFileChanges(context);
    indexAllWorkspaceFiles();
}
exports.activate = activate;
function registerDefinitionProvider(context) {
    context.subscriptions.push(vscode.languages.registerDefinitionProvider("bds", new definitionProvider_1.DefinitionProvider()));
}
function listenForFileChanges(context) {
    context.subscriptions.push(vscode.workspace.onDidSaveTextDocument(updateIndexForDocument));
}
function updateIndexForDocument(document) {
    simpleIndex_1.globalIndex.parseAndIndexFileContent(document.uri.fsPath, document.getText());
}
function indexAllWorkspaceFiles() {
    if (!vscode.workspace.workspaceFolders)
        return;
    for (const folder of vscode.workspace.workspaceFolders) {
        indexFilesInFolder(folder);
    }
    console.log("indexAllWorkspaceFiles");
    console.log(simpleIndex_1.globalIndex);
}
function indexFilesInFolder(folder) {
    const pattern = new vscode.RelativePattern(folder, "**/*.bds");
    vscode.workspace.findFiles(pattern, null).then((files) => {
        for (const file of files) {
            vscode.workspace.openTextDocument(file).then(updateIndexForDocument);
        }
    });
}
function deactivate() { }
exports.deactivate = deactivate;
//# sourceMappingURL=extension.js.map