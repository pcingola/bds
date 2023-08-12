import { Definition, Position } from "vscode-languageserver/node";
import { TextDocument } from "vscode-languageserver-textdocument";
import { SimpleIndex } from "./simpleIndex";

export class DefinitionLogic {
  private indexer = new SimpleIndex();

  public getDefinition(
    document: TextDocument,
    position: Position
  ): Definition | null {
    const word = this.getWordAtPosition(document, position);
    if (!word) return null;

    const locations = this.indexer.getSymbolLocation(word);
    if (locations && locations.length > 0) {
      return locations[0];
    }
    return null;
  }

  private getWordAtPosition(
    document: TextDocument,
    position: Position
  ): string | null {
    const line = document.getText({
      start: { line: position.line, character: 0 },
      end: { line: position.line, character: Number.MAX_VALUE },
    });

    const match = line.match(/\b(\w+)\b/g);
    if (match) {
      for (const word of match) {
        const startIndex = line.indexOf(word);
        const endIndex = startIndex + word.length;

        if (
          startIndex <= position.character &&
          endIndex >= position.character
        ) {
          return word;
        }
      }
    }

    return null;
  }
}
