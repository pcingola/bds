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
) {
  if (hasWorkspaceFoldersCapability) {
    workspace.getWorkspaceFolders().then((folders) => {
      folders?.forEach((folder) =>
        indexBDSFilesInWorkspace(folder, symbolindex)
      );
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
  readdirSync(dir).forEach((file) => {
    const filePath = join(dir, file);

    if (statSync(filePath).isDirectory()) {
      findAllBDSFiles(filePath, fileList);
    } else if (extname(file) === ".bds") {
      fileList.push(filePath);
    }
  });

  return fileList;
}
