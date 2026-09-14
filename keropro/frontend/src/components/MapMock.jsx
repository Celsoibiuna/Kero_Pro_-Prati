import React from "react";
import { Radar } from "lucide-react";

/** Representação visual simplificada do mapa (a integração real usa OpenStreetMap, conforme a proposta). */
export default function MapMock({ children }) {
  return (
    <div className="kp-map">
      <div className="kp-map-grid" />
      {children}
      <div className="kp-map-coords kp-mono">-23.5015, -47.4526</div>
      <div className="kp-map-badge">
        <Radar size={11} /> OpenStreetMap · GPS ao vivo
      </div>
    </div>
  );
}
