import React from "react";

// Geometria do arco do gauge semicircular (Score de Excelência)
function polarToCartesian(cx, cy, r, angleDeg) {
  const a = ((angleDeg - 180) * Math.PI) / 180;
  return { x: cx + r * Math.cos(a), y: cy + r * Math.sin(a) };
}
function describeArc(cx, cy, r, startAngle, endAngle) {
  const start = polarToCartesian(cx, cy, r, endAngle);
  const end = polarToCartesian(cx, cy, r, startAngle);
  const largeArc = endAngle - startAngle <= 180 ? 0 : 1;
  return `M ${start.x} ${start.y} A ${r} ${r} 0 ${largeArc} 0 ${end.x} ${end.y}`;
}

/** Mostra o Score de Excelência (0-100) como um gauge de instrumento de medição. */
export default function Gauge({ score, size = 168, accent = "#f5a623" }) {
  const cx = size / 2;
  const cy = size / 2 + 6;
  const r = size / 2 - 18;
  const pct = Math.max(0, Math.min(100, score)) / 100;
  const trackPath = describeArc(cx, cy, r, 0, 180);
  const valuePath = describeArc(cx, cy, r, 0, 180 * pct);
  const ticks = Array.from({ length: 11 }, (_, i) => i * 18);

  return (
    <svg width={size} height={size / 2 + 40} viewBox={`0 0 ${size} ${size / 2 + 40}`}>
      {ticks.map((a, i) => {
        const p1 = polarToCartesian(cx, cy, r + 8, a);
        const p2 = polarToCartesian(cx, cy, r + 2, a);
        return <line key={i} x1={p1.x} y1={p1.y} x2={p2.x} y2={p2.y} stroke="#3A465A" strokeWidth="2" />;
      })}
      <path d={trackPath} fill="none" stroke="#232F40" strokeWidth="14" strokeLinecap="round" />
      <path d={valuePath} fill="none" stroke={accent} strokeWidth="14" strokeLinecap="round" />
      <text x={cx} y={cy - 6} textAnchor="middle" fontFamily="'IBM Plex Mono', monospace" fontSize="34" fontWeight="600" fill="#F5F3EE">
        {score}
      </text>
      <text x={cx} y={cy + 16} textAnchor="middle" fontFamily="'IBM Plex Sans', sans-serif" fontSize="10.5" letterSpacing="1.5" fill="#9AA5B1">
        SCORE DE EXCELÊNCIA
      </text>
    </svg>
  );
}
