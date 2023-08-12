import { TextDocument } from "vscode-languageserver-textdocument";
import { Location, Position } from "vscode-languageserver";

export enum IndexType {
  Definition,
  Reference,
}

export class SimpleIndex {
  private indexes: Record<IndexType, Map<string, Location[]>> = {
    [IndexType.Definition]: new Map(),
    [IndexType.Reference]: new Map(),
  };

  private addLocationToIndex(
    indexType: IndexType,
    symbol: string,
    location: Location
  ): void {
    const index = this.indexes[indexType];
    const locations = index.get(symbol) || [];
    locations.push(location);
    index.set(symbol, locations);
  }

  public addSymbol(
    symbol: string,
    location: Location,
    indexType: IndexType
  ): void {
    this.addLocationToIndex(indexType, symbol, location);
  }

  public getSymbolLocation(
    symbol: string,
    indexType: IndexType
  ): Location[] | undefined {
    return this.indexes[indexType].get(symbol);
  }

  public clearFile(uri: string): void {
    Object.values(this.indexes).forEach(this.clearIndexForFile.bind(this, uri));
  }

  private clearIndexForFile(uri: string, index: Map<string, Location[]>): void {
    for (const [symbol, locations] of index.entries()) {
      const updatedLocations = locations.filter((loc) => loc.uri !== uri);
      if (updatedLocations.length) {
        index.set(symbol, updatedLocations);
      } else {
        index.delete(symbol);
      }
    }
  }

  public parseAndIndexDocument(document: TextDocument): void {
    this.clearFile(document.uri);
    const content = document.getText();

    this.processRegex(
      content,
      /class\s+([a-zA-Z_]\w*)\s*(?:extends\s*[a-zA-Z_]\w*\s*)?\{/g,
      this.handleClassDefinition.bind(this, document.uri)
    );
    this.processRegex(
      content,
      /new\s+([a-zA-Z_]\w*)\s*\(/g,
      this.handleClassReference.bind(this, document.uri)
    );
  }

  private processRegex(
    content: string,
    regex: RegExp,
    callback: (match: RegExpExecArray) => void
  ): void {
    let match;
    while ((match = regex.exec(content)) !== null) {
      callback(match);
    }
  }

  private handleClassDefinition(uri: string, match: RegExpExecArray): void {
    this.handleMatch(uri, match, IndexType.Definition);
  }

  private handleClassReference(uri: string, match: RegExpExecArray): void {
    this.handleMatch(uri, match, IndexType.Reference);
  }

  private handleMatch(
    uri: string,
    match: RegExpExecArray,
    indexType: IndexType
  ): void {
    const className = match[1];
    const content = match.input;
    const line = content.substring(0, match.index).split("\n").length - 1;
    const column = match.index - content.lastIndexOf("\n", match.index) - 1;
    const position: Position = { line, character: column };

    this.addSymbol(
      className,
      {
        uri: uri,
        range: { start: position, end: position },
      },
      indexType
    );
  }
}
