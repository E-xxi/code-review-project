import Editor from '@monaco-editor/react';

const SAMPLE_PROBLEM = 'N개의 정수 중, 두 수를 더해 K가 되는 쌍의 개수를 구하시오.';
const SAMPLE_CODE = `def solve(nums, k):
    n = len(nums)
    count = 0
    for i in range(n):
        for j in range(i+1, n):
            if nums[i] + nums[j] == k:
                count += 1
    return count`;

export default function CodeInputPanel({
  problem,
  onProblemChange,
  isProblemOpen,
  onToggleProblem,
  code,
  onCodeChange,
  language,
  onLoadSample,
  onAnalyze,
  isAnalyzing,
}) {
  return (
    <div className="panel">
      <div>
        <button className="problem-toggle" onClick={onToggleProblem} type="button">
          <span>문제 설명 (선택)</span>
          <span>{isProblemOpen ? '−' : '+'}</span>
        </button>
        {isProblemOpen && (
          <textarea
            className="problem-textarea"
            placeholder="문제 설명을 붙여넣으세요"
            value={problem}
            onChange={(e) => onProblemChange(e.target.value)}
          />
        )}
      </div>

      <div className="code-card">
        <div className="code-card-header">
          <span>내 코드</span>
          <button className="sample-btn" onClick={onLoadSample} type="button">
            예시 코드 불러오기
          </button>
        </div>
        <div className="code-editor">
          <Editor
            language={language}
            value={code}
            onChange={(value) => onCodeChange(value ?? '')}
            theme="vs-dark"
            options={{
              fontFamily: "'JetBrains Mono', ui-monospace, monospace",
              fontSize: 13,
              minimap: { enabled: false },
              scrollBeyondLastLine: false,
              tabSize: 2,
            }}
          />
        </div>
      </div>

      <button
        className="analyze-btn"
        onClick={onAnalyze}
        disabled={isAnalyzing || !code.trim()}
        type="button"
      >
        {isAnalyzing ? '분석 중...' : '분석하기'}
      </button>
    </div>
  );
}

export { SAMPLE_PROBLEM, SAMPLE_CODE };
