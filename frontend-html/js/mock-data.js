/* =====================================================================
   KeroPro — mock-data.js
   Dados de demonstração usados quando a API Java (backend/) não está
   rodando, para que este front-end funcione mesmo abrindo o index.html
   direto no navegador. Quando a API responde, estes dados são ignorados.
===================================================================== */

const MOCK_CATEGORIAS = [
  { id: "eletrica", label: "Elétrica" },
  { id: "hidraulica", label: "Hidráulica" },
  { id: "ti", label: "TI & Redes" },
  { id: "clima", label: "Ar-condicionado" },
  { id: "reforma", label: "Reformas" },
];

// Coordenadas de referência do cliente (Sorocaba/SP) — em produção viriam do GPS do dispositivo.
const CLIENTE_LAT = -23.4950;
const CLIENTE_LNG = -47.4580;

const MOCK_PROFISSIONAIS = [
  {
    id: 2, nome: "Carlos Henrique Santos", categoria: "eletrica", especialidade: "Técnico Eletricista",
    latitude: -23.4980, longitude: -47.4550, precoBase: 60, verificado: true,
    formacao: 95, certificacoes: 90, avaliacoesScore: 94, tempoResposta: 88,
  },
  {
    id: 3, nome: "Diego Fontoura", categoria: "ti", especialidade: "Técnico em Redes",
    latitude: -23.4930, longitude: -47.4480, precoBase: 90, verificado: true,
    formacao: 88, certificacoes: 84, avaliacoesScore: 86, tempoResposta: 80,
  },
  {
    id: 4, nome: "Renata Alves", categoria: "hidraulica", especialidade: "Encanadora",
    latitude: -23.5050, longitude: -47.4600, precoBase: 70, verificado: true,
    formacao: 70, certificacoes: 76, avaliacoesScore: 88, tempoResposta: 74,
  },
  {
    id: 5, nome: "Marcos Vinícius", categoria: "clima", especialidade: "Técnico em Refrigeração",
    latitude: -23.4870, longitude: -47.4400, precoBase: 95, verificado: false,
    formacao: 60, certificacoes: 68, avaliacoesScore: 78, tempoResposta: 72,
  },
];

const MOCK_PEDIDOS_PENDENTES = [
  { id: 101, clienteNome: "Mariana Oliveira", descricao: "Reparo em tomada", distanciaKm: 1.2, valorEstimado: 85, emergencia: true },
  { id: 102, clienteNome: "Beatriz Lima", descricao: "Troca de disjuntor", distanciaKm: 0.8, valorEstimado: 70, emergencia: true },
  { id: 103, clienteNome: "Fernando Costa", descricao: "Instalação de chuveiro elétrico", distanciaKm: 3.4, valorEstimado: 110, emergencia: false },
];

/* ------------ replica em JS as fórmulas do back-end (para o modo offline) ------------ */

// Mesma fórmula de Haversine usada em OrcamentoService.java
function calcularDistanciaKm(lat1, lon1, lat2, lon2) {
  const R = 6371;
  const dLat = ((lat2 - lat1) * Math.PI) / 180;
  const dLon = ((lon2 - lon1) * Math.PI) / 180;
  const a =
    Math.sin(dLat / 2) ** 2 +
    Math.cos((lat1 * Math.PI) / 180) * Math.cos((lat2 * Math.PI) / 180) * Math.sin(dLon / 2) ** 2;
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  return R * c;
}

// Mesmos pesos do ScoreService.java (Content-Based Filtering)
function calcularScoreTotal({ formacao, certificacoes, avaliacoesScore, tempoResposta }) {
  const total = formacao * 0.30 + certificacoes * 0.25 + avaliacoesScore * 0.30 + tempoResposta * 0.15;
  return Math.round(total);
}

// Mesma regra de precificação do OrcamentoService.java
function calcularPrecoEstimado(precoBase, distanciaKm, emergencia) {
  const TAXA_KM = 8;
  let total = precoBase + distanciaKm * TAXA_KM;
  if (emergencia) total *= 1.25;
  return Math.round(total * 100) / 100;
}

function montarProfissionaisMock({ categoria, emergencia }) {
  return MOCK_PROFISSIONAIS
    .filter((p) => !categoria || p.categoria === categoria)
    .map((p) => {
      const distanciaKm = Math.round(calcularDistanciaKm(CLIENTE_LAT, CLIENTE_LNG, p.latitude, p.longitude) * 10) / 10;
      return {
        ...p,
        score: calcularScoreTotal(p),
        distanciaKm,
        precoEstimado: calcularPrecoEstimado(p.precoBase, distanciaKm, !!emergencia),
      };
    })
    .sort((a, b) => b.score - a.score);
}
