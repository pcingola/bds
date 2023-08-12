import {
  createConnection,
  TextDocuments,
  ProposedFeatures,
  InitializeParams,
  TextDocumentSyncKind,
  InitializeResult,
} from "vscode-languageserver/node";
import { TextDocument } from "vscode-languageserver-textdocument";

import { DefinitionLogic } from "./definitionLogic";
import { SimpleIndex } from "./simpleIndex"; // Importing SimpleIndex for indexing logic

// Create a connection for the server. The connection uses Node's IPC as a transport.
let connection = createConnection(ProposedFeatures.all);

// Create a manager for open text documents.
let documents: TextDocuments<TextDocument> = new TextDocuments(TextDocument);

let hasWorkspaceFolderCapability: boolean = false;

let definitionLogic: DefinitionLogic = new DefinitionLogic();
let indexer: SimpleIndex = definitionLogic["indexer"]; // Accessing indexer from DefinitionLogic

connection.onInitialize((params: InitializeParams) => {
  let capabilities = params.capabilities;

  // Check if the client supports workspace folders
  hasWorkspaceFolderCapability = !!(
    capabilities.workspace && !!capabilities.workspace.workspaceFolders
  );

  const result: InitializeResult = {
    capabilities: {
      textDocumentSync: TextDocumentSyncKind.Incremental,
      // Tell the client that the server supports Go To Definition
      definitionProvider: true,
    },
  };
  return result;
});

// The content of a text document has changed. This event is emitted
// when the text document is first opened or when its content has changed.
documents.onDidChangeContent((change) => {
  // Update the indexer when document content changes
  indexer.parseAndIndexDocument(change.document);
});

// Register the definition provider
connection.onDefinition((textDocumentPosition: any) => {
  const document = documents.get(textDocumentPosition.textDocument.uri);
  if (!document) {
    return null;
  }
  return definitionLogic.getDefinition(document, textDocumentPosition.position);
});

// Make the text document manager listen on the connection
documents.listen(connection);

// Listen on the connection
connection.listen();
