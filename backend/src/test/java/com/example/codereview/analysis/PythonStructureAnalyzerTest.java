package com.example.codereview.analysis;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class PythonStructureAnalyzerTest {

    private final PythonStructureAnalyzer analyzer = new PythonStructureAnalyzer();

    @Test
    void detectsNestedLoopsWithNoRecursionOrHashUsage() {
        String bruteForceTwoSum = """
                def solve(nums, k):
                    n = len(nums)
                    count = 0
                    for i in range(n):
                        for j in range(i + 1, n):
                            if nums[i] + nums[j] == k:
                                count += 1
                    return count
                """;

        CodeStructureSummary summary = analyzer.analyze(bruteForceTwoSum).orElseThrow();

        assertThat(summary.loopCount()).isEqualTo(2);
        assertThat(summary.maxLoopDepth()).isEqualTo(2);
        assertThat(summary.branchCount()).isEqualTo(1);
        assertThat(summary.functionCount()).isEqualTo(1);
        assertThat(summary.hasRecursion()).isFalse();
        assertThat(summary.usesDictOrSet()).isFalse();
        assertThat(summary.usesSorting()).isFalse();
    }

    @Test
    void detectsHashMapUsageAndSingleLoop() {
        String hashMapTwoSum = """
                def solve(nums, k):
                    seen = {}
                    count = 0
                    for x in nums:
                        need = k - x
                        if need in seen:
                            count += seen[need]
                        seen[x] = seen.get(x, 0) + 1
                    return count
                """;

        CodeStructureSummary summary = analyzer.analyze(hashMapTwoSum).orElseThrow();

        assertThat(summary.loopCount()).isEqualTo(1);
        assertThat(summary.maxLoopDepth()).isEqualTo(1);
        assertThat(summary.usesDictOrSet()).isTrue();
    }

    @Test
    void detectsRecursion() {
        String recursiveFactorial = """
                def factorial(n):
                    if n <= 1:
                        return 1
                    return n * factorial(n - 1)
                """;

        CodeStructureSummary summary = analyzer.analyze(recursiveFactorial).orElseThrow();

        assertThat(summary.hasRecursion()).isTrue();
        assertThat(summary.loopCount()).isZero();
    }

    @Test
    void detectsSortUsage() {
        String sortedUsage = """
                def solve(nums):
                    return sorted(nums)
                """;

        CodeStructureSummary summary = analyzer.analyze(sortedUsage).orElseThrow();

        assertThat(summary.usesSorting()).isTrue();
    }

    @Test
    void returnsEmptyForNonPythonInput() {
        String javaCode = """
                public class Main {
                    public static void main(String[] args) {
                        System.out.println("hi");
                    }
                }
                """;

        Optional<CodeStructureSummary> summary = analyzer.analyze(javaCode);

        assertThat(summary).isEmpty();
    }
}
