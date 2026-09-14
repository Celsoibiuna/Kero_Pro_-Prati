/* =====================================================================
   KeroPro — main.js
   Comportamento geral do site institucional: ícones, animações de
   entrada, acordeão de FAQ e ilustração do gauge de score.
===================================================================== */

document.addEventListener("DOMContentLoaded", () => {
  mountIcons();
  setupRevealAnimations();
  setupFaqAccordion();
  mountHeroGauge();
});

/* ---------- Animação de entrada suave para blocos .reveal ---------- */
function setupRevealAnimations() {
  const items = document.querySelectorAll(".reveal");
  if (!("IntersectionObserver" in window) || items.length === 0) {
    items.forEach((el) => el.classList.add("is-visible"));
    return;
  }
  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          entry.target.classList.add("is-visible");
          observer.unobserve(entry.target);
        }
      });
    },
    { threshold: 0.15 }
  );
  items.forEach((el) => observer.observe(el));
}

/* ---------- Acordeão de perguntas frequentes ---------- */
function setupFaqAccordion() {
  document.querySelectorAll(".faq-item").forEach((item) => {
    const q = item.querySelector(".faq-q");
    if (!q) return;
    q.addEventListener("click", () => {
      const isOpen = item.classList.contains("is-open");
      document.querySelectorAll(".faq-item.is-open").forEach((other) => other.classList.remove("is-open"));
      if (!isOpen) item.classList.add("is-open");
    });
  });
}

/* ---------- Gauge decorativo do Score de Excelência (seção hero) ---------- */
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
function mountHeroGauge(score = 92) {
  const el = document.getElementById("gauge-illustration");
  if (!el) return;
  const size = 260;
  const cx = size / 2, cy = size / 2 + 14, r = size / 2 - 30;
  const pct = score / 100;
  const track = describeArc(cx, cy, r, 0, 180);
  const value = describeArc(cx, cy, r, 0, 180 * pct);
  const ticks = Array.from({ length: 11 }, (_, i) => i * 18)
    .map((a) => {
      const p1 = polarToCartesian(cx, cy, r + 12, a);
      const p2 = polarToCartesian(cx, cy, r + 4, a);
      return `<line x1="${p1.x}" y1="${p1.y}" x2="${p2.x}" y2="${p2.y}" stroke="#ffffff26" stroke-width="2"/>`;
    }).join("");

  el.innerHTML = `
    <svg width="${size}" height="${size / 2 + 60}" viewBox="0 0 ${size} ${size / 2 + 60}">
      ${ticks}
      <path d="${track}" fill="none" stroke="#ffffff14" stroke-width="20" stroke-linecap="round"/>
      <path d="${value}" fill="none" stroke="#f5a623" stroke-width="20" stroke-linecap="round"/>
      <text x="${cx}" y="${cy - 8}" text-anchor="middle" font-family="'IBM Plex Mono',monospace" font-size="52" font-weight="600" fill="#F5F3EE">${score}</text>
      <text x="${cx}" y="${cy + 20}" text-anchor="middle" font-family="'IBM Plex Sans',sans-serif" font-size="12.5" letter-spacing="2" fill="#9AA5B1">SCORE DE EXCELÊNCIA</text>
    </svg>`;
}
