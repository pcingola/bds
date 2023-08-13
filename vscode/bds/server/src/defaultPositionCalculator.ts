import { Position } from "vscode-languageserver";

export interface PositionCalculator {
  getPositionFromMatch(match: RegExpExecArray): Position;
}
export class DefaultPositionCalculator implements PositionCalculator {
  getPositionFromMatch(match: RegExpExecArray): Position {
    const content = match.input;
    const line = content.substring(0, match.index).split("\n").length - 1;
    const column = match.index - content.lastIndexOf("\n", match.index) - 1;
    return { line, character: column };
  }
}
