package com.example.codereview.analysis;

import com.example.codereview.grammar.Python3Parser;
import com.example.codereview.grammar.Python3ParserBaseVisitor;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Python3 파스 트리를 한 번 순회하며 구조적 특징을 센다.
 * 인스턴스 하나는 코드 한 건 분석에만 쓰고 버린다(상태를 누적하는 필드들이 재사용을 막음).
 */
class StructureVisitor extends Python3ParserBaseVisitor<Void> {

    private int loopCount = 0;
    private int currentLoopDepth = 0;
    private int maxLoopDepth = 0;
    private int branchCount = 0;
    private int functionCount = 0;
    private boolean hasRecursion = false;
    private boolean usesDictOrSet = false;
    private boolean usesSorting = false;

    private final Deque<String> functionNameStack = new ArrayDeque<>();

    @Override
    public Void visitFor_stmt(Python3Parser.For_stmtContext ctx) {
        return visitLoopBody(ctx);
    }

    @Override
    public Void visitWhile_stmt(Python3Parser.While_stmtContext ctx) {
        return visitLoopBody(ctx);
    }

    private Void visitLoopBody(org.antlr.v4.runtime.ParserRuleContext ctx) {
        loopCount++;
        currentLoopDepth++;
        maxLoopDepth = Math.max(maxLoopDepth, currentLoopDepth);
        visitChildren(ctx);
        currentLoopDepth--;
        return null;
    }

    @Override
    public Void visitIf_stmt(Python3Parser.If_stmtContext ctx) {
        branchCount++;
        return visitChildren(ctx);
    }

    @Override
    public Void visitFuncdef(Python3Parser.FuncdefContext ctx) {
        functionCount++;
        functionNameStack.push(ctx.name().getText());
        visitChildren(ctx);
        functionNameStack.pop();
        return null;
    }

    @Override
    public Void visitAtom(Python3Parser.AtomContext ctx) {
        // dictorsetmaker()는 '{}'가 비어 있으면 null이라 OPEN_BRACE 토큰 존재 여부로 확인한다.
        if (ctx.OPEN_BRACE() != null) {
            usesDictOrSet = true;
        }
        return visitChildren(ctx);
    }

    @Override
    public Void visitAtom_expr(Python3Parser.Atom_exprContext ctx) {
        boolean isCall = ctx.trailer().stream().anyMatch(t -> t.OPEN_PAREN() != null);

        if (ctx.atom() != null && ctx.atom().name() != null && isCall) {
            String calledName = ctx.atom().name().getText();
            switch (calledName) {
                case "sorted" -> usesSorting = true;
                case "dict", "set" -> usesDictOrSet = true;
                default -> {
                }
            }
            if (!functionNameStack.isEmpty() && calledName.equals(functionNameStack.peek())) {
                hasRecursion = true;
            }
        }

        for (Python3Parser.TrailerContext trailer : ctx.trailer()) {
            if (trailer.DOT() != null && trailer.name() != null && "sort".equals(trailer.name().getText())) {
                usesSorting = true;
            }
        }

        return visitChildren(ctx);
    }

    CodeStructureSummary toSummary() {
        return new CodeStructureSummary(
                loopCount, maxLoopDepth, branchCount, functionCount,
                hasRecursion, usesDictOrSet, usesSorting
        );
    }
}
