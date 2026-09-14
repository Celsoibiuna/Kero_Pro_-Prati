/* =====================================================================
   KeroPro — cadastro-cliente.js
   Máscaras, validação em tempo real e envio do formulário de cadastro
   de cliente para a API Java (com honeypot anti-bot e verificação de
   força de senha).
===================================================================== */

document.addEventListener("DOMContentLoaded", () => {
  mountIcons();

  const form = document.getElementById("cliente-form");
  const msg = document.getElementById("form-msg");
  const submitBtn = document.getElementById("submit-btn");

  const cpfInput = document.getElementById("cpf");
  const telefoneInput = document.getElementById("telefone");
  const cepInput = document.getElementById("cep");
  const senhaInput = document.getElementById("senha");
  const confirmarSenhaInput = document.getElementById("confirmar-senha");

  /* ---------- máscaras ---------- */
  cpfInput.addEventListener("input", () => (cpfInput.value = maskCPF(cpfInput.value)));
  telefoneInput.addEventListener("input", () => (telefoneInput.value = maskPhone(telefoneInput.value)));
  cepInput.addEventListener("input", () => (cepInput.value = maskCEP(cepInput.value)));

  /* ---------- força de senha em tempo real ---------- */
  senhaInput.addEventListener("input", () => {
    const { score, label, cssClass } = evaluatePasswordStrength(senhaInput.value);
    const bars = document.querySelectorAll("#pw-strength .pw-strength-bar");
    bars.forEach((bar, i) => {
      bar.className = "pw-strength-bar" + (i <= score - 1 && senhaInput.value ? ` ${cssClass}` : "");
    });
    document.getElementById("pw-strength-label").textContent = senhaInput.value
      ? `Força da senha: ${label}`
      : "Mínimo de 8 caracteres, misture letras, números e símbolos.";
  });

  /* ---------- validação de cada campo ao perder o foco ---------- */
  const validators = {
    nome: (v) => v.trim().split(" ").filter(Boolean).length >= 2,
    cpf: (v) => isValidCPF(v),
    nascimento: (v) => isAtLeastAge(v, 18),
    email: (v) => isValidEmail(v),
    telefone: (v) => onlyDigits(v).length >= 10,
    cep: (v) => onlyDigits(v).length === 8,
    logradouro: (v) => v.trim().length >= 3,
    numero: (v) => v.trim().length >= 1,
    bairro: (v) => v.trim().length >= 2,
    senha: (v) => v.length >= MIN_PASSWORD_LENGTH,
    confirmarSenha: (v) => v === senhaInput.value && v.length > 0,
  };

  Object.keys(validators).forEach((name) => {
    const input = form.querySelector(`[name="${name}"]`);
    if (!input) return;
    input.addEventListener("blur", () => validateField(name));
  });

  function validateField(name) {
    const input = form.querySelector(`[name="${name}"]`);
    const wrapper = input.closest(".field");
    const valid = validators[name](input.value);
    if (valid) clearFieldError(wrapper); else setFieldError(wrapper, wrapper.querySelector(".field-error").textContent);
    return valid;
  }

  function validateAll() {
    let allValid = true;
    Object.keys(validators).forEach((name) => {
      if (!validateField(name)) allValid = false;
    });
    if (!form.querySelector("#termos").checked || !form.querySelector("#lgpd").checked) {
      showMessage("Para continuar, você precisa aceitar os Termos de Uso e o tratamento de dados (LGPD).", "error");
      allValid = false;
    }
    return allValid;
  }

  function showMessage(text, type) {
    msg.textContent = text;
    msg.className = `form-msg is-visible form-msg-${type}`;
  }
  function hideMessage() {
    msg.className = "form-msg";
  }

  form.addEventListener("submit", async (ev) => {
    ev.preventDefault();
    hideMessage();

    if (isHoneypotTriggered(form)) return; // bot detectado — falha silenciosamente

    if (!validateAll()) {
      showMessage("Revise os campos destacados antes de continuar.", "error");
      return;
    }

    const payload = {
      nome: form.nome.value.trim(),
      email: form.email.value.trim(),
      senha: form.senha.value,
      cpf: onlyDigits(form.cpf.value),
      telefone: onlyDigits(form.telefone.value),
      dataNascimento: form.nascimento.value,
      endereco: {
        cep: onlyDigits(form.cep.value),
        estado: form.estado.value,
        cidade: form.cidade.value.trim(),
        logradouro: form.logradouro.value.trim(),
        numero: form.numero.value.trim(),
        complemento: form.complemento.value.trim(),
        bairro: form.bairro.value.trim(),
      },
      aceitouTermos: form.termos.checked,
      aceitouLgpd: form.lgpd.checked,
      aceitouMarketing: form.marketing.checked,
    };

    submitBtn.disabled = true;
    submitBtn.textContent = "Criando conta…";

    const result = await postCadastro("/auth/cadastro/cliente", payload);

    submitBtn.disabled = false;
    submitBtn.textContent = "Criar minha conta";

    if (result.ok) {
      showMessage("Conta criada com sucesso! Você já pode entrar no KeroPro.", "success");
      form.reset();
    } else if (result.offline) {
      showMessage(result.error + " (o formulário está validado e pronto para envio assim que a API estiver no ar.)", "offline");
    } else {
      showMessage(result.error, "error");
    }
  });
});
