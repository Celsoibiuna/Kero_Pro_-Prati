/* =====================================================================
   KeroPro — auth-api.js
   Cliente HTTP para os endpoints de cadastro da API Java (backend/).
===================================================================== */
const AUTH_API_BASE = "http://localhost:8080/api";

async function postCadastro(path, payload) {
  try {
    const res = await fetch(`${AUTH_API_BASE}${path}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });
    const data = await res.json().catch(() => null);
    if (!res.ok) {
      return { ok: false, offline: false, error: (data && data.mensagem) || "Não foi possível concluir o cadastro." };
    }
    return { ok: true, data };
  } catch (err) {
    // API Java não está rodando / inacessível
    return { ok: false, offline: true, error: "Não foi possível conectar à API. Verifique se o back-end está em execução." };
  }
}
