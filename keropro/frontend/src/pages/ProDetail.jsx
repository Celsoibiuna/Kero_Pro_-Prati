import React from "react";
import { ChevronRight, Wrench, Navigation } from "lucide-react";
import TopBar from "../components/TopBar";
import Gauge from "../components/Gauge";

export default function ProDetail({ pro, onBack, onContratar }) {
  return (
    <div className="kp-screen">
      <TopBar title={pro.nome} subtitle={pro.especialidade} onBack={onBack} />
      <div className="kp-scroll">
        <div className="kp-gauge-card">
          <Gauge score={pro.score} accent={pro.cor} />
          <div className="kp-gauge-caption">
            Validado por Content-Based Filtering · formação, certificações e histórico
          </div>
        </div>

        <div className="kp-eyebrow">COMPOSIÇÃO DO SCORE</div>
        <div className="kp-panel">
          <Barra label="Formação acadêmica" value={pro.formacao} accent="#f5a623" />
          <Barra label="Certificações técnicas" value={pro.certificacoes} accent="#00a3e0" />
          <Barra label="Avaliações de clientes" value={pro.avaliacoesScore} accent="#E2574C" />
          <Barra label="Tempo de resposta" value={pro.tempoResposta} accent="#8B8FF7" />
        </div>

        <div className="kp-eyebrow">PORTFÓLIO</div>
        <div className="kp-portfolio">
          {[0, 1, 2].map((i) => (
            <div key={i} className="kp-portfolio-tile" style={{ background: "#f5a62322", borderColor: "#f5a62355" }}>
              <Wrench size={18} color="#f5a623" />
            </div>
          ))}
        </div>

        <div className="kp-eyebrow">ORÇAMENTO ESTIMADO</div>
        <div className="kp-panel">
          <div className="kp-price-row"><span>Valor base do serviço</span><span className="kp-mono">R$ {Number(pro.precoBase).toFixed(2)}</span></div>
          <div className="kp-price-row"><span>Deslocamento · {pro.distanciaKm} km</span><span className="kp-mono">calculado via GPS</span></div>
          <div className="kp-price-row kp-price-total"><span>Total estimado</span><span className="kp-mono">R$ {Number(pro.precoEstimado).toFixed(2)}</span></div>
          <div className="kp-price-note"><Navigation size={11} /> calculado no back-end (OrcamentoService.java)</div>
        </div>
      </div>
      <div className="kp-cta-bar">
        <button className="kp-btn-primary" onClick={onContratar}>
          Contratar agora <ChevronRight size={16} />
        </button>
      </div>
    </div>
  );
}

function Barra({ label, value, accent }) {
  return (
    <div className="kp-bar-row">
      <div className="kp-bar-label">
        <span>{label}</span>
        <span className="kp-mono">{value}</span>
      </div>
      <div className="kp-bar-track">
        <div className="kp-bar-fill" style={{ width: `${value}%`, background: accent }} />
      </div>
    </div>
  );
}
