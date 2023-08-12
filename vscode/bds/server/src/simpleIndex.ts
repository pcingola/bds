import { TextDocument } from "vscode-languageserver-textdocument";
import { Position } from "vscode-languageserver";

export interface Location {
  uri: string;
  range: {
    start: Position;
    end: Position;
  };
}

export class SimpleIndex {
  private index: Map<string, Location[]>;

  constructor() {
    this.index = new Map();
  }

  addSymbol(symbol: string, location: Location): void {
    const locations = this.index.get(symbol) || [];
    locations.push(location);
    this.index.set(symbol, locations);
  }

  getSymbolLocation(symbol: string): Location[] | undefined {
    return this.index.get(symbol);
  }

  clearFile(uri: string): void {
    for (const [symbol, locations] of this.index.entries()) {
      const filteredLocations = locations.filter((loc) => loc.uri !== uri);
      if (filteredLocations.length > 0) {
        this.index.set(symbol, filteredLocations);
      } else {
        this.index.delete(symbol);
      }
    }
  }

  parseAndIndexDocument(document: TextDocument): void {
    this.clearFile(document.uri);
    const classRegex =
      /class\s+([a-zA-Z_]\w*)\s*(?:extends\s*[a-zA-Z_]\w*\s*)?\{/g;
    let match;
    const content = document.getText();

    while ((match = classRegex.exec(content)) !== null) {
      const className = match[1];
      const line = content.substring(0, match.index).split("\n").length - 1;
      const column = match.index - content.lastIndexOf("\n", match.index) - 1;
      const position: Position = { line, character: column };

      this.addSymbol(className, {
        uri: document.uri,
        range: {
          start: position,
          end: position,
        },
      });
    }
  }
}
