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
  // The server is implemented in a separate file, which we specify here
  let serverModule = context.asAbsolutePath(
    path.join("..", "server", "out", "server.js")
  );

  // The debug options for the server
  let debugOptions = { execArgv: ["--nolazy", "--inspect=6009"] };

  // If the extension is launched in debug mode, then the debug server options are used
  // Otherwise the run options are used
  let serverOptions: ServerOptions = {
    run: { module: serverModule, transport: TransportKind.ipc },
    debug: {
      module: serverModule,
      transport: TransportKind.ipc,
      options: debugOptions,
    },
  };

  // Options to control the language client
  let clientOptions: LanguageClientOptions = {
    // Register the server for bds documents
    documentSelector: [{ scheme: "file", language: "bds" }],
    synchronize: {
      fileEvents: vscode.workspace.createFileSystemWatcher("**/*.bds"),
    },
  };

  // Create the language client and start the client.
  client = new LanguageClient(
    "bdsLanguageServer",
    "BDS Language Server",
    serverOptions,
    clientOptions
  );

  // Start the client. This will also launch the server
  client.start();
}

export function deactivate(): Thenable<void> | undefined {
  if (!client) {
    return undefined;
  }
  return client.stop();
}
