import {
  createConnection,
  TextDocuments,
  ProposedFeatures,
  InitializeParams,
  TextDocumentSyncKind,
  WorkspaceFolder,
} from "vscode-languageserver/node";
import { TextDocument } from "vscode-languageserver-textdocument";
import { DefinitionLogic } from "./definitionLogic";
import * as fs from "fs";
import * as path from "path";
import * as url from "url";

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

connection.onInitialized(() => {
  if (hasWorkspaceFolderCapability) {
    connection.workspace.getWorkspaceFolders().then((folders) => {
      if (folders) {
        folders.forEach((folder) => {
          indexBDSFilesInWorkspace(folder);
        });
      }
    });
  }
});

function indexBDSFilesInWorkspace(folder: WorkspaceFolder) {
  const folderUri = url.fileURLToPath(folder.uri); // Convert the URI to a path
  const bdsFiles = findAllBDSFiles(folderUri);
  bdsFiles.forEach((file) => {
    const content = fs.readFileSync(file, "utf8");
    const document = TextDocument.create(file, "bds", 1, content);
    indexer.parseAndIndexDocument(document);
  });
}

function findAllBDSFiles(dir: string, fileList: string[] = []): string[] {
  const files = fs.readdirSync(dir);
  files.forEach((file) => {
    if (fs.statSync(path.join(dir, file)).isDirectory()) {
      fileList = findAllBDSFiles(path.join(dir, file), fileList);
    } else if (path.extname(file) === ".bds") {
      fileList.push(path.join(dir, file));
    }
  });
  return fileList;
}

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
