import React, { useEffect, useState } from "react";
import { TrendingUp, Star, CheckCircle2, Power, MapPin } from "lucide-react";
import TopBar from "../components/TopBar";
import Gauge from "../components/Gauge";
import { api } from "../api/api";

const PROFISSIONAL_ID = 2; // Carlos Henrique Santos (seed.sql)

export default function ProDashboard({ onAceitar, onLogout }) {
  const [disponivel, setDisponivel] = useState(true);
  const [pedidos, setPedidos] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  useEffect(() => {
    api
      .getPedidosPendentes(PROFISSIONAL_ID)
      .then(setPedidos)
      .catch(() => setErro("Não foi possível carregar os pedidos. Verifique se a API Java está rodando."))
      .finally(() => setCarregando(false));
  }, []);

  return (
    <div className="kp-screen">
      <TopBar title="Olá, Carlos" subtitle="Painel do profissional" onLogout={onLogout} />
      <div className="kp-scroll">
        <div className="kp-panel kp-pro-header">
          <Gauge score={92} size={128} accent="#f5a623" />
          <div className="kp-pro-header-stats">
            <div className="kp-stat"><TrendingUp size={13} /> R$ 1.420 <span>esta semana</span></div>
            <div className="kp-stat"><Star size={13} fill="#f5a623" color="#f5a623" /> 4.9 <span>118 avaliações</span></div>
            <div className="kp-stat"><CheckCircle2 size={13} /> 63 <span>serviços concluídos</span></div>
          </div>
        </div>

        <button className={`kp-emergency ${disponivel ? "kp-emergency-on" : ""}`} onClick={() => setDisponivel(!disponivel)}>
          <div className="kp-emergency-left">
            <Power size={15} />
            <div>
              <div className="kp-emergency-title">{disponivel ? "Disponível para chamados" : "Indisponível no momento"}</div>
              <div className="kp-emergency-sub">Visível para clientes em Sorocaba e região</div>
            </div>
          </div>
          <div className={`kp-toggle ${disponivel ? "kp-toggle-on" : ""}`}><div className="kp-toggle-knob" /></div>
        </button>

        <div className="kp-eyebrow" style={{ marginTop: 18 }}>PEDIDOS NA SUA REGIÃO</div>

        {carregando && <div className="kp-loading">Carregando pedidos…</div>}
        {erro && <div className="kp-error">{erro}</div>}
        {!carregando && !erro && pedidos.length === 0 && (
          <div className="kp-loading">Nenhum pedido pendente no momento.</div>
        )}
        {!carregando && !erro && pedidos.map((p) => (
          <div className="kp-panel kp-req-card" key={p.id}>
            <div className="kp-avatar kp-avatar-alt">{(p.cliente?.usuario?.nome || "Cliente").slice(0, 2).toUpperCase()}</div>
            <div className="kp-pro-info">
              <div className="kp-pro-name">
                {p.cliente?.usuario?.nome || "Cliente"} {p.emergencia && <span className="kp-badge-emerg">URGENTE</span>}
              </div>
              <div className="kp-pro-sub">{p.descricao}</div>
              <div className="kp-pro-meta">
                <span><MapPin size={12} /> {p.distanciaKm} km</span>
                <span className="kp-mono">R$ {Number(p.valorEstimado).toFixed(2)}</span>
              </div>
            </div>
            <button className="kp-btn-small" onClick={() => onAceitar(p)}>Aceitar</button>
          </div>
        ))}
      </div>
    </div>
  );
}
