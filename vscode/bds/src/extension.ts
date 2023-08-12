import * as vscode from "vscode";
import { DefinitionProvider } from "./definitionProvider";
import { globalIndex } from "./simpleIndex";

export function activate(context: vscode.ExtensionContext): void {
  registerDefinitionProvider(context);
  listenForFileChanges(context);
  indexAllWorkspaceFiles();
}

function registerDefinitionProvider(context: vscode.ExtensionContext): void {
  context.subscriptions.push(
    vscode.languages.registerDefinitionProvider("bds", new DefinitionProvider())
  );
}

function listenForFileChanges(context: vscode.ExtensionContext): void {
  context.subscriptions.push(
    vscode.workspace.onDidSaveTextDocument(updateIndexForDocument)
  );
}

function updateIndexForDocument(document: vscode.TextDocument): void {
  globalIndex.parseAndIndexFileContent(document.uri.fsPath, document.getText());
}

function indexAllWorkspaceFiles(): void {
  if (!vscode.workspace.workspaceFolders) return;

  for (const folder of vscode.workspace.workspaceFolders) {
    indexFilesInFolder(folder);
  }
}

function indexFilesInFolder(folder: vscode.WorkspaceFolder): void {
  const pattern = new vscode.RelativePattern(folder, "**/*.bds");
  vscode.workspace.findFiles(pattern, null).then((files) => {
    for (const file of files) {
      vscode.workspace.openTextDocument(file).then(updateIndexForDocument);
    }
  });
}

export function deactivate(): void {}
