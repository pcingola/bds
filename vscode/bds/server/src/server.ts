import {
  createConnection,
  TextDocuments,
  ProposedFeatures,
  InitializeParams,
  TextDocumentSyncKind,
} from "vscode-languageserver/node";
import { TextDocument } from "vscode-languageserver-textdocument";
import { DefinitionLogic } from "./definitionLogic";

let connection = createConnection(ProposedFeatures.all);
let documents: TextDocuments<TextDocument> = new TextDocuments(TextDocument);

let hasWorkspaceFolderCapability = false;
let definitionLogic = new DefinitionLogic();
let indexer = definitionLogic["indexer"];

connection.onInitialize((params: InitializeParams) => {
  hasWorkspaceFolderCapability = !!(
    params.capabilities.workspace &&
    params.capabilities.workspace.workspaceFolders
  );
  return {
    capabilities: {
      textDocumentSync: TextDocumentSyncKind.Incremental,
      definitionProvider: true,
    },
  };
});

documents.onDidChangeContent((change) => {
  indexer.parseAndIndexDocument(change.document);
});

connection.onDefinition((textDocumentPosition: any) => {
  const document = documents.get(textDocumentPosition.textDocument.uri);
  return document
    ? definitionLogic.getDefinition(document, textDocumentPosition.position)
    : null;
});

documents.listen(connection);
connection.listen();
