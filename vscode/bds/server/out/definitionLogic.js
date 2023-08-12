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
        if (!word)
            return null;
        const locations = this.indexer.getSymbolLocation(word);
        if (locations && locations.length > 0) {
            return locations[0];
        }
        return null;
    }
    getWordAtPosition(document, position) {
        const line = document.getText({
            start: { line: position.line, character: 0 },
            end: { line: position.line, character: Number.MAX_VALUE },
        });
        const match = line.match(/\b(\w+)\b/g);
        if (match) {
            for (const word of match) {
                const startIndex = line.indexOf(word);
                const endIndex = startIndex + word.length;
                if (startIndex <= position.character &&
                    endIndex >= position.character) {
                    return word;
                }
            }
        }
        return null;
    }
}
exports.DefinitionLogic = DefinitionLogic;
