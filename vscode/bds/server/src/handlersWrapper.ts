import {
  InitializeParams,
  InitializeResult,
  DefinitionParams,
  ReferenceParams,
  Definition,
  Location,
  Connection,
  TextDocumentSyncKind,
  TextDocumentChangeEvent,
  TextDocuments,
  ClientCapabilities,
} from "vscode-languageserver/node";
import { TextDocument } from "vscode-languageserver-textdocument";
import { getDefinition, getReferences } from "./definitionLogic";
import { indexBDSFilesInWorkspace } from "./fileIndexer";
import { SymbolIndex } from "./symbolIndex";

class HandlersWrapper {
  private clientCapabilities: ClientCapabilities = {};
  private connection: Connection;
  private documents: TextDocuments<TextDocument>;
  private symbolindex: SymbolIndex;

  constructor(
    connection: Connection,
    documents: TextDocuments<TextDocument>,
    symbolIndex: SymbolIndex
  ) {
    this.connection = connection;
    this.documents = documents;
    this.symbolindex = symbolIndex;
  }
  handleInitialize(params: InitializeParams): InitializeResult {
    this.clientCapabilities = params.capabilities;
    return {
      capabilities: {
        textDocumentSync: TextDocumentSyncKind.Incremental,
        definitionProvider: true,
        referencesProvider: true,
      },
    };
  }

  handleInitialized(): void {
    if (this.clientCapabilities.workspace?.workspaceFolders) {
      this.connection.workspace.getWorkspaceFolders().then((folders) => {
        folders?.forEach((folder) => {
          indexBDSFilesInWorkspace(folder, this.symbolindex);
        });
      });
    }
  }

  handleDocumentChange(change: TextDocumentChangeEvent<TextDocument>): void {
    this.symbolindex.parseAndIndexDocument(change.document);
  }

  handleDefinition(textDocumentPosition: DefinitionParams): Definition | null {
    const document: TextDocument | undefined = this.documents.get(
      textDocumentPosition.textDocument.uri
    );
    return document
      ? getDefinition(document, textDocumentPosition.position, this.symbolindex)
      : null;
  }

  handleReferences(textDocumentPosition: ReferenceParams): Location[] | null {
    const document: TextDocument | undefined = this.documents.get(
      textDocumentPosition.textDocument.uri
    );
    return document
      ? getReferences(document, textDocumentPosition.position, this.symbolindex)
      : null;
  }

  registerHandlers() {
    this.connection.onInitialize(this.handleInitialize.bind(this));
    this.connection.onInitialized(this.handleInitialized.bind(this));
    this.documents.onDidChangeContent(this.handleDocumentChange.bind(this));
    this.connection.onDefinition(this.handleDefinition.bind(this));
    this.connection.onReferences(this.handleReferences.bind(this));
  }

  listen() {
    this.documents.listen(this.connection);
    this.connection.listen();
  }
}

export default HandlersWrapper;
