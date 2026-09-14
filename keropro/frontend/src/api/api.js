// =====================================================================
// KeroPro — cliente HTTP para a API Java (Spring Boot)
// Centraliza todas as chamadas fetch usadas pelas páginas React.
// =====================================================================
const BASE_URL = process.env.REACT_APP_API_URL || "http://localhost:8080/api";

async function request(path, options = {}) {
  const res = await fetch(`${BASE_URL}${path}`, {
    headers: { "Content-Type": "application/json" },
    ...options,
  });
  if (!res.ok) {
    throw new Error(`Falha na requisição ${path}: ${res.status}`);
  }
  return res.json();
}

export const api = {
  // Auth
  login: (email, senha) =>
    request("/auth/login", { method: "POST", body: JSON.stringify({ email, senha }) }),

  // Categorias
  getCategorias: () => request("/categorias"),

  // Profissionais ranqueados por Score de Excelência
  getProfissionais: ({ categoria, clienteLat, clienteLng, emergencia } = {}) => {
    const params = new URLSearchParams();
    if (categoria) params.set("categoria", categoria);
    if (clienteLat != null) params.set("clienteLat", clienteLat);
    if (clienteLng != null) params.set("clienteLng", clienteLng);
    if (emergencia != null) params.set("emergencia", emergencia);
    return request(`/profissionais?${params.toString()}`);
  },

  // Pedidos
  getPedidosPendentes: (profissionalId) => request(`/pedidos/pendentes/${profissionalId}`),

  criarPedido: (payload) =>
    request("/pedidos", { method: "POST", body: JSON.stringify(payload) }),

  avancarStatusPedido: (pedidoId) =>
    request(`/pedidos/${pedidoId}/avancar`, { method: "PATCH" }),
};
