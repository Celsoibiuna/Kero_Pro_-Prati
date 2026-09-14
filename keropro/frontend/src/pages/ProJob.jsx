import React, { useState } from "react";
import { MapPin, Wrench } from "lucide-react";
import TopBar from "../components/TopBar";
import MapMock from "../components/MapMock";
import Stepper from "../components/Stepper";
import { api } from "../api/api";

const STEPS = [{ label: "A caminho" }, { label: "Cheguei" }, { label: "Em execução" }, { label: "Concluído" }];
const TEXTOS = [
  "Trajeto iniciado até o cliente.",
  "Chegada confirmada no endereço.",
  "Serviço em andamento.",
  "Pagamento liberado para sua conta.",
];
const ORDEM = ["PENDENTE", "ACEITO", "A_CAMINHO", "EM_EXECUCAO", "CONCLUIDO"];

export default function ProJob({ job, onFinish }) {
  const [pedidoAtual, setPedidoAtual] = useState(job);
  const statusIndex = Math.max(0, ORDEM.indexOf(pedidoAtual.status) - 1);
  const done = pedidoAtual.status === "CONCLUIDO";

  const avancar = async () => {
    const atualizado = await api.avancarStatusPedido(pedidoAtual.id);
    setPedidoAtual(atualizado);
  };

  return (
    <div className="kp-screen">
      <TopBar title={pedidoAtual.cliente?.usuario?.nome || "Cliente"} subtitle={pedidoAtual.descricao} />
      <div className="kp-scroll">
        <MapMock>
          <div className="kp-map-pin kp-map-pin-home" style={{ left: "66%", top: "34%" }}><MapPin size={16} /></div>
          <div className="kp-map-pin" style={{ left: `${30 + statusIndex * 9}%`, top: `${64 - statusIndex * 8}%`, background: "#f5a623" }}>
            <Wrench size={14} />
          </div>
        </MapMock>

        <div className="kp-panel">
          <div className="kp-status-headline">{TEXTOS[Math.min(statusIndex, 3)]}</div>
          <Stepper steps={STEPS} active={statusIndex} />
        </div>

        <div className="kp-panel">
          <div className="kp-price-row"><span>Valor do serviço</span><span className="kp-mono">R$ {Number(pedidoAtual.valorEstimado).toFixed(2)}</span></div>
          <div className="kp-price-row"><span>Distância até o cliente</span><span className="kp-mono">{pedidoAtual.distanciaKm} km</span></div>
        </div>
      </div>
      <div className="kp-cta-bar">
        {!done ? (
          <button className="kp-btn-primary" onClick={avancar}>Avançar status</button>
        ) : (
          <button className="kp-btn-primary" onClick={onFinish}>Concluir e voltar ao painel</button>
        )}
      </div>
    </div>
  );
}
