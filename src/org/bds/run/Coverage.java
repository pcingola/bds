package org.bds.run;

import org.bds.BdsLog;
import org.bds.compile.BdsNodeWalker;
import org.bds.lang.BdsNode;
import org.bds.lang.statement.ClassDeclaration;
import org.bds.lang.statement.FunctionDeclaration;
import org.bds.lang.statement.MethodDeclaration;
import org.bds.lang.statement.StatementFunctionDeclaration;
import org.bds.vm.BdsVm;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Keep coverage statistics when running bds tests (i.e. 'bds -t -coverage ...')
 * <p>
 * We want to accumulate coveare statistics across different test "suites" (i.e. different files that
 * contain test cases). For example if there are two sets of test cases, then each of them accumulates
 * the statistics in a file:
 * bds -t -covreage tests_01.bds   # First we run the first set of test cases
 * bds -t -covreage tests_02.bds   # Then we run the second set of test cases
 * <p>
 * The statistics are accumulated in a `bds.test` file
 * <p>
 * Important: A line can contain more than one expression or statement (i.e. more than one bdsNode)
 * so we need to be able to keep which bdsNodes were covered acrross different test runs
 * <p>
 * Important: Since the test are recompiled each time the bdsNode IDs will not be the same.
 * So we only assume that nodeIds will have the same order after recompiling in different runs.
 * The LineCoverage class keeps track of coverage for "more than one node per line" by indexing
 * on bdsNode order (instead of bdsNodeId)
 *
 * @author pcingola
 */
public class Coverage implements Serializable, BdsLog {

    static String TABLE_SEPARATOR_LINE = "+----------------------------------------------------+-------------------+---------+------------------------";

    HashMap<Integer, BdsNode> bdsNodes;
    Map<String, FileCoverage> coverageByFile;
    int countLines = -1; // Lines in the code (excluding test code lines)
    int countCovered = -1; // Lines covered by test cases
    int countTestCodeLines = -1; // Lines in test cases code
    int countTestCoveredLines = -1; // Lines covered in test cases code
    double coverageRatio = -1;

    public Coverage() {
        bdsNodes = new HashMap<>();
        coverageByFile = new HashMap<>();
    }

    /**
     * Center a string
     */
    public static String centerString(int width, String s) {
        return String.format("%-" + width + "s", String.format("%" + (s.length() + (width - s.length()) / 2) + "s", s));
    }

    /**
     * Add coverage statistics from a VM (that already finished running)
     */
    public void add(BdsVm vm) {
        // Initialize: add nodes, coverage counters, etc.
        var nodes = vm.findNodes();
        nodes.stream().forEach(n -> this.bdsNodes.put(n.getId(), n)); // Add all nodes to map
        createFileCoverage(nodes); // Make sure all coverage counters are created
        mapBdsNodes2Order(); // Map all nodes to order

        // Update node coverage counters
        Map<Integer, Integer> vmcov = vm.getCoverageCounter();
        for (Integer nodeId : vmcov.keySet()) {
            var bdsNode = bdsNodes.get(nodeId);
            var count = vmcov.get(nodeId);
            add(bdsNode, count);
        }
    }

    /**
     * Add coverage from one node
     */
    public void add(BdsNode bdsNode, int count) {
        getFileCoverage(bdsNode).add(bdsNode, count);
    }

    /**
     * Calculate total coverage ratio
     */
    public double coverageRatio() {
        countLines = 0;
        countCovered = 0;
        countTestCodeLines = 0;
        countTestCoveredLines = 0;

        for (FileCoverage fc : coverageByFile.values()) {
            countLines += fc.getLinesCounted();
            countCovered += fc.getLinesCovered();
            countTestCodeLines += fc.getLinesTestCode();
            countTestCoveredLines += fc.getLinesTestCovered();
        }

        coverageRatio = countLines > 0 ? (1.0 * countCovered) / countLines : 0.0;
        return coverageRatio;
    }

    /**
     * Create all coverage counters
     */
    void createFileCoverage(Collection<BdsNode> bdsNodes) {
        for (BdsNode bdsNode : bdsNodes) {
            var file = bdsNode.getFileNameCanonical();
            if (!coverageByFile.containsKey(file)) {
                coverageByFile.put(file, new FileCoverage(this, file));
            }
            coverageByFile.get(file).createLineCoverage(bdsNode);
        }
    }

    public Map<Integer, BdsNode> getBdsNodes() {
        return bdsNodes;
    }

    public int getCountLines() {
        return countLines;
    }

    public int getCountCovered() {
        return countCovered;
    }

    public int getCountTestCodeLines() {
        return countTestCodeLines;
    }

    public int getCountTestCoveredLines() {
        return countTestCoveredLines;
    }

    FileCoverage getFileCoverage(BdsNode bdsNode) {
        var file = bdsNode.getFileNameCanonical();
        if (!coverageByFile.containsKey(file)) {
            coverageByFile.put(file, new FileCoverage(this, file));
        }
        return coverageByFile.get(file);
    }

    void mapBdsNodes2Order() {
        for (FileCoverage fc : coverageByFile.values()) fc.mapBdsNodes2Order();
    }

    /**
     * Remove coverage stats from all nodes that are in the test*() function
     * because we don't want to calculate coverage on testing code
     */
    void markTestCode(BdsVm vm, FunctionDeclaration testFunc) {
        // Find all nodes that are part of the test function code, and mark them as 'test code'
        BdsNodeWalker bw = new BdsNodeWalker(testFunc.getStatement(), null, true, false);
        // Don't recurse into the body of the functions or methods
        bw.addClassStop(StatementFunctionDeclaration.class);
        bw.addClassStop(FunctionDeclaration.class);
        bw.addClassStop(MethodDeclaration.class);
        bw.addClassStop(ClassDeclaration.class);

        // Mark all nodes within the test function code
        bw.findNodes().stream().forEach(this::markTestCode); // Mark all nodes in the function statements
        markTestCode(testFunc); // Mark the function declaration node
    }

    /**
     * Mark this node as 'test' code
     * I.e. it is part of the function executing the test case, so it should not be counted for coverage
     */
    void markTestCode(BdsNode bdsNode) {
        getFileCoverage(bdsNode).markTestCode(bdsNode);
    }

    /**
     * Show coverage table summary line
     */
    public String summary() {
        coverageRatio();
        double testLinesRatio = countTestCoveredLines / ((double) countTestCodeLines);
        return "" //
                + String.format("| %50.50s | %7d / %7d | %6.2f%% | %s\n", centerString(50, "Test code"), countTestCoveredLines, countTestCodeLines, 100.0 * testLinesRatio, "") //
                + String.format("| %50.50s | %7d / %7d | %6.2f%% | %s\n", centerString(50, "Total"), countCovered, countLines, 100.0 * coverageRatio, "") //
                ;
    }

    /**
     * Coverage table's title
     */
    String title() {
        return String.format("| %50.50s | %7s / %7s |  %6s | %s" //
                , centerString(50, "File name") //
                , "Covered" //
                , centerString(7, "Total") //
                , centerString(5, "%") //
                , "Not covered intervals" //
        );
    }

    public void resetNodes() {
        bdsNodes = new HashMap<>();
        coverageByFile.values().stream().forEach(cf -> cf.resetNodes());
    }

    public String toStringCounts() {
        // Sort by file name
        List<FileCoverage> fileCoverages = new ArrayList<>();
        fileCoverages.addAll(coverageByFile.values());
        Collections.sort(fileCoverages);

        // Show coverage table
        StringBuilder sb = new StringBuilder();
        for (FileCoverage fc : fileCoverages) {
            sb.append(fc.toStringCounts() + "\n");
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        // Sort by file name
        List<FileCoverage> fileCoverages = new ArrayList<>();
        fileCoverages.addAll(coverageByFile.values());
        Collections.sort(fileCoverages);

        // Show coverage table
        StringBuilder sb = new StringBuilder();
        sb.append(title() + "\n");
        sb.append(TABLE_SEPARATOR_LINE + "\n");
        for (FileCoverage fc : fileCoverages) {
            if (!fc.getFileName().isEmpty()) sb.append(fc + "\n");
        }
        sb.append(TABLE_SEPARATOR_LINE + "\n");
        coverageRatio();
        sb.append(summary() + "\n");

        return sb.toString();
    }
}

/**
 * Coverage for a file
 */
class FileCoverage implements Comparable<FileCoverage>, Serializable, BdsLog {
    String fileName;
    Coverage coverage;
    Map<Integer, LineCoverage> lineCoverage;

    FileCoverage(Coverage coverage, String fileName) {
        this.coverage = coverage;
        this.fileName = fileName;
        lineCoverage = new HashMap<>();
    }

    /**
     * Convert an interval to a string representation
     */
    static String intervalToString(int start, int end) {
        if (start < 0 || end < 0) return "";
        if (start == end) return start + " ";
        return start + "-" + end + " ";
    }

    /**
     * Add to covrarge counters
     */
    void add(BdsNode bdsNode, int count) {
        var lineCov = getLineCoverage(bdsNode);
        if (lineCov != null) lineCov.add(bdsNode, count);
    }

    @Override
    public int compareTo(FileCoverage o) {
        return getFileName().compareTo(o.getFileName());
    }

    /**
     * Create a line coverage for each bdsNode
     */
    void createLineCoverage(BdsNode bdsNode) {
        var lineNum = bdsNode.getLineNum();
        if (lineNum < 0) return;
        if (!lineCoverage.containsKey(lineNum)) lineCoverage.put(lineNum, new LineCoverage(this, lineNum));
        lineCoverage.get(lineNum).addBdsNode(bdsNode);
    }

    String getFileName() {
        return fileName != null ? fileName : "";
    }

    LineCoverage getLineCoverage(BdsNode bdsNode) {
        var lineNum = bdsNode.getLineNum();
        return lineCoverage.get(lineNum);
    }

    /**
     * Count number of lines (with bdsNodes)
     */
    int getLinesCounted() {
        return (int) lineCoverage.values().stream() //
                .filter(LineCoverage::isCounted)//
                .count();
    }

    /**
     * Count number of lines covered by the tests
     */
    int getLinesCovered() {
        return (int) lineCoverage.values().stream() //
                .filter(LineCoverage::isCounted) //
                .filter(LineCoverage::isCovered)//
                .count();
    }

    /**
     * Count number of lines in test code (lines in test cases)
     */
    int getLinesTestCode() {
        return (int) lineCoverage.values().stream() //
                .filter(LineCoverage::isTestCode) //
                .count();
    }


    /**
     * Count number of lines in test code (lines in test cases), that are covered by the tests
     */
    int getLinesTestCovered() {
        return (int) lineCoverage.values().stream() //
                .filter(LineCoverage::isTestCode) //
                .filter(LineCoverage::isCovered) //
                .count();
    }

    /**
     * Return a boolean array, representing whether each line was covered by the
     * test or not (or null if the line does not have a bdsNode)
     */
    Boolean[] getLinesCoveredArray() {
        // Get maximum line number in this file
        var maxLineNum = lineCoverage.values().stream().map(lc -> lc.lineNumber).max(Integer::compareTo).orElse(0);

        // Create a boolean array
        Boolean[] lineCovered = new Boolean[maxLineNum + 1];
        lineCoverage.values().stream() //
                .filter(lc -> lc.isCounted()) // Filter out lines that are not counted for statistics
                .forEach(lc -> lineCovered[lc.lineNumber] = lc.isCovered()); // Mark as covered
        return lineCovered;
    }

    void markTestCode(BdsNode bdsNode) {
        var lineCov = getLineCoverage(bdsNode);
        if (lineCov != null) lineCov.markTestCode();
    }

    /**
     * Map bds node to order
     */
    void mapBdsNodes2Order() {
        for (LineCoverage l : lineCoverage.values()) l.mapBdsNodes2Order();
    }

    void resetNodes() {
        lineCoverage.values().stream().forEach(lc -> lc.resetNodes());
    }

    public String toStringCounts() {
        return lineCoverage.values().stream()//
                .sorted() //
                .map(LineCoverage::toStringCounts) //
                .collect(Collectors.joining());
    }

    public String toString() {
        Boolean[] lines = getLinesCoveredArray();

        // Count line covered
        int countLines = 0, countCovered = 0;
        for (int i = 0; i < lines.length; i++) {
            Boolean l = lines[i];
            if (l != null) {
                countLines++;
                if (l) countCovered++;
            }
        }

        // Lines not covered (intevals)
        int start = -1, end = -1;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            Boolean l = lines[i];
            if (l != null) {
                if (l) {
                    if (end >= 0) sb.append(intervalToString(start, end));
                    start = end = -1;
                } else {
                    if (start < 0) start = i;
                    end = i;
                }
            }
        }
        if (end >= 0) sb.append(intervalToString(start, end));

        // Coverage percentage
        double perc = (100.0 * countCovered) / countLines;

        // Limit file name length
        var file = getFileName();
        if (file.length() > 50) file = "..." + file.substring(file.length() - 50 + 3);

        // One line statistics
        return String.format("| %50.50s | %7d / %7d | %6.2f%% | %s", file, countCovered, countLines, perc, sb);
    }
}

/**
 * Coverage for a line in a file
 * <p>
 * Note: A line can contain more than one statement and one
 * of the statements could be not covered by any test. E.g.:
 * <p>
 * # Two statements in one line
 * if( ok ) { println 'OK' } else { println 'BAD' }
 * <p>
 * We need to make sure that ALL nodes in a line are covered
 * <p>
 * Important: We assume always the nodeIds within a line are compiled
 * in the same order, so even if the nodeIds are different, the order
 * is mantained
 */
class LineCoverage implements Comparable<LineCoverage>, Serializable {
    boolean testCode; // Is this part of the 'test' code?
    int lineNumber; // Line number within the file
    FileCoverage fileCoverage; // File this line belongs to
    Set<BdsNode> nodes; // All nodes in the file
    Map<Integer, Integer> nodeId2order; // Node order
    int[] coverageCount; // Coverage counters

    LineCoverage(FileCoverage fileCoverage, int lineNumber) {
        this.fileCoverage = fileCoverage;
        this.lineNumber = lineNumber;
        nodes = new HashSet<>();
    }

    /**
     * Add to coverage counters
     */
    void add(BdsNode bdsNode, int count) {
        var idx = nodeId2order.get(bdsNode.getId()); // Get node order index from nodeId
        if (coverageCount == null) coverageCount = new int[nodes.size()];
        coverageCount[idx] += count;
    }

    /**
     * Add a bds node to a line (in cases where there is more than one element
     */
    void addBdsNode(BdsNode bdsNode) {
        nodes.add(bdsNode);
    }

    @Override
    public int compareTo(LineCoverage o) {
        return lineNumber - o.lineNumber;
    }

    /**
     * Is this line counted?
     * We don't count lines with negative line number, or lines that are part of the test case code
     */
    boolean isCounted() {
        return lineNumber >= 0 && !testCode;
    }

    /**
     * Is this line fully covered by test cases? (i.e. all nodes in the line are covered)
     */
    boolean isCovered() {
        if (coverageCount == null) return false;

        for (int c : coverageCount) {
            if (c == 0) return false;
        }
        return true;
    }

    boolean isTestCode() {
        return testCode;
    }

    /**
     * Map the nodeIds to their order in the line
     */
    void mapBdsNodes2Order() {
        nodeId2order = new HashMap<>();

        // Create a sorted list of nodeIds
        List<Integer> nodeIds = new ArrayList<>(nodeId2order.size());
        for (BdsNode n : nodes) nodeIds.add(n.getId());
        Collections.sort(nodeIds);

        // Map nodeIds to sort order
        for (int i = 0; i < nodeIds.size(); i++)
            nodeId2order.put(nodeIds.get(i), i);
    }

    void markTestCode() {
        this.testCode = true;
    }

    /**
     * Reset nodes. Used after loading from a serialized file
     */
    void resetNodes() {
        nodes = new HashSet<>();
    }

    String toStringCounts() {
        if (lineNumber < 0) return "";

        var countsStr = "";
        if (coverageCount == null) countsStr = "0";
        else if (coverageCount.length == 1) countsStr = "" + coverageCount[0];
        else countsStr = Arrays.toString(coverageCount);

        return fileCoverage.fileName //
                + ":" + lineNumber //
                + "\t" + countsStr //
                + (isTestCode() ? "\ttest-code" : "") //
                + "\n";
    }
}