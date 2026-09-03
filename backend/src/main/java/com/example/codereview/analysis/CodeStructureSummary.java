package com.example.codereview.analysis;

/**
 * ANTLR4로 Python 코드를 파싱해 뽑아낸 구조적 특징.
 * Claude 프롬프트에 컨텍스트로 덧붙여서, 텍스트만으로는 놓치기 쉬운 반복문 중첩/재귀 여부 같은
 * 사실관계를 모델이 다시 추론하지 않고 바로 참고하게 한다.
 */
public record CodeStructureSummary(
        int loopCount,
        int maxLoopDepth,
        int branchCount,
        int functionCount,
        boolean hasRecursion,
        boolean usesDictOrSet,
        boolean usesSorting
) {

    public String toPromptContext() {
        return """
                - 반복문 개수: %d개, 최대 중첩 깊이: %d
                - 조건 분기(if/elif) 개수: %d
                - 함수 정의 개수: %d개, 재귀 호출: %s
                - 딕셔너리/셋 사용: %s
                - 정렬 함수(sorted/.sort) 사용: %s""".formatted(
                loopCount, maxLoopDepth, branchCount, functionCount,
                hasRecursion ? "있음" : "없음",
                usesDictOrSet ? "있음" : "없음",
                usesSorting ? "있음" : "없음"
        );
    }
}
