const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

export async function analyzeCode({ problem, code }) {
  const response = await fetch(`${API_BASE_URL}/api/analyze`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ problem, code }),
  });

  if (!response.ok) {
    throw new Error(`분석 요청이 실패했습니다 (HTTP ${response.status})`);
  }

  return response.json();
}
