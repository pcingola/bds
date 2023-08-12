"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.DefinitionLogic = void 0;
const simpleIndex_1 = require("./simpleIndex");
class DefinitionLogic {
    constructor() {
        this.indexer = new simpleIndex_1.SimpleIndex();
    }
    getDefinition(document, position) {
        const word = this.getWordAtPosition(document, position);
        if (!word) {
            return null;
        }
        // Use the indexer to find the definition of the word
        const locations = this.indexer.getSymbolLocation(word);
        if (!locations || locations.length === 0) {
            return null;
        }
        // For simplicity, we're returning the first location if there are multiple.
        // You can modify this to return all locations if necessary.
        return locations[0];
    }
    getWordAtPosition(document, position) {
        const line = document.getText({
            start: { line: position.line, character: 0 },
            end: { line: position.line, character: Number.MAX_VALUE },
        });
        const regex = /\b(\w+)\b/g;
        let match;
        while ((match = regex.exec(line))) {
            if (match.index <= position.character &&
                regex.lastIndex >= position.character) {
                return match[1];
            }
        }
        return null;
    }
}
exports.DefinitionLogic = DefinitionLogic;
