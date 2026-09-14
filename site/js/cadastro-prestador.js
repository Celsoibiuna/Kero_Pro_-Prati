/* =====================================================================
   KeroPro — cadastro-prestador.js
   Máscaras, validação em tempo real, upload de comprovantes e envio do
   formulário de cadastro de profissional para a API Java.
===================================================================== */

document.addEventListener("DOMContentLoaded", () => {
  mountIcons();

  const form = document.getElementById("prestador-form");
  const msg = document.getElementById("form-msg");
  const submitBtn = document.getElementById("submit-btn");

  const documentoInput = document.getElementById("documento");
  const documentoLabel = document.getElementById("documento-label");
  const telefoneInput = document.getElementById("telefone");
  const cepInput = document.getElementById("cep");
  const senhaInput = document.getElementById("senha");

  /* ---------- toggle CPF / CNPJ ---------- */
  let tipoDocumento = "cpf";
  const docToggle = document.getElementById("doc-toggle");
  docToggle.querySelectorAll("button").forEach((btn) => {
    btn.addEventListener("click", () => {
      docToggle.querySelectorAll("button").forEach((b) => b.classList.remove("is-active"));
      btn.classList.add("is-active");
      tipoDocumento = btn.dataset.doc;
      documentoLabel.textContent = tipoDocumento === "cpf" ? "CPF" : "CNPJ";
      documentoInput.placeholder = tipoDocumento === "cpf" ? "000.000.000-00" : "00.000.000/0000-00";
      documentoInput.value = "";
      clearFieldError(documentoInput.closest(".field"));
    });
  });

  /* ---------- máscaras ---------- */
  documentoInput.addEventListener("input", () => {
    documentoInput.value = tipoDocumento === "cpf" ? maskCPF(documentoInput.value) : maskCNPJ(documentoInput.value);
  });
  telefoneInput.addEventListener("input", () => (telefoneInput.value = maskPhone(telefoneInput.value)));
  cepInput.addEventListener("input", () => (cepInput.value = maskCEP(cepInput.value)));

  /* ---------- upload de comprovantes (drag & drop + seleção manual) ---------- */
  const fileDrop = document.getElementById("file-drop");
  const fileInput = document.getElementById("arquivos");
  const fileList = document.getElementById("file-drop-list");
  let selectedFiles = [];

  function renderFileList() {
    fileList.innerHTML = selectedFiles.map((f, i) => `
      <div class="file-drop-item">
        <span><i data-icon="file" style="display:inline-block;vertical-align:-2px;margin-right:6px"></i>${f.name}</span>
        <button type="button" data-remove="${i}" aria-label="Remover">${ICONS.x}</button>
      </div>`).join("");
    mountIcons(fileList);
    fileList.querySelectorAll("[data-remove]").forEach((btn) => {
      btn.addEventListener("click", () => {
        selectedFiles.splice(Number(btn.dataset.remove), 1);
        renderFileList();
      });
    });
  }
  fileInput.addEventListener("change", () => {
    selectedFiles = selectedFiles.concat(Array.from(fileInput.files));
    renderFileList();
  });
  ["dragover", "dragleave", "drop"].forEach((evt) => {
    fileDrop.addEventListener(evt, (e) => e.preventDefault());
  });
  fileDrop.addEventListener("drop", (e) => {
    selectedFiles = selectedFiles.concat(Array.from(e.dataTransfer.files));
    renderFileList();
  });

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

  /* ---------- validação ---------- */
  const currentYear = new Date().getFullYear();
  const validators = {
    nome: (v) => v.trim().split(" ").filter(Boolean).length >= 2,
    documento: (v) => (tipoDocumento === "cpf" ? isValidCPF(v) : isValidCNPJ(v)),
    nascimento: (v) => isAtLeastAge(v, 18),
    email: (v) => isValidEmail(v),
    telefone: (v) => onlyDigits(v).length >= 10,
    categoria: (v) => v !== "",
    especialidade: (v) => v.trim().length >= 3,
    experiencia: (v) => v !== "" && Number(v) >= 0,
    instituicao: (v) => v.trim().length >= 2,
    curso: (v) => v.trim().length >= 2,
    anoConclusao: (v) => Number(v) >= 1970 && Number(v) <= currentYear,
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
    if (!form.querySelector("#termos").checked || !form.querySelector("#lgpd").checked || !form.querySelector("#verificacao").checked) {
      showMessage("Para continuar, você precisa aceitar os três consentimentos obrigatórios (Termos, LGPD e verificação de dados).", "error");
      allValid = false;
    }
    return allValid;
  }

  function showMessage(text, type) {
    msg.textContent = text;
    msg.className = `form-msg is-visible form-msg-${type}`;
  }
  function hideMessage() { msg.className = "form-msg"; }

  form.addEventListener("submit", async (ev) => {
    ev.preventDefault();
    hideMessage();

    if (isHoneypotTriggered(form)) return;

    if (!validateAll()) {
      showMessage("Revise os campos destacados antes de continuar.", "error");
      return;
    }

    const payload = {
      nome: form.nome.value.trim(),
      email: form.email.value.trim(),
      senha: form.senha.value,
      tipoDocumento: tipoDocumento.toUpperCase(),
      documento: onlyDigits(form.documento.value),
      telefone: onlyDigits(form.telefone.value),
      dataNascimento: form.nascimento.value,
      categoria: form.categoria.value,
      especialidade: form.especialidade.value.trim(),
      anosExperiencia: Number(form.experiencia.value),
      raioAtendimentoKm: Number(form.raio.value),
      bio: form.bio.value.trim(),
      formacao: {
        instituicao: form.instituicao.value.trim(),
        curso: form.curso.value.trim(),
        anoConclusao: Number(form.anoConclusao.value),
        certificacoesAdicionais: form.certificacoes.value.trim(),
      },
      endereco: {
        cep: onlyDigits(form.cep.value),
        estado: form.estado.value,
        cidade: form.cidade.value.trim(),
        logradouro: form.logradouro.value.trim(),
        numero: form.numero.value.trim(),
        complemento: form.complemento.value.trim(),
        bairro: form.bairro.value.trim(),
      },
      quantidadeComprovantes: selectedFiles.length,
      aceitouTermos: form.termos.checked,
      aceitouLgpd: form.lgpd.checked,
      aceitouVerificacao: form.verificacao.checked,
      aceitouMarketing: form.marketing.checked,
    };

    submitBtn.disabled = true;
    submitBtn.textContent = "Enviando cadastro…";

    const result = await postCadastro("/auth/cadastro/profissional", payload);

    submitBtn.disabled = false;
    submitBtn.textContent = "Enviar cadastro para análise";

    if (result.ok) {
      showMessage("Cadastro enviado! Nossa equipe vai validar sua formação e você recebe a confirmação por e-mail em até 48h.", "success");
      form.reset();
      selectedFiles = [];
      renderFileList();
    } else if (result.offline) {
      showMessage(result.error + " (o formulário está validado e pronto para envio assim que a API estiver no ar.)", "offline");
    } else {
      showMessage(result.error, "error");
    }
  });
});
