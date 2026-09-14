import React from "react";
import { ChevronLeft, LogOut } from "lucide-react";

export default function TopBar({ title, subtitle, onBack, onLogout }) {
  return (
    <div className="kp-topbar">
      {onBack ? (
        <button className="kp-icon-btn" onClick={onBack} aria-label="Voltar">
          <ChevronLeft size={18} />
        </button>
      ) : (
        <div className="kp-brand">
          <img src="/assets/logo-mark.png" alt="" /> KeroPro
        </div>
      )}
      <div className="kp-topbar-center">
        <div className="kp-topbar-title">{title}</div>
        {subtitle && <div className="kp-topbar-subtitle">{subtitle}</div>}
      </div>
      {onLogout ? (
        <button className="kp-icon-btn" onClick={onLogout} aria-label="Sair">
          <LogOut size={17} />
        </button>
      ) : (
        <div style={{ width: 34 }} />
      )}
    </div>
  );
}
