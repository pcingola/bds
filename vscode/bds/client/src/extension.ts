import * as vscode from "vscode";
import * as path from "path";
import {
  LanguageClient,
  LanguageClientOptions,
  ServerOptions,
  TransportKind,
} from "vscode-languageclient/node";

let client: LanguageClient;

export function activate(context: vscode.ExtensionContext) {
  let serverModule = context.asAbsolutePath(
    path.join("server", "out", "server.js")
  );

  let debugOptions = { execArgv: ["--nolazy", "--inspect=6009"] };

  let serverOptions: ServerOptions = {
    run: { module: serverModule, transport: TransportKind.ipc },
    debug: {
      module: serverModule,
      transport: TransportKind.ipc,
      options: debugOptions,
    },
  };

  let clientOptions: LanguageClientOptions = {
    documentSelector: [{ scheme: "file", language: "bds" }],
    synchronize: {
      fileEvents: vscode.workspace.createFileSystemWatcher("**/*.bds"),
    },
  };

  client = new LanguageClient(
    "bdsLanguageServer",
    "BDS Language Server",
    serverOptions,
    clientOptions
  );
  client.onNotification("custom/showErrorMessage", (message) => {
    vscode.window.showErrorMessage(message);
  });
  client.onNotification("custom/indexingStatus", (message) => {
    vscode.window.showInformationMessage(message);
  });

  client.start();
}

export function deactivate(): Thenable<void> | undefined {
  if (!client) {
    return undefined;
  }
  return client.stop();
}
