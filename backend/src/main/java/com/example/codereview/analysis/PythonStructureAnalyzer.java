package com.example.codereview.analysis;

import com.example.codereview.grammar.Python3Lexer;
import com.example.codereview.grammar.Python3Parser;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * ANTLR4 Python3 문법(grammars-v4)으로 코드를 파싱해 구조적 특징을 뽑아낸다.
 * 파이썬이 아니거나 문법 오류가 있는 코드가 들어오면 예외를 던지지 않고 빈 결과를 돌려준다 —
 * 구조 분석은 Claude 분석 품질을 보조하는 부가 정보일 뿐, 실패해도 전체 분석 흐름을 막으면 안 된다.
 */
@Component
public class PythonStructureAnalyzer {

    private static final BaseErrorListener THROWING_ERROR_LISTENER = new BaseErrorListener() {
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
                                 int charPositionInLine, String msg, RecognitionException e) {
            throw new ParseCancellationException("line " + line + ":" + charPositionInLine + " " + msg);
        }
    };

    public Optional<CodeStructureSummary> analyze(String code) {
        try {
            Python3Lexer lexer = new Python3Lexer(CharStreams.fromString(code));
            lexer.removeErrorListeners();
            lexer.addErrorListener(THROWING_ERROR_LISTENER);

            Python3Parser parser = new Python3Parser(new CommonTokenStream(lexer));
            parser.removeErrorListeners();
            parser.addErrorListener(THROWING_ERROR_LISTENER);

            Python3Parser.File_inputContext tree = parser.file_input();

            StructureVisitor visitor = new StructureVisitor();
            visitor.visit(tree);
            return Optional.of(visitor.toSummary());
        } catch (RuntimeException e) {
            // ParseCancellationException(문법 오류)을 포함해, 파싱 중 발생 가능한 모든 런타임 예외를 여기서 흡수한다.
            return Optional.empty();
        }
    }
}
