import { RemoteWorkspace, WorkspaceFolder } from "vscode-languageserver/node";
import { TextDocument } from "vscode-languageserver-textdocument";
import { readFileSync, readdirSync, statSync } from "fs";
import { join, extname } from "path";
import { fileURLToPath } from "url";
import { SymbolIndex } from "./symbolIndex";

export function indexAllFilesInWorkspace(
  workspace: RemoteWorkspace,
  supportsWorkspaceFolders: boolean | undefined,
  symbols: SymbolIndex
): void {
  if (supportsWorkspaceFolders) {
    workspace.getWorkspaceFolders().then(processWorkspaceFolders(symbols));
  }
}

function processWorkspaceFolders(
  symbols: SymbolIndex
): (folders: WorkspaceFolder[] | null) => void {
  return function (folders: WorkspaceFolder[] | null): void {
    folders?.forEach(indexFilesInFolder(symbols));
  };
}

function indexFilesInFolder(
  symbols: SymbolIndex
): (folder: WorkspaceFolder) => void {
  return function (folder: WorkspaceFolder): void {
    const folderPath = fileURLToPath(folder.uri);
    const targetFiles = findAllFilesWithExtensionInDir(folderPath, ".bds");
    targetFiles.forEach(addFileToSymbolIndex(symbols));
  };
}

function addFileToSymbolIndex(index: SymbolIndex): (filePath: string) => void {
  return function (filePath: string): void {
    const fileContent = readFileSync(filePath, "utf8");
    const document = TextDocument.create(filePath, "bds", 1, fileContent);
    index.parseAndIndexDocument(document);
  };
}

function findAllFilesWithExtensionInDir(
  directory: string,
  extension: string,
  filesFound: string[] = []
): string[] {
  readdirSync(directory).forEach(
    determineFileType(directory, filesFound, extension)
  );
  return filesFound;
}

function determineFileType(
  directory: string,
  filesFound: string[],
  extension: string
): (filename: string) => void {
  return function (filename: string): void {
    const fullPath = join(directory, filename);
    if (statSync(fullPath).isDirectory()) {
      findAllFilesWithExtensionInDir(fullPath, extension, filesFound);
    } else if (extname(filename) === extension) {
      filesFound.push(fullPath);
    }
  };
}
