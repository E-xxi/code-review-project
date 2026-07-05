const LANGUAGE_OPTIONS = [
  { value: 'python', label: 'Python' },
  { value: 'java', label: 'Java' },
  { value: 'cpp', label: 'C++' },
  { value: 'javascript', label: 'JavaScript' },
];

export default function Header({ language, onLanguageChange }) {
  return (
    <div className="header">
      <div className="header-left">
        <div className="logo">{'</>'}</div>
        <div>
          <div className="title">AlgoLens</div>
          <div className="subtitle">코드 복잡도 &amp; 개선점 분석</div>
        </div>
      </div>
      <select
        className="lang-select"
        value={language}
        onChange={(e) => onLanguageChange(e.target.value)}
      >
        {LANGUAGE_OPTIONS.map((option) => (
          <option key={option.value} value={option.value}>
            {option.label}
          </option>
        ))}
      </select>
    </div>
  );
}
