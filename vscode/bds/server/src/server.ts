import {
  createConnection,
  TextDocuments,
  ProposedFeatures,
} from "vscode-languageserver/node";
import { TextDocument } from "vscode-languageserver-textdocument";
import HandlersWrapper from "./handlersWrapper";
import { SymbolIndex } from "./symbolIndex";
import { DefaultDocumentParser } from "./defaultDocumentParser";

const connection = createConnection(ProposedFeatures.all);
const documents = new TextDocuments(TextDocument);
const symbolIndex = new SymbolIndex();
const parser = new DefaultDocumentParser();

const requestHandler = new HandlersWrapper(
  connection,
  documents,
  symbolIndex,
  parser
);

requestHandler.registerHandlers();
requestHandler.listen();
