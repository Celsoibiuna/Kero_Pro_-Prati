import React from "react";
import { ShieldCheck, Star, MapPin } from "lucide-react";

/** Card de profissional na lista de busca do cliente, ordenada por Score de Excelência. */
export default function ProCard({ pro, onClick }) {
  return (
    <button className="kp-pro-card" onClick={onClick}>
      <div className="kp-avatar" style={{ background: pro.cor || "#f5a623" }}>
        {pro.iniciais || pro.nome?.slice(0, 2).toUpperCase()}
      </div>
      <div className="kp-pro-info">
        <div className="kp-pro-name">
          {pro.nome} {pro.verificado && <ShieldCheck size={13} className="kp-verified" />}
        </div>
        <div className="kp-pro-sub">{pro.especialidade}</div>
        <div className="kp-pro-meta">
          <span>
            <Star size={12} fill="#f5a623" color="#f5a623" /> {pro.rating ?? "4.8"}
          </span>
          <span>
            <MapPin size={12} /> {pro.distanciaKm} km
          </span>
        </div>
      </div>
      <div className="kp-pro-score">
        <div className="kp-pro-score-num kp-mono">{pro.score}</div>
        <div className="kp-pro-score-label">score</div>
      </div>
    </button>
  );
}
