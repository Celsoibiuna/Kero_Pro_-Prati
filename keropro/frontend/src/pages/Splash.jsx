import React, { useEffect } from "react";

export default function Splash({ onDone }) {
  useEffect(() => {
    const t = setTimeout(onDone, 2000);
    return () => clearTimeout(t);
  }, [onDone]);

  return (
    <div className="kp-screen kp-splash" onClick={onDone}>
      <div className="kp-splash-mark">
        <svg width="96" height="96" viewBox="0 0 96 96">
          <circle cx="48" cy="48" r="42" fill="none" stroke="#2A3A4E" strokeWidth="3" />
          <path d="M 48 6 A 42 42 0 0 1 88 55" fill="none" stroke="#f5a623" strokeWidth="3" strokeLinecap="round" className="kp-spin" />
        </svg>
        <img src="/assets/logo-mark.png" alt="KeroPro" className="kp-splash-logo" />
      </div>
      <div className="kp-splash-title">KeroPro</div>
      <div className="kp-splash-tag">Serviço técnico, validado por competência.</div>
      <div className="kp-splash-hint kp-mono">toque para continuar</div>
    </div>
  );
}
