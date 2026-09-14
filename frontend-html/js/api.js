/* =====================================================================
   KeroPro — api.js
   Cliente HTTP para a API Java (Spring Boot). Se a API não responder
   (backend não iniciado, CORS, etc.), cada função devolve null e quem
   chamou decide usar os dados de mock-data.js como alternativa.
===================================================================== */

const API_BASE = "http://localhost:8080/api";

async function apiRequest(path, options = {}) {
  try {
    const res = await fetch(`${API_BASE}${path}`, {
      headers: { "Content-Type": "application/json" },
      ...options,
    });
    if (!res.ok) return null;
    return await res.json();
  } catch (err) {
    return null; // API offline — quem chamou usa o fallback de mock-data.js
  }
}

const api = {
  login: (email, senha) =>
    apiRequest("/auth/login", { method: "POST", body: JSON.stringify({ email, senha }) }),

  getCategorias: () => apiRequest("/categorias"),

  getProfissionais: ({ categoria, clienteLat, clienteLng, emergencia } = {}) => {
    const params = new URLSearchParams();
    if (categoria) params.set("categoria", categoria);
    if (clienteLat != null) params.set("clienteLat", clienteLat);
    if (clienteLng != null) params.set("clienteLng", clienteLng);
    if (emergencia != null) params.set("emergencia", emergencia);
    return apiRequest(`/profissionais?${params.toString()}`);
  },

  getPedidosPendentes: (profissionalId) => apiRequest(`/pedidos/pendentes/${profissionalId}`),

  criarPedido: (payload) => apiRequest("/pedidos", { method: "POST", body: JSON.stringify(payload) }),

  avancarStatusPedido: (pedidoId) => apiRequest(`/pedidos/${pedidoId}/avancar`, { method: "PATCH" }),
};
