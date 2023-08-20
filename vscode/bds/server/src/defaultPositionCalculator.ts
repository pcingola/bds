import { Position } from "vscode-languageserver";

export interface PositionCalculator {
  getPositionFromMatch(match: RegExpExecArray): Position;
}

export class DefaultPositionCalculator implements PositionCalculator {
  getPositionFromMatch(match: RegExpExecArray): Position {
    const content = match.input;
    const groupIndex = match.index + match[0].indexOf(match[1]);
    const line = content.substring(0, groupIndex).split("\n").length - 1;
    const column = groupIndex - content.lastIndexOf("\n", groupIndex) - 1;
    return { line, character: column };
  }
}
