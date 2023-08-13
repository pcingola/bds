import { Location, Position } from "vscode-languageserver/node";
import { TextDocument } from "vscode-languageserver-textdocument";
import { IndexType, SymbolIndex } from "./symbolIndex";

function getLocationByType(
  document: TextDocument,
  position: Position,
  index: SymbolIndex,
  type: IndexType
): Location[] | null {
  const word = getWordAtPosition(document, position);
  if (!word) return null;

  const locations = index.getSymbolLocation(word, type);
  return locations && locations.length > 0 ? locations : null;
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

export { getLocationByType };
