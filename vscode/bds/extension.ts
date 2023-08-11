import * as vscode from "vscode";

export function activate(context: vscode.ExtensionContext): void {
  context.subscriptions.push(
    vscode.languages.registerDefinitionProvider(
      "bds",
      new (class implements vscode.DefinitionProvider {
        provideDefinition(
          document: vscode.TextDocument,
          position: vscode.Position,
          token: vscode.CancellationToken
        ): vscode.ProviderResult<vscode.Definition> {
          const word = document.getText(
            document.getWordRangeAtPosition(position)
          );

          // Check if the user is hovering over a class instantiation like "new A()"
          if (document.lineAt(position.line).text.includes(`new ${word}()`)) {
            const regex = new RegExp(`class\\s+${word}\\s*{`);
            for (let i = 0; i < document.lineCount; i++) {
              const line = document.lineAt(i);
              const match = line.text.match(regex);
              if (match) {
                const start = match.index || 0;
                return new vscode.Location(
                  document.uri,
                  new vscode.Range(
                    new vscode.Position(i, start),
                    new vscode.Position(i, start + word.length)
                  )
                );
              }
            }
          } else {
            // Search for the variable or class declaration
            const regex = new RegExp(`\\b${word}\\b\\s*=[^=]|class\\s+${word}`);
            for (let i = position.line; i >= 0; i--) {
              const line = document.lineAt(i);
              const match = line.text.match(regex);
              if (match) {
                const start = match.index || 0;
                return new vscode.Location(
                  document.uri,
                  new vscode.Range(
                    new vscode.Position(i, start),
                    new vscode.Position(i, start + word.length)
                  )
                );
              }
            }
          }
          return null;
        }
      })()
    )
  );
}

export function deactivate(): void {}
