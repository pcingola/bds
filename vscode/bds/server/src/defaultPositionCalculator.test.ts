import { DefaultPositionCalculator } from "./defaultPositionCalculator";

describe("DefaultPositionCalculator", () => {
  let calculator: DefaultPositionCalculator;

  beforeEach(() => {
    calculator = new DefaultPositionCalculator();
  });

  it("should calculate the correct position for a given match", () => {
    const mockContent = "class SampleClass {";
    const mockMatch: RegExpExecArray = [
      "class SampleClass {",
      "SampleClass",
    ] as any;
    mockMatch.input = mockContent;
    mockMatch.index = 0;

    const position = calculator.getPositionFromMatch(mockMatch);

    // Jest-style assertions:
    expect(position).toEqual({ line: 0, character: 6 });
  });

  // Additional test cases can be added to cover more scenarios and edge cases.
});
