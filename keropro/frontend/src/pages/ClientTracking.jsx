import React, { useState } from "react";
import { MapPin, Wrench } from "lucide-react";
import TopBar from "../components/TopBar";
import MapMock from "../components/MapMock";
import Stepper from "../components/Stepper";
import { api } from "../api/api";

const STATUS_LABELS = [
  { key: "PENDENTE", label: "Aceito", texto: "O profissional aceitou seu pedido." },
  { key: "ACEITO", label: "Aceito", texto: "O profissional aceitou seu pedido." },
  { key: "A_CAMINHO", label: "A caminho", texto: "Técnico a caminho — chegada estimada em 12 min." },
  { key: "EM_EXECUCAO", label: "Em execução", texto: "Serviço em execução no local." },
  { key: "CONCLUIDO", label: "Concluído", texto: "Serviço concluído com sucesso!" },
];
const STEPS = [{ label: "Aceito" }, { label: "A caminho" }, { label: "Em execução" }, { label: "Concluído" }];
const ORDEM = ["PENDENTE", "ACEITO", "A_CAMINHO", "EM_EXECUCAO", "CONCLUIDO"];

export default function ClientTracking({ pro, pedido, onFinish }) {
  const [pedidoAtual, setPedidoAtual] = useState(pedido);

  const statusIndex = Math.max(0, ORDEM.indexOf(pedidoAtual.status) - 1); // 0..3, ignora PENDENTE inicial
  const infoAtual = STATUS_LABELS.find((s) => s.key === pedidoAtual.status) || STATUS_LABELS[0];
  const done = pedidoAtual.status === "CONCLUIDO";

  const avancar = async () => {
    const atualizado = await api.avancarStatusPedido(pedidoAtual.id);
    setPedidoAtual(atualizado);
  };

  return (
    <div className="kp-screen">
      <TopBar title="Acompanhamento" subtitle="Sincronização em tempo real (polling da API)" />
      <div className="kp-scroll">
        <MapMock>
          <div className="kp-map-pin kp-map-pin-home" style={{ left: "28%", top: "62%" }}><MapPin size={16} /></div>
          <div className="kp-map-pin" style={{ left: `${58 - statusIndex * 8}%`, top: `${30 + statusIndex * 6}%`, background: "#f5a623" }}>
            <Wrench size={14} />
          </div>
        </MapMock>

        <div className="kp-panel">
          <div className="kp-status-headline">{infoAtual.texto}</div>
          <Stepper steps={STEPS} active={statusIndex} />
        </div>

        <div className="kp-panel kp-pro-mini">
          <div className="kp-avatar" style={{ background: "#f5a623" }}>{pro.iniciais}</div>
          <div className="kp-pro-info">
            <div className="kp-pro-name">{pro.nome}</div>
            <div className="kp-pro-sub kp-mono">Score {pro.score} · R$ {Number(pedidoAtual.valorEstimado).toFixed(2)}</div>
          </div>
        </div>
      </div>
      <div className="kp-cta-bar">
        {!done ? (
          <button className="kp-btn-primary" onClick={avancar}>Simular próxima atualização</button>
        ) : (
          <button className="kp-btn-primary" onClick={onFinish}>Avaliar e voltar ao início</button>
        )}
      </div>
    </div>
  );
}
