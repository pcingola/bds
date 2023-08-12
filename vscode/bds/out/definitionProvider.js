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
exports.DefinitionProvider = void 0;
const vscode = __importStar(require("vscode"));
const simpleIndex_1 = require("./simpleIndex");
class DefinitionProvider {
    provideDefinition(document, position) {
        const word = document.getText(document.getWordRangeAtPosition(position));
        return this.isClassInstantiation(document, position, word)
            ? this.findClassDefinition(document, word)
            : this.findVarOrClassDeclaration(document, position, word);
    }
    isClassInstantiation(document, position, word) {
        return document.lineAt(position.line).text.includes(`new ${word}()`);
    }
    findClassDefinition(document, word) {
        const locations = simpleIndex_1.globalIndex.getSymbolLocation(word);
        if (!locations || locations.length === 0) {
            return null;
        }
        return locations.map((location) => {
            const uri = location.file === document.uri.fsPath
                ? document.uri
                : vscode.Uri.file(location.file);
            return this.createLocation(uri, word, location.line - 1, location.column);
        });
    }
    findVarOrClassDeclaration(document, position, word) {
        const regex = new RegExp(`\\b${word}\\b\\s*=[^=]|class\\s+${word}`);
        for (let i = position.line; i >= 0; i--) {
            const line = document.lineAt(i);
            const match = line.text.match(regex);
            if (match) {
                return this.createLocation(document.uri, word, i, match.index || 0);
            }
        }
        return null;
    }
    createLocation(uri, word, lineNum, start) {
        const range = new vscode.Range(new vscode.Position(lineNum, start), new vscode.Position(lineNum, start + word.length));
        return new vscode.Location(uri, range);
    }
}
exports.DefinitionProvider = DefinitionProvider;
//# sourceMappingURL=definitionProvider.js.map