import * as vscode from "vscode";
import { globalIndex } from "./simpleIndex";

export class DefinitionProvider implements vscode.DefinitionProvider {
  provideDefinition(
    document: vscode.TextDocument,
    position: vscode.Position
  ): vscode.ProviderResult<vscode.Definition> {
    const word = document.getText(document.getWordRangeAtPosition(position));
    return this.isClassInstantiation(document, position, word)
      ? this.findClassDefinition(document, word)
      : this.findVarOrClassDeclaration(document, position, word);
  }

  private isClassInstantiation(
    document: vscode.TextDocument,
    position: vscode.Position,
    word: string
  ): boolean {
    return document.lineAt(position.line).text.includes(`new ${word}()`);
  }

  private findClassDefinition(
    document: vscode.TextDocument,
    word: string
  ): vscode.Location[] | null {
    const locations = globalIndex.getSymbolLocation(word);
    if (!locations || locations.length === 0) {
      return null;
    }

    return locations.map((location) => {
      const uri =
        location.file === document.uri.fsPath
          ? document.uri
          : vscode.Uri.file(location.file);
      return this.createLocation(uri, word, location.line - 1, location.column);
    });
  }

  private findVarOrClassDeclaration(
    document: vscode.TextDocument,
    position: vscode.Position,
    word: string
  ): vscode.Location | null {
    const regex = new RegExp(`\\b${word}\\b\\s*=[^=]|class\\s+${word}`);
    for (let i = position.line; i >= 0; i--) {
      const line = document.lineAt(i);
      const match = line.text.match(regex);
      if (match) {
        return this.createLocation(document.uri, word, i, match.index || 0);
      }
    }
    return null;
  }

  private createLocation(
    uri: vscode.Uri,
    word: string,
    lineNum: number,
    start: number
  ): vscode.Location {
    const range = new vscode.Range(
      new vscode.Position(lineNum, start),
      new vscode.Position(lineNum, start + word.length)
    );
    return new vscode.Location(uri, range);
  }
}
