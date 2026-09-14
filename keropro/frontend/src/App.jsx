import React, { useState } from "react";
import Splash from "./pages/Splash";
import Role from "./pages/Role";
import ClientHome from "./pages/ClientHome";
import ProDetail from "./pages/ProDetail";
import ClientTracking from "./pages/ClientTracking";
import ProDashboard from "./pages/ProDashboard";
import ProJob from "./pages/ProJob";
import { api } from "./api/api";

const CLIENTE_ID = 1; // Mariana Oliveira (seed.sql)

/** Orquestra a navegação entre telas — o roteamento por estado é suficiente
 *  para este protótipo; em produção considerar react-router-dom. */
export default function App() {
  const [screen, setScreen] = useState("splash");
  const [selectedPro, setSelectedPro] = useState(null);
  const [pedidoCliente, setPedidoCliente] = useState(null);
  const [job, setJob] = useState(null);

  const reset = () => {
    setSelectedPro(null);
    setPedidoCliente(null);
    setJob(null);
    setScreen("role");
  };

  const contratar = async () => {
    const pedido = await api.criarPedido({
      clienteId: CLIENTE_ID,
      profissionalId: selectedPro.id,
      descricao: `Solicitação de ${selectedPro.especialidade.toLowerCase()}`,
      clienteLatitude: -23.495,
      clienteLongitude: -47.458,
      emergencia: false,
    });
    setPedidoCliente(pedido);
    setScreen("clientTracking");
  };

  return (
    <div className="kp-app">
      <div className="kp-frame">
        {screen === "splash" && <Splash onDone={() => setScreen("role")} />}

        {screen === "role" && (
          <Role onEnter={(role) => setScreen(role === "cliente" ? "clientHome" : "proDashboard")} />
        )}

        {screen === "clientHome" && (
          <ClientHome onOpenPro={(p) => { setSelectedPro(p); setScreen("proDetail"); }} onLogout={reset} />
        )}

        {screen === "proDetail" && selectedPro && (
          <ProDetail pro={selectedPro} onBack={() => setScreen("clientHome")} onContratar={contratar} />
        )}

        {screen === "clientTracking" && selectedPro && pedidoCliente && (
          <ClientTracking
            pro={selectedPro}
            pedido={pedidoCliente}
            onFinish={() => { setSelectedPro(null); setPedidoCliente(null); setScreen("clientHome"); }}
          />
        )}

        {screen === "proDashboard" && (
          <ProDashboard onAceitar={(p) => { setJob(p); setScreen("proJob"); }} onLogout={reset} />
        )}

        {screen === "proJob" && job && (
          <ProJob job={job} onFinish={() => { setJob(null); setScreen("proDashboard"); }} />
        )}
      </div>
    </div>
  );
}
