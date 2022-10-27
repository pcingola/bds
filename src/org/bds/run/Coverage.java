package org.bds.run;

import org.bds.lang.BdsNode;
import org.bds.vm.BdsVm;

import java.util.*;

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
public class Coverage {

    static String TABLE_SEPARATOR_LINE = "+----------------------------------------------------+-------------------+---------+------------------------";

    HashMap<Integer, BdsNode> bdsNodes;
    Map<String, FileCoverage> coverageByFile;
    int countLines = -1, countCovered = -1;
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
        createFileCoverage(vm.findNodes()); // Make sure all coverage counters are created
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
        var file = bdsNode.getFileNameCanonical();
        if (!coverageByFile.containsKey(file)) {
            coverageByFile.put(file, new FileCoverage(this, file));
        }
        coverageByFile.get(file).add(bdsNode, count);
    }


    /**
     * Calculate total coverage ratio
     */
    public double coverageRatio() {
        countLines = 0;
        countCovered = 0;

        for (FileCoverage fc : coverageByFile.values()) {
            countLines += fc.getLines();
            countCovered += fc.getLinesCovered();
        }

        coverageRatio = (1.0 * countCovered) / countLines;
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


    void mapBdsNodes2Order() {
        for (FileCoverage fc : coverageByFile.values()) fc.mapBdsNodes2Order();
    }

    /**
     * Show coverage table summary line
     */
    public String summary() {
        coverageRatio();
        return String.format("| %50.50s | %7d / %7d | %5.2f%% | %s", centerString(50, "Total"), countCovered, countLines, 100.0 * coverageRatio, "");
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
            sb.append(fc + "\n");
        }
        sb.append(TABLE_SEPARATOR_LINE + "\n");
        sb.append(summary());

        return sb.toString();
    }
}

/**
 * Coverage for a file
 */
class FileCoverage implements Comparable<FileCoverage> {
    String fileName;
    Coverage coverage;
    Map<Integer, LineCoverage> lineCoverage;

    FileCoverage(Coverage coverage, String fileName) {
        this.coverage = coverage;
        this.fileName = fileName;
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
     * Add to covrerge counters
     */
    void add(BdsNode bdsNode, int count) {
        var lineNum = bdsNode.getLineNum();
        if (!lineCoverage.containsKey(lineNum)) lineCoverage.put(lineNum, new LineCoverage(this, lineNum));
        lineCoverage.get(lineNum).add(bdsNode, count);
    }

    @Override
    public int compareTo(FileCoverage o) {
        return fileName.compareTo(o.fileName);
    }

    /**
     * Create a line coverage for each bdsNode
     */
    void createLineCoverage(BdsNode bdsNode) {
        var lineNum = bdsNode.getLineNum();
        if (!lineCoverage.containsKey(lineNum)) lineCoverage.put(lineNum, new LineCoverage(this, lineNum));
        lineCoverage.get(lineNum).addBdsNode(bdsNode);
    }

    /**
     * Count number of lines (with bdsNodes)
     */
    int getLines() {
        return lineCoverage.size();
    }

    /**
     * Count number of lines covered by the tests
     */
    int getLinesCovered() {
        return (int) lineCoverage.values().stream().filter(LineCoverage::isCovered).count();
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
        Arrays.fill(lineCovered, false);
        lineCoverage.values().stream().forEach(lc -> lineCovered[lc.lineNumber] = lc.isCovered());
        return lineCovered;
    }

    /**
     * Map bds node to order
     */
    void mapBdsNodes2Order() {
        for (LineCoverage l : lineCoverage.values()) l.mapBdsNodes2Order();
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
        var file = fileName;
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
class LineCoverage implements Comparable<LineCoverage> {
    int lineNumber;
    FileCoverage fileCoverage;
    Set<BdsNode> nodes;
    Map<Integer, Integer> nodeId2order;
    int[] count;

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
        this.count[idx] += count;
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
     * Is this line fully covered by test cases?
     */
    boolean isCovered() {
        for (int c : count) {
            if (c == 0) return false;
        }
        return true;
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
            nodeId2order.put(i, nodeIds.get(i));
    }

}