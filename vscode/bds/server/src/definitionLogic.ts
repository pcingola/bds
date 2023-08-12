import {
  TextDocumentPositionParams,
  Definition,
  Location,
  Range,
  Position,
} from "vscode-languageserver/node";
import { TextDocument } from "vscode-languageserver-textdocument";
import { SimpleIndex } from "./simpleIndex";

export class DefinitionLogic {
  private indexer: SimpleIndex;

  constructor() {
    this.indexer = new SimpleIndex();
  }

  public getDefinition(
    document: TextDocument,
    position: Position
  ): Definition | null {
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
