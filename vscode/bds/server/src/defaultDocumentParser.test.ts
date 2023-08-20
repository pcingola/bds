import { TextDocument } from "vscode-languageserver-textdocument";
import { DefaultDocumentParser, ParsedSymbol } from "./defaultDocumentParser";
import { IndexType } from "./symbolIndex";

describe("DefaultDocumentParser", () => {
  let parser: DefaultDocumentParser;

  beforeEach(() => {
    parser = new DefaultDocumentParser();
  });

  it("should identify single class definition", () => {
    const mockDocument: TextDocument = {
      getText: () => `class SampleClass {`,
    } as any;

    const symbols: ParsedSymbol[] = parser.parse(mockDocument);

    expect(symbols).toContainEqual({
      symbol: "SampleClass",
      type: IndexType.Definition,
      position: { line: 0, character: 6 },
    });
  });
  it("should identify multiple class definitions", () => {
    const mockDocument: TextDocument = {
      getText: () => `
class SampleClass1 {
}
class SampleClass2 {
}
`,
    } as any;

    const symbols: ParsedSymbol[] = parser.parse(mockDocument);

    expect(symbols).toContainEqual({
      symbol: "SampleClass1",
      type: IndexType.Definition,
      position: { line: 1, character: 6 },
    });
    expect(symbols).toContainEqual({
      symbol: "SampleClass2",
      type: IndexType.Definition,
      position: { line: 3, character: 6 },
    });
  });
});
