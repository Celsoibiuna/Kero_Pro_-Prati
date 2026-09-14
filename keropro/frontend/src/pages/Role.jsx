import React from "react";
import { CircleUser, Briefcase, ChevronRight, ShieldCheck } from "lucide-react";
import TopBar from "../components/TopBar";

export default function Role({ onEnter }) {
  return (
    <div className="kp-screen kp-role">
      <TopBar title="Bem-vindo" />
      <div className="kp-role-body">
        <div className="kp-eyebrow">ENTRAR COMO</div>

        <button className="kp-role-card" onClick={() => onEnter("cliente")}>
          <CircleUser size={22} />
          <div>
            <div className="kp-role-card-title">Cliente</div>
            <div className="kp-role-card-sub">Continuar como Mariana Oliveira</div>
          </div>
          <ChevronRight size={18} className="kp-role-chev" />
        </button>

        <button className="kp-role-card kp-role-card-alt" onClick={() => onEnter("profissional")}>
          <Briefcase size={22} />
          <div>
            <div className="kp-role-card-title">Profissional</div>
            <div className="kp-role-card-sub">Continuar como Carlos H. Santos</div>
          </div>
          <ChevronRight size={18} className="kp-role-chev" />
        </button>

        <div className="kp-role-footnote">
          <ShieldCheck size={13} /> Login via API Java (/api/auth/login) · dados em MySQL
        </div>
      </div>
    </div>
  );
}
