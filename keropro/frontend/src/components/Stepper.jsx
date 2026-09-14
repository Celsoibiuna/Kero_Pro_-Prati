import React from "react";
import { CheckCircle2 } from "lucide-react";

/** Indicador visual do status do pedido (espelha a sincronização em tempo real do back-end). */
export default function Stepper({ steps, active }) {
  return (
    <div className="kp-stepper">
      {steps.map((s, i) => (
        <div key={s.label} className="kp-step">
          <div className={`kp-step-dot ${i <= active ? "kp-step-dot-on" : ""}`}>
            {i < active ? <CheckCircle2 size={13} /> : <span className="kp-mono">{i + 1}</span>}
          </div>
          <div className={`kp-step-label ${i <= active ? "kp-step-label-on" : ""}`}>{s.label}</div>
          {i < steps.length - 1 && <div className={`kp-step-line ${i < active ? "kp-step-line-on" : ""}`} />}
        </div>
      ))}
    </div>
  );
}
