/* =====================================================================
   KeroPro — validation.js
   Utilitários de validação e segurança compartilhados pelos formulários
   de cadastro (cliente e profissional). Sem dependências externas.
===================================================================== */

const onlyDigits = (str) => (str || "").replace(/\D/g, "");

/* ---------------------------------------------------------------
   Máscaras de entrada (formatam enquanto o usuário digita)
---------------------------------------------------------------- */
function maskCPF(value) {
  return onlyDigits(value).slice(0, 11)
    .replace(/(\d{3})(\d)/, "$1.$2")
    .replace(/(\d{3})(\d)/, "$1.$2")
    .replace(/(\d{3})(\d{1,2})$/, "$1-$2");
}
function maskCNPJ(value) {
  return onlyDigits(value).slice(0, 14)
    .replace(/(\d{2})(\d)/, "$1.$2")
    .replace(/(\d{3})(\d)/, "$1.$2")
    .replace(/(\d{3})(\d)/, "$1/$2")
    .replace(/(\d{4})(\d{1,2})$/, "$1-$2");
}
function maskPhone(value) {
  const d = onlyDigits(value).slice(0, 11);
  if (d.length <= 10) {
    return d.replace(/(\d{2})(\d)/, "($1) $2").replace(/(\d{4})(\d{1,4})$/, "$1-$2");
  }
  return d.replace(/(\d{2})(\d)/, "($1) $2").replace(/(\d{5})(\d{1,4})$/, "$1-$2");
}
function maskCEP(value) {
  return onlyDigits(value).slice(0, 8).replace(/(\d{5})(\d{1,3})$/, "$1-$2");
}

/* ---------------------------------------------------------------
   Validadores de documento (algoritmo módulo 11 oficial)
---------------------------------------------------------------- */
function isValidCPF(raw) {
  const cpf = onlyDigits(raw);
  if (cpf.length !== 11 || /^(\d)\1{10}$/.test(cpf)) return false;
  let soma = 0;
  for (let i = 0; i < 9; i++) soma += parseInt(cpf[i], 10) * (10 - i);
  let resto = (soma * 10) % 11;
  if (resto === 10) resto = 0;
  if (resto !== parseInt(cpf[9], 10)) return false;

  soma = 0;
  for (let i = 0; i < 10; i++) soma += parseInt(cpf[i], 10) * (11 - i);
  resto = (soma * 10) % 11;
  if (resto === 10) resto = 0;
  return resto === parseInt(cpf[10], 10);
}

function isValidCNPJ(raw) {
  const cnpj = onlyDigits(raw);
  if (cnpj.length !== 14 || /^(\d)\1{13}$/.test(cnpj)) return false;
  const calc = (base) => {
    const pesos = base.length === 12 ? [5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2] : [6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2];
    const soma = base.split("").reduce((acc, d, i) => acc + parseInt(d, 10) * pesos[i], 0);
    const resto = soma % 11;
    return resto < 2 ? 0 : 11 - resto;
  };
  const dv1 = calc(cnpj.slice(0, 12));
  const dv2 = calc(cnpj.slice(0, 12) + dv1);
  return cnpj.endsWith(`${dv1}${dv2}`);
}

function isValidEmail(email) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test((email || "").trim());
}

/** Idade mínima em anos a partir de uma data (yyyy-mm-dd). */
function isAtLeastAge(dateStr, minAge = 18) {
  if (!dateStr) return false;
  const birth = new Date(dateStr);
  if (Number.isNaN(birth.getTime())) return false;
  const today = new Date();
  let age = today.getFullYear() - birth.getFullYear();
  const m = today.getMonth() - birth.getMonth();
  if (m < 0 || (m === 0 && today.getDate() < birth.getDate())) age--;
  return age >= minAge;
}

/* ---------------------------------------------------------------
   Força de senha — heurística simples e transparente:
   comprimento + variedade de caracteres (minúscula/maiúscula/número/símbolo)
---------------------------------------------------------------- */
function evaluatePasswordStrength(pw) {
  if (!pw) return { score: 0, label: "" };
  let score = 0;
  if (pw.length >= 8) score++;
  if (pw.length >= 12) score++;
  if (/[a-z]/.test(pw) && /[A-Z]/.test(pw)) score++;
  if (/\d/.test(pw)) score++;
  if (/[^A-Za-z0-9]/.test(pw)) score++;

  const bucket = Math.min(4, Math.round((score / 5) * 4));
  const labels = ["Muito fraca", "Fraca", "Razoável", "Boa", "Forte"];
  const classes = ["filled-weak", "filled-weak", "filled-medium", "filled-medium", "filled-strong"];
  return { score: bucket, label: labels[bucket], cssClass: classes[bucket] };
}

const MIN_PASSWORD_LENGTH = 8;

/* ---------------------------------------------------------------
   Helpers de UI para exibir/ocultar erros em um campo (.field)
---------------------------------------------------------------- */
function setFieldError(fieldWrapper, message) {
  if (!fieldWrapper) return;
  fieldWrapper.classList.add("has-error");
  const input = fieldWrapper.querySelector("input, select, textarea");
  if (input) input.classList.add("is-invalid");
  const errorEl = fieldWrapper.querySelector(".field-error");
  if (errorEl) errorEl.textContent = message;
}
function clearFieldError(fieldWrapper) {
  if (!fieldWrapper) return;
  fieldWrapper.classList.remove("has-error");
  const input = fieldWrapper.querySelector("input, select, textarea");
  if (input) { input.classList.remove("is-invalid"); if (input.value) input.classList.add("is-valid"); }
}

function debounce(fn, wait = 250) {
  let t;
  return (...args) => { clearTimeout(t); t = setTimeout(() => fn(...args), wait); };
}

/* ---------------------------------------------------------------
   Proteção anti-bot simples (honeypot): campo invisível para humanos.
   Se vier preenchido, é quase certamente um robô preenchendo tudo.
---------------------------------------------------------------- */
function isHoneypotTriggered(form) {
  const hp = form.querySelector('input[name="website"]');
  return !!(hp && hp.value.trim() !== "");
}
