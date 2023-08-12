import {
  createConnection,
  TextDocuments,
  ProposedFeatures,
  InitializeParams,
  TextDocumentSyncKind,
  WorkspaceFolder,
  DefinitionParams,
  Definition,
  ReferenceParams,
  Location,
} from "vscode-languageserver/node";
import { TextDocument } from "vscode-languageserver-textdocument";

import { DefinitionLogic } from "./definitionLogic";
import { readdirSync, readFileSync, statSync } from "fs";
import { join, extname } from "path";
import { fileURLToPath } from "url";

let connection = createConnection(ProposedFeatures.all);
let documents = new TextDocuments(TextDocument);

let hasWorkspaceFolderCapability = false;
let definitionLogic = new DefinitionLogic();
let indexer = definitionLogic["indexer"];

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

function indexBDSFilesInWorkspace(folder: WorkspaceFolder) {
  const folderUri = fileURLToPath(folder.uri);
  const bdsFiles = findAllBDSFiles(folderUri);
  bdsFiles.forEach((file) => {
    const content = readFileSync(file, "utf8");
    const document = TextDocument.create(file, "bds", 1, content);
    indexer.parseAndIndexDocument(document);
  });
}

function findAllBDSFiles(dir: string, fileList: string[] = []): string[] {
  const files = readdirSync(dir);
  for (const file of files) {
    const filePath = join(dir, file);
    if (statSync(filePath).isDirectory()) {
      fileList = findAllBDSFiles(filePath, fileList);
    } else if (extname(file) === ".bds") {
      fileList.push(filePath);
    }
  }
  return fileList;
}

documents.onDidChangeContent((change) => {
  indexer.parseAndIndexDocument(change.document);
});

connection.onDefinition(
  (textDocumentPosition: DefinitionParams): Definition | null => {
    const document = documents.get(textDocumentPosition.textDocument.uri);
    return document
      ? definitionLogic.getDefinition(document, textDocumentPosition.position)
      : null;
  }
);

connection.onReferences(
  (textDocumentPosition: ReferenceParams): Location[] | null => {
    const document = documents.get(textDocumentPosition.textDocument.uri);
    return document
      ? definitionLogic.getReferences(document, textDocumentPosition.position)
      : null;
  }
);

documents.listen(connection);
connection.listen();
