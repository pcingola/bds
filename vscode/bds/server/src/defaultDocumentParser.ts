import { TextDocument } from "vscode-languageserver-textdocument";
import { Position } from "vscode-languageserver";
import { IndexType } from "./symbolIndex";
import {
  DefaultPositionCalculator,
  PositionCalculator,
} from "./defaultPositionCalculator";

export type ParsedSymbol = {
  symbol: string;
  type: IndexType;
  position: Position;
};

export interface DocumentParser {
  parse(document: TextDocument): ParsedSymbol[];
}

export class DefaultDocumentParser implements DocumentParser {
  private readonly classReferenceRegex: RegExp;
  private readonly classDefinitionRegex: RegExp;
  private readonly positionCalculator: PositionCalculator;

  constructor(
    classReferenceRegex: RegExp = /new\s+([a-zA-Z_]\w*)\s*\(/g,
    classDefinitionRegex: RegExp = /class\s+([a-zA-Z_]\w*)\s*(?:extends\s*[a-zA-Z_]\w*\s*)?\{/g,
    positionCalculator: PositionCalculator = new DefaultPositionCalculator()
  ) {
    this.classReferenceRegex = classReferenceRegex;
    this.classDefinitionRegex = classDefinitionRegex;
    this.positionCalculator = positionCalculator;
  }

  parse(document: TextDocument): ParsedSymbol[] {
    const content = document.getText();
    const definitions = this.findSymbols(
      content,
      this.classDefinitionRegex,
      IndexType.Definition
    );
    const references = this.findSymbols(
      content,
      this.classReferenceRegex,
      IndexType.Reference
    );
    return [...definitions, ...references];
  }

  private findSymbols(
    content: string,
    regex: RegExp,
    type: IndexType
  ): ParsedSymbol[] {
    const results: ParsedSymbol[] = [];
    this.processRegex(content, regex, (match) => {
      results.push({
        symbol: match[1],
        type: type,
        position: this.positionCalculator.getPositionFromMatch(match),
      });
    });
    return results;
  }

  private processRegex(
    content: string,
    regex: RegExp,
    callback: (match: RegExpExecArray) => void
  ): void {
    let match;
    while ((match = regex.exec(content)) !== null) {
      callback(match);
    }
  }
}
