import { expect } from "chai";
import { DefaultPositionCalculator } from "./defaultPositionCalculator";

describe("DefaultPositionCalculator", () => {
  let positionCalculator: DefaultPositionCalculator;

  beforeEach(() => {
    positionCalculator = new DefaultPositionCalculator();
  });

  it("should calculate the correct position for a given match", () => {
    const mockMatch: RegExpExecArray = ["sampleMatch"] as any;
    mockMatch.input =
      "This is a test\nsampleMatch should be found here\nAnd another line here";
    mockMatch.index = 15;

    const position = positionCalculator.getPositionFromMatch(mockMatch);

    expect(position).to.deep.equal({ line: 1, character: 0 });
  });
});
