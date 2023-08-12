import { Location } from "./types";

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

  clearFile(file: string): void {
    for (const [symbol, locations] of this.index.entries()) {
      const filteredLocations = locations.filter((loc) => loc.file !== file);
      if (filteredLocations.length > 0) {
        this.index.set(symbol, filteredLocations);
      } else {
        this.index.delete(symbol);
      }
    }
  }

  parseAndIndexFileContent(file: string, content: string): void {
    // Clear previous symbols from this file
    this.clearFile(file);

    // Regex to match class definitions
    const classRegex =
      /class\s+([a-zA-Z_]\w*)\s*(?:extends\s*[a-zA-Z_]\w*\s*)?\{/g;
    let match;

    while ((match = classRegex.exec(content)) !== null) {
      const className = match[1];
      const line = content.substring(0, match.index).split("\n").length;
      const column =
        match.index - content.lastIndexOf("\n", match.index) - 1 + 6;

      this.addSymbol(className, { file, line, column });
    }
    console.log("parseAndIndexFileContent");
    console.log(this.index);
  }
}

export const globalIndex = new SimpleIndex();
