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
import { WorkspaceIndexer } from "./fileIndexer";
import { IndexType, SymbolIndex } from "./symbolIndex";
import { DocumentParser } from "./defaultDocumentParser";

class HandlersWrapper {
  private clientCapabilities: ClientCapabilities = {};
  private connection: Connection;
  private documents: TextDocuments<TextDocument>;
  private symbolindex: SymbolIndex;
  private parser: DocumentParser;

  constructor(
    connection: Connection,
    documents: TextDocuments<TextDocument>,
    symbolIndex: SymbolIndex,
    parser: DocumentParser
  ) {
    this.connection = connection;
    this.documents = documents;
    this.symbolindex = symbolIndex;
    this.parser = parser;
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
    const indexer = new WorkspaceIndexer(
      this.connection.workspace,
      this.clientCapabilities.workspace?.workspaceFolders,
      this.symbolindex,
      this.parser
    );
    indexer.run();
  }

  handleDocumentChange(change: TextDocumentChangeEvent<TextDocument>): void {
    const parsedResults = this.parser.parse(change.document);
    this.symbolindex.indexDocument(change.document.uri, parsedResults);
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
