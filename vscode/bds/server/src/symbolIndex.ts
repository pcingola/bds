import { TextDocument } from "vscode-languageserver-textdocument";
import { Location } from "vscode-languageserver";
import {
  DefaultDocumentParser,
  DocumentParser,
  ParsedSymbol,
} from "./defaultDocumentParser";

export enum IndexType {
  Definition,
  Reference,
}

export class SymbolIndex {
  private indexes: Record<IndexType, Map<string, Location[]>> = {
    [IndexType.Definition]: new Map(),
    [IndexType.Reference]: new Map(),
  };

  constructor(private parser: DocumentParser = new DefaultDocumentParser()) {}

  public addSymbol(
    symbol: string,
    location: Location,
    indexType: IndexType
  ): void {
    const index = this.indexes[indexType];
    const locations = index.get(symbol) || [];
    locations.push(location);
    index.set(symbol, locations);
  }

  public getSymbolLocation(
    symbol: string,
    indexType: IndexType
  ): Location[] | undefined {
    return this.indexes[indexType].get(symbol);
  }

  public clearFile(uri: string): void {
    Object.values(this.indexes).forEach((index) =>
      this.clearIndexForFile(uri, index)
    );
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

  public indexDocument(
    documentUri: string,
    parsedResults: ParsedSymbol[]
  ): void {
    this.clearFile(documentUri);
    for (const result of parsedResults) {
      this.addSymbol(
        result.symbol,
        {
          uri: documentUri,
          range: { start: result.position, end: result.position },
        },
        result.type
      );
    }
  }
}
