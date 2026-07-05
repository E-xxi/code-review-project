function ComplexityCard({ complexity }) {
  return (
    <div className="result-card">
      <div className="card-header">
        <span className="card-icon time">⏱</span>
        <span className="card-title">시간/공간 복잡도</span>
      </div>
      <div className="complexity-row">
        <span className="badge-time">시간 {complexity.time}</span>
        <span className="badge-space">공간 {complexity.space}</span>
      </div>
    </div>
  );
}

function StyleFeedbackCard({ items }) {
  return (
    <div className="result-card">
      <div className="card-header">
        <span className="card-icon style">✎</span>
        <span className="card-title">스타일 / 가독성 피드백</span>
      </div>
      <div className="style-feedback-list">
        {items.map((item) => (
          <div className="style-feedback-item" key={item}>
            <span className="bullet">·</span>
            <span>{item}</span>
          </div>
        ))}
      </div>
    </div>
  );
}

function PatternCard({ patterns, concepts }) {
  return (
    <div className="result-card">
      <div className="card-header">
        <span className="card-icon pattern">◆</span>
        <span className="card-title">감지된 패턴 &amp; 개념 진단</span>
      </div>
      <div className="pattern-body">
        {patterns.length > 0 && (
          <div className="pattern-badge-row">
            {patterns.map((pattern) => (
              <span className="pattern-badge" key={pattern}>{pattern}</span>
            ))}
            <span className="pattern-badge-label">감지된 접근법</span>
          </div>
        )}
        {concepts.map((concept) => (
          <div className="diagnosis-box" key={concept.concept}>
            <div className="diagnosis-concept">{concept.concept}</div>
            <div>{concept.diagnosis}</div>
          </div>
        ))}
      </div>
    </div>
  );
}

function ImprovementCard({ improvements }) {
  return (
    <div className="result-card">
      <div className="card-header">
        <span className="card-icon improve">↻</span>
        <span className="card-title">개선 방향 제안</span>
      </div>
      <div className="improvement-body">
        {improvements.map((improvement) => (
          <div key={improvement.description}>
            <div className="improvement-summary">{improvement.description}</div>
            {improvement.example && (
              <div className="refactor-block">
                <div className="refactor-header">리팩토링 예시</div>
                <pre className="refactor-code">{improvement.example}</pre>
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}

export default function ResultPanel({ result, isAnalyzing, error }) {
  if (error) {
    return (
      <div className="panel">
        <div className="empty-state">
          <div className="empty-icon">⚠</div>
          <div className="empty-text">{error}</div>
        </div>
      </div>
    );
  }

  if (!result) {
    return (
      <div className="panel">
        <div className="empty-state">
          <div className="empty-icon">🔍</div>
          <div className="empty-text">
            {isAnalyzing ? '분석 중입니다...' : '코드를 분석하면 결과가 여기에 표시됩니다'}
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="panel">
      <ComplexityCard complexity={result.complexity} />
      <StyleFeedbackCard items={result.styleFeedback} />
      <PatternCard patterns={result.detectedPatterns} concepts={result.missingConcepts} />
      <ImprovementCard improvements={result.improvements} />
    </div>
  );
}
