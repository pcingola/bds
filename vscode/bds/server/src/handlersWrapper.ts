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
import { getLocationByType } from "./symbolLocation";
import { indexAllFilesInWorkspace } from "./fileIndexer";
import { IndexType, SymbolIndex } from "./symbolIndex";

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
    indexAllFilesInWorkspace(
      this.connection.workspace,
      this.clientCapabilities.workspace?.workspaceFolders,
      this.symbolindex
    );
  }

  handleDocumentChange(change: TextDocumentChangeEvent<TextDocument>): void {
    this.symbolindex.parseAndIndexDocument(change.document);
  }

  handleDefinition(textDocumentPosition: DefinitionParams): Definition | null {
    const document: TextDocument | undefined = this.documents.get(
      textDocumentPosition.textDocument.uri
    );
    return document
      ? getLocationByType(
          document,
          textDocumentPosition.position,
          this.symbolindex,
          IndexType.Definition
        )
      : null;
  }

  handleReferences(textDocumentPosition: ReferenceParams): Location[] | null {
    const document: TextDocument | undefined = this.documents.get(
      textDocumentPosition.textDocument.uri
    );
    return document
      ? getLocationByType(
          document,
          textDocumentPosition.position,
          this.symbolindex,
          IndexType.Reference
        )
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
