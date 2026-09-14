/* =====================================================================
   KeroPro — app.js
   Controlador principal: estado da aplicação, navegação entre telas e
   ligação dos eventos de clique. Sem frameworks — DOM puro.
===================================================================== */

const root = document.getElementById("screen-root");

const state = {
  screen: "splash",
  categoria: null,
  emergencia: false,
  profissionais: [],
  selectedPro: null,
  pedidoCliente: null,
  disponivel: true,
  pedidosPendentes: [],
  job: null,
  offline: false,
};

const CLIENTE_ID = 1;       // Mariana Oliveira (seed.sql)
const PROFISSIONAL_ID = 2;  // Carlos Henrique Santos (seed.sql)

const ORDEM_STATUS = ["PENDENTE", "ACEITO", "A_CAMINHO", "EM_EXECUCAO", "CONCLUIDO"];
const CLIENT_STEPS = ["Aceito", "A caminho", "Em execução", "Concluído"];
const CLIENT_TEXTOS = {
  PENDENTE: "O profissional aceitou seu pedido.",
  ACEITO: "O profissional aceitou seu pedido.",
  A_CAMINHO: "Técnico a caminho — chegada estimada em 12 min.",
  EM_EXECUCAO: "Serviço em execução no local.",
  CONCLUIDO: "Serviço concluído com sucesso!",
};
const PRO_STEPS = ["A caminho", "Cheguei", "Em execução", "Concluído"];
const PRO_TEXTOS = ["Trajeto iniciado até o cliente.", "Chegada confirmada no endereço.", "Serviço em andamento.", "Pagamento liberado para sua conta."];

function statusIndex(status) {
  return Math.max(0, ORDEM_STATUS.indexOf(status) - 1);
}

function goTo(screen) {
  state.screen = screen;
  render();
}

function resetSession() {
  Object.assign(state, {
    selectedPro: null, pedidoCliente: null, job: null,
    categoria: null, emergencia: false, screen: "role",
  });
  render();
}

/* ---------------------------------------------------------------
   Carregamento de dados (API Java -> fallback mock-data.js)
---------------------------------------------------------------- */
async function carregarProfissionais() {
  const remoto = await api.getProfissionais({
    categoria: state.categoria, clienteLat: CLIENTE_LAT, clienteLng: CLIENTE_LNG, emergencia: state.emergencia,
  });
  if (remoto) {
    state.offline = false;
    state.profissionais = remoto.map((p) => ({ ...p, avaliacoesScore: p.avaliacoesScore ?? p.avaliacoes }));
  } else {
    state.offline = true;
    state.profissionais = montarProfissionaisMock({ categoria: state.categoria, emergencia: state.emergencia });
  }
  render();
}

async function carregarPedidosPendentes() {
  const remoto = await api.getPedidosPendentes(PROFISSIONAL_ID);
  if (remoto) {
    state.offline = false;
    state.pedidosPendentes = remoto.map((p) => ({
      id: p.id, clienteNome: p.cliente?.usuario?.nome || "Cliente",
      descricao: p.descricao, distanciaKm: p.distanciaKm, valorEstimado: p.valorEstimado, emergencia: p.emergencia,
    }));
  } else {
    state.offline = true;
    state.pedidosPendentes = MOCK_PEDIDOS_PENDENTES;
  }
  render();
}

async function contratar() {
  const pro = state.selectedPro;
  const payload = {
    clienteId: CLIENTE_ID, profissionalId: pro.id,
    descricao: `Solicitação de ${pro.especialidade.toLowerCase()}`,
    clienteLatitude: CLIENTE_LAT, clienteLongitude: CLIENTE_LNG, emergencia: state.emergencia,
  };
  const remoto = await api.criarPedido(payload);
  state.pedidoCliente = remoto || {
    id: `mock-${Date.now()}`, status: "PENDENTE",
    valorEstimado: pro.precoEstimado, distanciaKm: pro.distanciaKm, emergencia: state.emergencia,
  };
  state.offline = !remoto;
  goTo("clientTracking");
}

async function avancarPedidoCliente() {
  const pedido = state.pedidoCliente;
  const remoto = await api.avancarStatusPedido(pedido.id);
  if (remoto) {
    state.pedidoCliente = remoto;
  } else {
    const idx = ORDEM_STATUS.indexOf(pedido.status);
    pedido.status = ORDEM_STATUS[Math.min(idx + 1, ORDEM_STATUS.length - 1)];
  }
  render();
}

async function avancarJob() {
  const job = state.job;
  const remoto = await api.avancarStatusPedido(job.id);
  if (remoto) {
    state.job = { ...job, ...remoto };
  } else {
    const idx = ORDEM_STATUS.indexOf(job.status || "PENDENTE");
    job.status = ORDEM_STATUS[Math.min(idx + 1, ORDEM_STATUS.length - 1)];
  }
  render();
}

/* ---------------------------------------------------------------
   Renderização de cada tela (HTML puro, via ui.js)
---------------------------------------------------------------- */
function renderSplash() {
  root.innerHTML = `
    <div class="kp-screen kp-splash" data-action="splash-continue">
      <div class="kp-splash-mark">
        <svg width="96" height="96" viewBox="0 0 96 96">
          <circle cx="48" cy="48" r="42" fill="none" stroke="#2A3A4E" stroke-width="3"/>
          <path d="M 48 6 A 42 42 0 0 1 88 55" fill="none" stroke="#f5a623" stroke-width="3" stroke-linecap="round" class="kp-spin"/>
        </svg>
        <img src="assets/logo-mark.png" alt="KeroPro" class="kp-splash-logo" />
      </div>
      <div class="kp-splash-title">KeroPro</div>
      <div class="kp-splash-tag">Serviço técnico, validado por competência.</div>
      <div class="kp-splash-hint kp-mono">toque para continuar</div>
    </div>`;
  setTimeout(() => { if (state.screen === "splash") goTo("role"); }, 2000);
}

function renderRole() {
  root.innerHTML = `
    <div class="kp-screen">
      ${buildTopBar({ title: "Bem-vindo" })}
      <div class="kp-role-body">
        <div class="kp-eyebrow">ENTRAR COMO</div>
        <button class="kp-role-card" data-action="enter-cliente">
          ${ICON.circleUser}
          <div><div class="kp-role-card-title">Cliente</div><div class="kp-role-card-sub">Continuar como Mariana Oliveira</div></div>
          <span class="kp-role-chev">${ICON.chevronRight}</span>
        </button>
        <button class="kp-role-card kp-role-card-alt" data-action="enter-profissional">
          ${ICON.briefcase}
          <div><div class="kp-role-card-title">Profissional</div><div class="kp-role-card-sub">Continuar como Carlos H. Santos</div></div>
          <span class="kp-role-chev">${ICON.chevronRight}</span>
        </button>
        <div class="kp-role-footnote">${ICON.shield} Login via API Java (/api/auth/login) · dados em MySQL</div>
      </div>
    </div>`;
}

function renderClientHome() {
  const chips = MOCK_CATEGORIAS.map((c) => `
    <button class="kp-chip ${state.categoria === c.id ? "kp-chip-on" : ""}" data-action="toggle-categoria" data-id="${c.id}">
      ${ICON[CATEGORY_ICON[c.id]]} ${c.label}
    </button>`).join("");

  const lista = state.profissionais.map(buildProCard).join("") || `<div class="kp-loading">Buscando profissionais…</div>`;

  root.innerHTML = `
    <div class="kp-screen">
      ${buildTopBar({ title: "Olá, Mariana", subtitle: "Sorocaba, SP", logout: true })}
      <div class="kp-scroll">
        ${state.offline ? buildOfflineBanner() : ""}
        <div class="kp-search">${ICON.search}<span>Que serviço você precisa agora?</span></div>
        <div class="kp-chips">${chips}</div>
        <button class="kp-emergency ${state.emergencia ? "kp-emergency-on" : ""}" data-action="toggle-emergencia">
          <div class="kp-emergency-left">
            ${ICON.sparkles}
            <div><div class="kp-emergency-title">Emergência em até 1h</div><div class="kp-emergency-sub">Prioriza técnicos com resposta imediata</div></div>
          </div>
          <div class="kp-toggle ${state.emergencia ? "kp-toggle-on" : ""}"><div class="kp-toggle-knob"></div></div>
        </button>
        <div class="kp-eyebrow" style="margin-top:18px">PROFISSIONAIS PRÓXIMOS · RANQUEADOS POR SCORE</div>
        ${lista}
      </div>
    </div>`;
  carregarProfissionais();
}

function renderProDetail() {
  const pro = state.selectedPro;
  root.innerHTML = `
    <div class="kp-screen">
      ${buildTopBar({ title: pro.nome, subtitle: pro.especialidade, back: true })}
      <div class="kp-scroll">
        <div class="kp-gauge-card">${buildGauge(pro.score)}<div class="kp-gauge-caption">Validado por Content-Based Filtering · formação, certificações e histórico</div></div>
        <div class="kp-eyebrow">COMPOSIÇÃO DO SCORE</div>
        <div class="kp-panel">
          ${buildBar("Formação acadêmica", pro.formacao, "#f5a623")}
          ${buildBar("Certificações técnicas", pro.certificacoes, "#00a3e0")}
          ${buildBar("Avaliações de clientes", pro.avaliacoesScore, "#E2574C")}
          ${buildBar("Tempo de resposta", pro.tempoResposta, "#8B8FF7")}
        </div>
        <div class="kp-eyebrow">PORTFÓLIO</div>
        <div class="kp-portfolio">
          ${[0, 1, 2].map(() => `<div class="kp-portfolio-tile" style="background:#f5a62322;border-color:#f5a62355">${ICON.wrenchLg}</div>`).join("")}
        </div>
        <div class="kp-eyebrow">ORÇAMENTO ESTIMADO</div>
        <div class="kp-panel">
          <div class="kp-price-row"><span>Valor base do serviço</span><span class="kp-mono">R$ ${Number(pro.precoBase).toFixed(2)}</span></div>
          <div class="kp-price-row"><span>Deslocamento · ${pro.distanciaKm} km</span><span class="kp-mono">calculado via GPS</span></div>
          <div class="kp-price-row kp-price-total"><span>Total estimado</span><span class="kp-mono">R$ ${Number(pro.precoEstimado).toFixed(2)}</span></div>
          <div class="kp-price-note">${ICON.navigation} calculado no back-end (OrcamentoService.java)</div>
        </div>
      </div>
      <div class="kp-cta-bar"><button class="kp-btn-primary" data-action="contratar">Contratar agora ${ICON.chevronRight}</button></div>
    </div>`;
}

function renderClientTracking() {
  const pro = state.selectedPro;
  const pedido = state.pedidoCliente;
  const idx = statusIndex(pedido.status || "PENDENTE");
  const done = pedido.status === "CONCLUIDO";
  const iniciais = pro.nome.split(" ").map((n) => n[0]).slice(0, 2).join("");

  root.innerHTML = `
    <div class="kp-screen">
      ${buildTopBar({ title: "Acompanhamento", subtitle: "Sincronização em tempo real" })}
      <div class="kp-scroll">
        ${state.offline ? buildOfflineBanner() : ""}
        ${buildMap(mapPinHome(28, 62) + mapPinPro(58 - idx * 8, 30 + idx * 6))}
        <div class="kp-panel">
          <div class="kp-status-headline">${CLIENT_TEXTOS[pedido.status] || CLIENT_TEXTOS.PENDENTE}</div>
          ${buildStepper(CLIENT_STEPS, idx)}
        </div>
        <div class="kp-panel kp-pro-mini">
          <div class="kp-avatar" style="background:#f5a623">${iniciais}</div>
          <div class="kp-pro-info">
            <div class="kp-pro-name">${pro.nome}</div>
            <div class="kp-pro-sub kp-mono">Score ${pro.score} · R$ ${Number(pedido.valorEstimado).toFixed(2)}</div>
          </div>
        </div>
      </div>
      <div class="kp-cta-bar">
        ${done
          ? `<button class="kp-btn-primary" data-action="finish-tracking">Avaliar e voltar ao início</button>`
          : `<button class="kp-btn-primary" data-action="avancar-cliente">Simular próxima atualização</button>`}
      </div>
    </div>`;
}

function renderProDashboard() {
  const lista = state.pedidosPendentes.map((p) => `
    <div class="kp-panel kp-req-card">
      <div class="kp-avatar kp-avatar-alt">${p.clienteNome.split(" ").map((n) => n[0]).slice(0, 2).join("")}</div>
      <div class="kp-pro-info">
        <div class="kp-pro-name">${p.clienteNome} ${p.emergencia ? `<span class="kp-badge-emerg">URGENTE</span>` : ""}</div>
        <div class="kp-pro-sub">${p.descricao}</div>
        <div class="kp-pro-meta"><span>${ICON.mapPin} ${p.distanciaKm} km</span><span class="kp-mono">R$ ${Number(p.valorEstimado).toFixed(2)}</span></div>
      </div>
      <button class="kp-btn-small" data-action="aceitar-pedido" data-id="${p.id}">Aceitar</button>
    </div>`).join("") || `<div class="kp-loading">Nenhum pedido pendente no momento.</div>`;

  root.innerHTML = `
    <div class="kp-screen">
      ${buildTopBar({ title: "Olá, Carlos", subtitle: "Painel do profissional", logout: true })}
      <div class="kp-scroll">
        ${state.offline ? buildOfflineBanner() : ""}
        <div class="kp-panel kp-pro-header">
          ${buildGauge(92, 128)}
          <div class="kp-pro-header-stats">
            <div class="kp-stat">${ICON.trendingUp} R$ 1.420 <span>esta semana</span></div>
            <div class="kp-stat">${ICON.star} 4.9 <span>118 avaliações</span></div>
            <div class="kp-stat">${ICON.check} 63 <span>serviços concluídos</span></div>
          </div>
        </div>
        <button class="kp-emergency ${state.disponivel ? "kp-emergency-on" : ""}" data-action="toggle-disponivel">
          <div class="kp-emergency-left">
            ${ICON.power}
            <div><div class="kp-emergency-title">${state.disponivel ? "Disponível para chamados" : "Indisponível no momento"}</div><div class="kp-emergency-sub">Visível para clientes em Sorocaba e região</div></div>
          </div>
          <div class="kp-toggle ${state.disponivel ? "kp-toggle-on" : ""}"><div class="kp-toggle-knob"></div></div>
        </button>
        <div class="kp-eyebrow" style="margin-top:18px">PEDIDOS NA SUA REGIÃO</div>
        ${lista}
      </div>
    </div>`;
  carregarPedidosPendentes();
}

function renderProJob() {
  const job = state.job;
  const idx = statusIndex(job.status || "PENDENTE");
  const done = job.status === "CONCLUIDO";

  root.innerHTML = `
    <div class="kp-screen">
      ${buildTopBar({ title: job.clienteNome, subtitle: job.descricao })}
      <div class="kp-scroll">
        ${state.offline ? buildOfflineBanner() : ""}
        ${buildMap(mapPinHome(66, 34) + mapPinPro(30 + idx * 9, 64 - idx * 8))}
        <div class="kp-panel">
          <div class="kp-status-headline">${PRO_TEXTOS[Math.min(idx, 3)]}</div>
          ${buildStepper(PRO_STEPS, idx)}
        </div>
        <div class="kp-panel">
          <div class="kp-price-row"><span>Valor do serviço</span><span class="kp-mono">R$ ${Number(job.valorEstimado).toFixed(2)}</span></div>
          <div class="kp-price-row"><span>Distância até o cliente</span><span class="kp-mono">${job.distanciaKm} km</span></div>
        </div>
      </div>
      <div class="kp-cta-bar">
        ${done
          ? `<button class="kp-btn-primary" data-action="finish-job">Concluir e voltar ao painel</button>`
          : `<button class="kp-btn-primary" data-action="avancar-job">Avançar status</button>`}
      </div>
    </div>`;
}

function render() {
  const renderers = {
    splash: renderSplash, role: renderRole, clientHome: renderClientHome,
    proDetail: renderProDetail, clientTracking: renderClientTracking,
    proDashboard: renderProDashboard, proJob: renderProJob,
  };
  (renderers[state.screen] || renderRole)();
}

/* ---------------------------------------------------------------
   Delegação de eventos — um único listener cuida de todos os cliques
---------------------------------------------------------------- */
root.addEventListener("click", (ev) => {
  const el = ev.target.closest("[data-action]");
  if (!el) return;
  const action = el.dataset.action;
  const id = el.dataset.id;

  switch (action) {
    case "splash-continue": goTo("role"); break;
    case "enter-cliente": goTo("clientHome"); break;
    case "enter-profissional": goTo("proDashboard"); break;
    case "back": goTo(state.screen === "proDetail" ? "clientHome" : "role"); break;
    case "logout": resetSession(); break;

    case "toggle-categoria":
      state.categoria = state.categoria === id ? null : id;
      renderClientHome();
      break;
    case "toggle-emergencia":
      state.emergencia = !state.emergencia;
      renderClientHome();
      break;
    case "open-pro":
      state.selectedPro = state.profissionais.find((p) => String(p.id) === id);
      goTo("proDetail");
      break;
    case "contratar":
      contratar();
      break;
    case "avancar-cliente":
      avancarPedidoCliente();
      break;
    case "finish-tracking":
      state.selectedPro = null; state.pedidoCliente = null;
      goTo("clientHome");
      break;

    case "toggle-disponivel":
      state.disponivel = !state.disponivel;
      renderProDashboard();
      break;
    case "aceitar-pedido":
      state.job = state.pedidosPendentes.find((p) => String(p.id) === id);
      state.job.status = state.job.status || "PENDENTE";
      goTo("proJob");
      break;
    case "avancar-job":
      avancarJob();
      break;
    case "finish-job":
      state.job = null;
      goTo("proDashboard");
      break;
  }
});

render();
