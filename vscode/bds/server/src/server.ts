import {
  createConnection,
  TextDocuments,
  ProposedFeatures,
  InitializeParams,
  TextDocumentSyncKind,
  DefinitionParams,
  Definition,
  ReferenceParams,
  Location,
} from "vscode-languageserver/node";
import { TextDocument } from "vscode-languageserver-textdocument";

import { globalIndex } from "./symbolIndex";
import { getDefinition, getReferences } from "./definitionLogic";
import { indexBDSFilesInWorkspace } from "./fileIndexer";

let connection = createConnection(ProposedFeatures.all);
let documents = new TextDocuments(TextDocument);

let hasWorkspaceFolderCapability = false;

connection.onInitialize((params: InitializeParams) => {
  hasWorkspaceFolderCapability =
    !!params.capabilities.workspace?.workspaceFolders;
  return {
    capabilities: {
      textDocumentSync: TextDocumentSyncKind.Incremental,
      definitionProvider: true,
      referencesProvider: true,
    },
  };
});

connection.onInitialized(() => {
  if (hasWorkspaceFolderCapability) {
    connection.workspace.getWorkspaceFolders().then((folders) => {
      folders?.forEach(indexBDSFilesInWorkspace);
    });
  }
});

documents.onDidChangeContent((change) => {
  globalIndex.parseAndIndexDocument(change.document);
});

connection.onDefinition(
  (textDocumentPosition: DefinitionParams): Definition | null => {
    const document = documents.get(textDocumentPosition.textDocument.uri);
    return document
      ? getDefinition(document, textDocumentPosition.position)
      : null;
  }
);

connection.onReferences(
  (textDocumentPosition: ReferenceParams): Location[] | null => {
    const document = documents.get(textDocumentPosition.textDocument.uri);
    return document
      ? getReferences(document, textDocumentPosition.position)
      : null;
  }
);

documents.listen(connection);
connection.listen();
