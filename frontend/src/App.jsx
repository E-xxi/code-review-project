import { useState } from 'react';
import './App.css';
import Header from './components/Header.jsx';
import CodeInputPanel, { SAMPLE_PROBLEM, SAMPLE_CODE } from './components/CodeInputPanel.jsx';
import ResultPanel from './components/ResultPanel.jsx';
import { analyzeCode } from './api/analyzeCode.js';

function App() {
  const [language, setLanguage] = useState('python');
  const [isProblemOpen, setIsProblemOpen] = useState(true);
  const [problem, setProblem] = useState(SAMPLE_PROBLEM);
  const [code, setCode] = useState(SAMPLE_CODE);
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);
  const [isAnalyzing, setIsAnalyzing] = useState(false);

  const handleLoadSample = () => {
    setLanguage('python');
    setProblem(SAMPLE_PROBLEM);
    setCode(SAMPLE_CODE);
  };

  const handleAnalyze = async () => {
    setIsAnalyzing(true);
    setError(null);
    try {
      const response = await analyzeCode({ problem, code });
      setResult(response);
    } catch (err) {
      setError(err.message);
      setResult(null);
    } finally {
      setIsAnalyzing(false);
    }
  };

  return (
    <div className="app">
      <Header language={language} onLanguageChange={setLanguage} />
      <div className="main">
        <CodeInputPanel
          problem={problem}
          onProblemChange={setProblem}
          isProblemOpen={isProblemOpen}
          onToggleProblem={() => setIsProblemOpen((open) => !open)}
          code={code}
          onCodeChange={setCode}
          language={language}
          onLoadSample={handleLoadSample}
          onAnalyze={handleAnalyze}
          isAnalyzing={isAnalyzing}
        />
        <ResultPanel result={result} isAnalyzing={isAnalyzing} error={error} />
      </div>
    </div>
  );
}

export default App;
