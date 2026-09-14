import React, { useEffect, useState } from "react";
import { Search, Sparkles, Zap, Droplet, Wifi, Wind, Hammer } from "lucide-react";
import TopBar from "../components/TopBar";
import ProCard from "../components/ProCard";
import { api } from "../api/api";

const CATEGORIAS = [
  { id: "eletrica", label: "Elétrica", icon: Zap },
  { id: "hidraulica", label: "Hidráulica", icon: Droplet },
  { id: "ti", label: "TI & Redes", icon: Wifi },
  { id: "clima", label: "Ar-condicionado", icon: Wind },
  { id: "reforma", label: "Reformas", icon: Hammer },
];

// Coordenadas de referência do cliente (Sorocaba/SP) — em produção viriam do GPS do dispositivo.
const CLIENTE_LAT = -23.4950;
const CLIENTE_LNG = -47.4580;

export default function ClientHome({ onOpenPro, onLogout }) {
  const [categoria, setCategoria] = useState(null);
  const [emergencia, setEmergencia] = useState(false);
  const [profissionais, setProfissionais] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  useEffect(() => {
    setCarregando(true);
    setErro(null);
    api
      .getProfissionais({ categoria, clienteLat: CLIENTE_LAT, clienteLng: CLIENTE_LNG, emergencia })
      .then((lista) =>
        setProfissionais(
          lista.map((p) => ({
            ...p,
            iniciais: p.nome.split(" ").map((n) => n[0]).slice(0, 2).join(""),
            cor: "#f5a623",
          }))
        )
      )
      .catch(() => setErro("Não foi possível carregar os profissionais. Verifique se a API Java está rodando."))
      .finally(() => setCarregando(false));
  }, [categoria, emergencia]);

  return (
    <div className="kp-screen">
      <TopBar title="Olá, Mariana" subtitle="Sorocaba, SP" onLogout={onLogout} />
      <div className="kp-scroll">
        <div className="kp-search">
          <Search size={15} />
          <span>Que serviço você precisa agora?</span>
        </div>

        <div className="kp-chips">
          {CATEGORIAS.map((c) => {
            const Icon = c.icon;
            const on = categoria === c.id;
            return (
              <button key={c.id} className={`kp-chip ${on ? "kp-chip-on" : ""}`} onClick={() => setCategoria(on ? null : c.id)}>
                <Icon size={13} /> {c.label}
              </button>
            );
          })}
        </div>

        <button className={`kp-emergency ${emergencia ? "kp-emergency-on" : ""}`} onClick={() => setEmergencia(!emergencia)}>
          <div className="kp-emergency-left">
            <Sparkles size={15} />
            <div>
              <div className="kp-emergency-title">Emergência em até 1h</div>
              <div className="kp-emergency-sub">Prioriza técnicos com resposta imediata</div>
            </div>
          </div>
          <div className={`kp-toggle ${emergencia ? "kp-toggle-on" : ""}`}>
            <div className="kp-toggle-knob" />
          </div>
        </button>

        <div className="kp-eyebrow" style={{ marginTop: 18 }}>
          PROFISSIONAIS PRÓXIMOS · RANQUEADOS POR SCORE
        </div>

        {carregando && <div className="kp-loading">Buscando profissionais…</div>}
        {erro && <div className="kp-error">{erro}</div>}
        {!carregando && !erro && profissionais.map((p) => (
          <ProCard key={p.id} pro={p} onClick={() => onOpenPro(p)} />
        ))}
      </div>
    </div>
  );
}
