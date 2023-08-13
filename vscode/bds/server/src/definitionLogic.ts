import { Definition, Location, Position } from "vscode-languageserver/node";
import { TextDocument } from "vscode-languageserver-textdocument";
import { IndexType, SymbolIndex } from "./symbolIndex";

function getDefinition(
  document: TextDocument,
  position: Position,
  index: SymbolIndex
): Definition | null {
  const word = getWordAtPosition(document, position);
  if (!word) return null;

  const locations = index.getSymbolLocation(word, IndexType.Definition);
  if (locations && locations.length > 0) {
    return locations;
  }
  return null;
}

function getReferences(
  document: TextDocument,
  position: Position,
  index: SymbolIndex
): Location[] | null {
  const word = getWordAtPosition(document, position);
  if (!word) return null;

  const locations = index.getSymbolLocation(word, IndexType.Reference);
  if (locations && locations.length > 0) {
    return locations;
  }
  return null;
}

function getWordAtPosition(
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

      if (startIndex <= position.character && endIndex >= position.character) {
        return word;
      }
    }
  }

  return null;
}

export { getDefinition, getReferences };
