import { Definition, Position } from "vscode-languageserver/node";
import { TextDocument } from "vscode-languageserver-textdocument";
import { SimpleIndex } from "./simpleIndex";

export class DefinitionLogic {
  private indexer: SimpleIndex = new SimpleIndex();

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
    const regex = /\b(\w+)\b/g;
    let match;
    while ((match = regex.exec(line))) {
      if (
        match.index <= position.character &&
        regex.lastIndex >= position.character
      ) {
        return match[1];
      }
    }
    return null;
  }
}
