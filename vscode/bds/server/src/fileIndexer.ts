import { RemoteWorkspace, WorkspaceFolder } from "vscode-languageserver/node";
import { readFileSync, readdirSync, statSync } from "fs";
import { join, extname } from "path";
import { fileURLToPath } from "url";
import { TextDocument } from "vscode-languageserver-textdocument";
import { SymbolIndex } from "./symbolIndex";

export function indexAllBDSFiles(
  workspace: RemoteWorkspace,
  hasWorkspaceFoldersCapability: boolean | undefined,
  symbolindex: SymbolIndex
) {
  if (hasWorkspaceFoldersCapability) {
    workspace.getWorkspaceFolders().then((folders) => {
      folders?.forEach((folder) => {
        indexBDSFilesInWorkspace(folder, symbolindex);
      });
    });
  }
}

function indexBDSFilesInWorkspace(folder: WorkspaceFolder, index: SymbolIndex) {
  const folderUri = fileURLToPath(folder.uri);
  const bdsFiles = findAllBDSFiles(folderUri);
  bdsFiles.forEach((file) => {
    const content = readFileSync(file, "utf8");
    const document = TextDocument.create(file, "bds", 1, content);
    index.parseAndIndexDocument(document);
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
