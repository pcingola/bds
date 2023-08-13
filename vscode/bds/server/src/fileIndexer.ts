import { RemoteWorkspace, WorkspaceFolder } from "vscode-languageserver/node";
import { TextDocument } from "vscode-languageserver-textdocument";
import { readFileSync, readdirSync, statSync } from "fs";
import { join, extname } from "path";
import { fileURLToPath } from "url";
import { SymbolIndex } from "./symbolIndex";

export function indexAllBDSFiles(
  workspace: RemoteWorkspace,
  hasWorkspaceFoldersCapability: boolean | undefined,
  symbolindex: SymbolIndex
): void {
  if (hasWorkspaceFoldersCapability) {
    workspace.getWorkspaceFolders().then(handleWorkspaceFolders(symbolindex));
  }
}

function handleWorkspaceFolders(
  symbolindex: SymbolIndex
): (folders: WorkspaceFolder[] | null) => void {
  return function (folders: WorkspaceFolder[] | null): void {
    folders?.forEach(processEachFolder(symbolindex));
  };
}

function processEachFolder(
  symbolindex: SymbolIndex
): (folder: WorkspaceFolder) => void {
  return function (folder: WorkspaceFolder): void {
    const folderUri = fileURLToPath(folder.uri);
    const bdsFiles = findBDSFilesInDirectory(folderUri);
    bdsFiles.forEach(indexEachBDSFile(symbolindex));
  };
}

function indexEachBDSFile(index: SymbolIndex): (file: string) => void {
  return function (file: string): void {
    const content = readFileSync(file, "utf8");
    const document = TextDocument.create(file, "bds", 1, content);
    index.parseAndIndexDocument(document);
  };
}

function findBDSFilesInDirectory(
  dir: string,
  fileList: string[] = []
): string[] {
  readdirSync(dir).forEach(determineIfDirOrBDSFile(dir, fileList));
  return fileList;
}

function determineIfDirOrBDSFile(
  dir: string,
  fileList: string[]
): (file: string) => void {
  return function (file: string): void {
    const filePath = join(dir, file);
    if (statSync(filePath).isDirectory()) {
      findBDSFilesInDirectory(filePath, fileList);
    } else if (extname(file) === ".bds") {
      fileList.push(filePath);
    }
  };
}
