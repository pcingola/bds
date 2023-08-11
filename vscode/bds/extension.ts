import * as vscode from "vscode";

export function activate(context: vscode.ExtensionContext): void {
  context.subscriptions.push(
    vscode.languages.registerHoverProvider("bds", {
      provideHover(
        document: vscode.TextDocument,
        position: vscode.Position,
        token: vscode.CancellationToken
      ): vscode.ProviderResult<vscode.Hover> {
        // Your logic to determine hover content
        const hoverText = "Your Hover Content Here";

        return new vscode.Hover(hoverText);
      },
    })
  );
}

export function deactivate(): void {}
