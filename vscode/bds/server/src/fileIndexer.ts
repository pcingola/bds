import { RemoteWorkspace, WorkspaceFolder } from "vscode-languageserver/node";
import { TextDocument } from "vscode-languageserver-textdocument";
import { promises as fs } from "fs";
import { fileURLToPath } from "url";
import { SymbolIndex } from "./symbolIndex";
import { glob } from "glob";
import { DocumentParser } from "./defaultDocumentParser";

type ParsedFiles = { [uri: string]: any };

export class WorkspaceIndexer {
  private workspace: RemoteWorkspace;
  private supportsWorkspaceFolders: boolean | undefined;
  private index: SymbolIndex;
  private parser: DocumentParser;

  constructor(
    workspace: RemoteWorkspace,
    supportsWorkspaceFolders: boolean | undefined,
    index: SymbolIndex,
    parser: DocumentParser
  ) {
    this.workspace = workspace;
    this.supportsWorkspaceFolders = supportsWorkspaceFolders;
    this.index = index;
    this.parser = parser;
  }

  public async run(): Promise<void> {
    const filePaths = await this.findPaths();
    const documents = await this.buildDocuments(filePaths);
    const parsedFiles = this.parse(documents);
    this.indexFiles(parsedFiles);
  }

  private async findPaths(): Promise<string[]> {
    const paths: string[] = [];

    if (this.supportsWorkspaceFolders) {
      const folders = await this.workspace.getWorkspaceFolders();
      if (folders) {
        for (const folder of folders) {
          const folderPath = fileURLToPath(folder.uri);
          const filePaths = glob.sync(folderPath + "/**/*.bds");
          paths.push(...filePaths);
        }
      }
    }

    return paths;
  }

  private async buildDocuments(filePaths: string[]): Promise<TextDocument[]> {
    const documents: TextDocument[] = [];

    for (const filePath of filePaths) {
      const fileContent = await fs.readFile(filePath, "utf8");
      const document = TextDocument.create(filePath, "bds", 1, fileContent);
      documents.push(document);
    }

    return documents;
  }

  private parse(documents: TextDocument[]): ParsedFiles {
    const parsedResults: ParsedFiles = {};

    for (const document of documents) {
      parsedResults[document.uri] = this.parser.parse(document);
    }

    return parsedResults;
  }

  private indexFiles(parsedFiles: ParsedFiles): void {
    for (const uri in parsedFiles) {
      this.index.indexDocument(uri, parsedFiles[uri]);
    }
  }
}
