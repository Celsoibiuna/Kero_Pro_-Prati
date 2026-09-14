/* =====================================================================
   KeroPro — ui.js
   Funções puras que devolvem trechos de HTML (strings), usadas por
   app.js para montar cada tela. Nenhuma delas mexe em estado global.
===================================================================== */

/* Ícones em SVG inline (equivalentes aos usados na versão React/lucide-react) */
const ICON = {
  chevronLeft: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M15 18l-6-6 6-6"/></svg>`,
  chevronRight: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 18l6-6-6-6"/></svg>`,
  logout: `<svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><path d="M16 17l5-5-5-5"/><path d="M21 12H9"/></svg>`,
  search: `<svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><path d="M21 21l-4.3-4.3"/></svg>`,
  sparkles: `<svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 3v4M12 17v4M3 12h4M17 12h4M6 6l2 2M16 16l2 2M6 18l2-2M16 8l2-2"/></svg>`,
  shield: `<svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/><path d="M9 12l2 2 4-4"/></svg>`,
  star: `<svg width="12" height="12" viewBox="0 0 24 24" fill="#f5a623" stroke="#f5a623" stroke-width="1"><polygon points="12 2 15 9 22 9.5 17 14.5 18.5 22 12 18 5.5 22 7 14.5 2 9.5 9 9"/></svg>`,
  mapPin: `<svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></svg>`,
  mapPinLg: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></svg>`,
  wrench: `<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14.7 6.3a4 4 0 1 0-5.4 5.4L4 17l3 3 5.3-5.3a4 4 0 0 0 5.4-5.4l-2.8 2.8-2-2z"/></svg>`,
  wrenchLg: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14.7 6.3a4 4 0 1 0-5.4 5.4L4 17l3 3 5.3-5.3a4 4 0 0 0 5.4-5.4l-2.8 2.8-2-2z"/></svg>`,
  radar: `<svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M19.07 4.93A10 10 0 1 0 22 12"/><path d="M12 12l6-3"/><circle cx="12" cy="12" r="1"/></svg>`,
  power: `<svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2v10"/><path d="M18.4 6.6a9 9 0 1 1-12.8 0"/></svg>`,
  trendingUp: `<svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="23 6 13.5 15.5 8.5 10.5 1 18"/><polyline points="17 6 23 6 23 12"/></svg>`,
  check: `<svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 6L9 17l-5-5"/></svg>`,
  circleUser: `<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><circle cx="12" cy="10" r="3"/><path d="M6.5 19a6 6 0 0 1 11 0"/></svg>`,
  briefcase: `<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"/></svg>`,
  navigation: `<svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="3 11 22 2 13 21 11 13 3 11"/></svg>`,
  zap: `<svg width="13" height="13" viewBox="0 0 24 24" fill="currentColor"><polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/></svg>`,
  droplet: `<svg width="13" height="13" viewBox="0 0 24 24" fill="currentColor"><path d="M12 2s7 8 7 13a7 7 0 1 1-14 0c0-5 7-13 7-13z"/></svg>`,
  wifi: `<svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12.5a11 11 0 0 1 14 0"/><path d="M8.5 16a6 6 0 0 1 7 0"/><circle cx="12" cy="19" r="1"/></svg>`,
  wind: `<svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 8h11a3 3 0 1 0-3-3"/><path d="M3 16h13a3 3 0 1 1-3 3"/></svg>`,
  hammer: `<svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M15 12l-8.5 8.5a1.5 1.5 0 0 1-2-2L13 10"/><path d="M13 10l6-6 3 3-6 6z"/></svg>`,
};

const CATEGORY_ICON = { eletrica: "zap", hidraulica: "droplet", ti: "wifi", clima: "wind", reforma: "hammer" };

/* ---------- Gauge (Score de Excelência) — mesma matemática da versão React ---------- */
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
function buildGauge(score, size = 168, accent = "#f5a623") {
  const cx = size / 2, cy = size / 2 + 6, r = size / 2 - 18;
  const pct = Math.max(0, Math.min(100, score)) / 100;
  const trackPath = describeArc(cx, cy, r, 0, 180);
  const valuePath = describeArc(cx, cy, r, 0, 180 * pct);
  const ticks = Array.from({ length: 11 }, (_, i) => i * 18)
    .map((a) => {
      const p1 = polarToCartesian(cx, cy, r + 8, a);
      const p2 = polarToCartesian(cx, cy, r + 2, a);
      return `<line x1="${p1.x}" y1="${p1.y}" x2="${p2.x}" y2="${p2.y}" stroke="#3A465A" stroke-width="2"/>`;
    }).join("");
  return `
    <svg width="${size}" height="${size / 2 + 40}" viewBox="0 0 ${size} ${size / 2 + 40}">
      ${ticks}
      <path d="${trackPath}" fill="none" stroke="#232F40" stroke-width="14" stroke-linecap="round"/>
      <path d="${valuePath}" fill="none" stroke="${accent}" stroke-width="14" stroke-linecap="round"/>
      <text x="${cx}" y="${cy - 6}" text-anchor="middle" font-family="'IBM Plex Mono',monospace" font-size="34" font-weight="600" fill="#F5F3EE">${score}</text>
      <text x="${cx}" y="${cy + 16}" text-anchor="middle" font-family="'IBM Plex Sans',sans-serif" font-size="10.5" letter-spacing="1.5" fill="#9AA5B1">SCORE DE EXCELÊNCIA</text>
    </svg>`;
}

/* ---------- Stepper (status do pedido) ---------- */
function buildStepper(steps, active) {
  return `<div class="kp-stepper">` + steps.map((label, i) => `
    <div class="kp-step">
      <div class="kp-step-dot ${i <= active ? "kp-step-dot-on" : ""}">${i < active ? ICON.check : `<span class="kp-mono">${i + 1}</span>`}</div>
      <div class="kp-step-label ${i <= active ? "kp-step-label-on" : ""}">${label}</div>
      ${i < steps.length - 1 ? `<div class="kp-step-line ${i < active ? "kp-step-line-on" : ""}"></div>` : ""}
    </div>`).join("") + `</div>`;
}

/* ---------- Mapa mock ---------- */
function buildMap(pinsHtml) {
  return `
    <div class="kp-map">
      <div class="kp-map-grid"></div>
      ${pinsHtml}
      <div class="kp-map-coords kp-mono">-23.5015, -47.4526</div>
      <div class="kp-map-badge">${ICON.radar} OpenStreetMap · GPS ao vivo</div>
    </div>`;
}
function mapPinHome(left, top) {
  return `<div class="kp-map-pin kp-map-pin-home" style="left:${left}%;top:${top}%">${ICON.mapPinLg}</div>`;
}
function mapPinPro(left, top, color = "#f5a623") {
  return `<div class="kp-map-pin" style="left:${left}%;top:${top}%;background:${color}">${ICON.wrench}</div>`;
}

/* ---------- Card de profissional ---------- */
function buildProCard(pro) {
  const iniciais = pro.nome.split(" ").map((n) => n[0]).slice(0, 2).join("");
  return `
    <button class="kp-pro-card" data-action="open-pro" data-id="${pro.id}">
      <div class="kp-avatar" style="background:#f5a623">${iniciais}</div>
      <div class="kp-pro-info">
        <div class="kp-pro-name">${pro.nome} ${pro.verificado ? `<span class="kp-verified">${ICON.shield}</span>` : ""}</div>
        <div class="kp-pro-sub">${pro.especialidade}</div>
        <div class="kp-pro-meta">
          <span>${ICON.star} 4.8</span>
          <span>${ICON.mapPin} ${pro.distanciaKm} km</span>
        </div>
      </div>
      <div class="kp-pro-score">
        <div class="kp-pro-score-num kp-mono">${pro.score}</div>
        <div class="kp-pro-score-label">score</div>
      </div>
    </button>`;
}

/* ---------- Barra de composição do score ---------- */
function buildBar(label, value, accent) {
  return `
    <div class="kp-bar-row">
      <div class="kp-bar-label"><span>${label}</span><span class="kp-mono">${value}</span></div>
      <div class="kp-bar-track"><div class="kp-bar-fill" style="width:${value}%;background:${accent}"></div></div>
    </div>`;
}

/* ---------- Top bar ---------- */
function buildTopBar({ title, subtitle = "", back = false, logout = false }) {
  const left = back
    ? `<button class="kp-icon-btn" data-action="back">${ICON.chevronLeft}</button>`
    : `<div class="kp-brand"><img src="assets/logo-mark.png" alt="" />KeroPro</div>`;
  const right = logout
    ? `<button class="kp-icon-btn" data-action="logout">${ICON.logout}</button>`
    : `<div style="width:34px"></div>`;
  return `
    <div class="kp-topbar">
      ${left}
      <div class="kp-topbar-center">
        <div class="kp-topbar-title">${title}</div>
        ${subtitle ? `<div class="kp-topbar-subtitle">${subtitle}</div>` : ""}
      </div>
      ${right}
    </div>`;
}

/* ---------- Aviso de modo offline (quando a API Java não respondeu) ---------- */
function buildOfflineBanner() {
  return `<div class="kp-offline-banner">${ICON.radar} Modo offline · exibindo dados de demonstração</div>`;
}
