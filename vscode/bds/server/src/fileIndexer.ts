import { RemoteWorkspace, WorkspaceFolder } from "vscode-languageserver/node";
import { TextDocument } from "vscode-languageserver-textdocument";
import { promises as fs } from "fs";
import { fileURLToPath } from "url";
import { SymbolIndex } from "./symbolIndex";
import { glob } from "glob";

export async function indexAllFilesInWorkspace(
  workspace: RemoteWorkspace,
  supportsWorkspaceFolders: boolean | undefined,
  index: SymbolIndex
): Promise<void> {
  if (supportsWorkspaceFolders) {
    const folders = await workspace.getWorkspaceFolders();
    if (folders) {
      for (const folder of folders) {
        await indexFilesInFolder(folder, index);
      }
    }
  }
}

async function indexFilesInFolder(
  folder: WorkspaceFolder,
  index: SymbolIndex
): Promise<void> {
  const folderPath = fileURLToPath(folder.uri);
  const files = glob.sync(folderPath + "/**/*.bds");

  for (const filePath of files) {
    const fileContent = await fs.readFile(filePath, "utf8");
    const document = TextDocument.create(filePath, "bds", 1, fileContent);
    index.parseAndIndexDocument(document);
  }
}
