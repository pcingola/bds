import {
  Connection,
  RemoteWorkspace,
  WorkspaceFolder,
} from "vscode-languageserver/node";
import { TextDocument } from "vscode-languageserver-textdocument";
import { promises as fs } from "fs";
import { fileURLToPath } from "url";
import { SymbolIndex } from "./symbolIndex";
import { glob } from "glob";
import { DocumentParser } from "./defaultDocumentParser";

type ParsedFile = {
  uri: string;
  data: any;
};

type FileData = {
  path: string;
  content: string;
};
export class WorkspaceIndexer {
  constructor(
    private workspace: RemoteWorkspace,
    private supportsWorkspaceFolders: boolean | undefined,
    private index: SymbolIndex,
    private parser: DocumentParser,
    private connection: Connection
  ) {}

  public async run(): Promise<void> {
    try {
      this.connection.sendNotification(
        "custom/indexingStatus",
        "Indexing started."
      );

      const folders = await this.getFolders();
      const filePaths = this.findPaths(folders);
      const files = await this.readFiles(filePaths);
      const documents = this.buildDocuments(files);
      const parsedFiles = this.parse(documents);
      this.indexFiles(parsedFiles);

      this.connection.sendNotification(
        "custom/indexingStatus",
        "Indexing finished."
      );
    } catch (error) {
      this.connection.sendNotification(
        "custom/showErrorMessage",
        "Indexing failed."
      );
      throw error;
    }
  }

  private async getFolders(): Promise<WorkspaceFolder[]> {
    if (!this.supportsWorkspaceFolders) {
      this.connection.sendNotification(
        "custom/showErrorMessage",
        "Workspace folders are not supported by this extension."
      );
      throw new Error("Workspace folders not supported");
    }

    const folders = await this.workspace.getWorkspaceFolders();

    if (folders === null) {
      this.connection.sendNotification(
        "custom/showErrorMessage",
        "Unable to retrieve workspace folders."
      );
      throw new Error("Failed to retrieve workspace folders");
    }

    if (folders.length === 0) {
      this.connection.sendNotification(
        "custom/showErrorMessage",
        "There are no workspace folders open."
      );
      throw new Error("No workspace folders open");
    }

    return folders;
  }

  private findPaths(folders: WorkspaceFolder[]): string[] {
    return folders
      .map((folder) => {
        const folderPath = fileURLToPath(folder.uri);
        return glob.sync(folderPath + "/**/*.bds");
      })
      .flat();
  }

  private async readFiles(filePaths: string[]): Promise<FileData[]> {
    return Promise.all(
      filePaths.map(async (filePath) => ({
        path: filePath,
        content: await fs.readFile(filePath, "utf8"),
      }))
    );
  }

  private buildDocuments(files: FileData[]): TextDocument[] {
    return files.map((file) =>
      TextDocument.create(file.path, "bds", 1, file.content)
    );
  }

  private parse(documents: TextDocument[]): ParsedFile[] {
    return documents.map((document) => ({
      uri: document.uri,
      data: this.parser.parse(document),
    }));
  }

  private indexFiles(parsedFiles: ParsedFile[]): void {
    parsedFiles.forEach((parsedFile) => {
      this.index.indexDocument(parsedFile.uri, parsedFile.data);
    });
  }
}
