const GLOBAL_URL = "/api/instaladores";
let editingId = null;
let saving = false;
let loading = false;
let formReady = false;

function showMessage(selector, message = "") {
  const element = document.querySelector(selector);
  element.textContent = message;
  element.hidden = !message;
}

async function requestJson(url, options) {
  const response = await fetch(url, options);
  if (!response.ok) {
    const error = await response.json().catch(() => ({}));
    throw new Error(error.mensagem || "Não foi possível concluir a operação. Tente novamente.");
  }
  return response.json();
}

function updateFormState() {
  document.querySelector("#instalador-fields").disabled = saving || loading || !formReady;
  document.querySelector("#save-instalador").disabled = saving || loading || !formReady;
}

async function loadInstallers() {
  const instaladores = await requestJson(GLOBAL_URL);
  const tbody = document.querySelector("#table_instaladores tbody");
  tbody.replaceChildren();
  for (const instalador of instaladores) {
    const row = tbody.insertRow();
    for (const value of [instalador.id, instalador.nome, instalador.telefone]) {
      row.insertCell().textContent = value ?? "";
    }
    const edit = document.createElement("button");
    edit.type = "button";
    edit.className = "btn btn-primary me-2";
    edit.textContent = "Editar";
    edit.addEventListener("click", () => openInstaller(instalador.id));
    const remove = document.createElement("button");
    remove.type = "button";
    remove.className = "btn btn-danger";
    remove.textContent = "Remover";
    remove.addEventListener("click", () => removeInstaller(instalador.id));
    row.insertCell().append(edit, remove);
  }
}

async function openInstaller(id = null) {
  if (saving || loading) return;
  editingId = id;
  loading = true;
  formReady = false;
  document.querySelector("#instalador-form").reset();
  document.querySelector("#modal_instalador .modal-title").textContent = id ? "Editar Instalador" : "Cadastrar Novo Instalador";
  document.querySelector("#save-instalador").textContent = id ? "Salvar alterações" : "Cadastrar Instalador";
  showMessage("#instalador-form-message");
  updateFormState();
  bootstrap.Modal.getOrCreateInstance(document.querySelector("#modal_instalador")).show();
  try {
    if (id) {
      const instalador = await requestJson(`${GLOBAL_URL}/${id}`);
      document.querySelector("#nome").value = instalador.nome;
      document.querySelector("#telefone").value = instalador.telefone ?? "";
    }
    formReady = true;
  } catch (error) {
    showMessage("#instalador-form-message", error.message);
  } finally {
    loading = false;
    updateFormState();
  }
}

async function saveInstaller(event) {
  event.preventDefault();
  if (saving || loading || !formReady) return;
  if (!document.querySelector("#instalador-form").reportValidity()) return;
  saving = true;
  updateFormState();
  showMessage("#instalador-form-message");
  try {
    await requestJson(editingId ? `${GLOBAL_URL}/${editingId}` : GLOBAL_URL, {
      method: editingId ? "PUT" : "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        nome: document.querySelector("#nome").value,
        telefone: document.querySelector("#telefone").value,
      }),
    });
  } catch (error) {
    showMessage("#instalador-form-message", error.message);
    return;
  } finally {
    saving = false;
    updateFormState();
  }
  bootstrap.Modal.getOrCreateInstance(document.querySelector("#modal_instalador")).hide();
  showMessage("#instalador-message");
  try {
    await loadInstallers();
  } catch (error) {
    showMessage("#instalador-message", "Instalador salvo, mas a listagem não pôde ser atualizada. Recarregue a página.");
  }
}

async function removeInstaller(id) {
  if (!confirm("Realmente deseja apagar esse registro?")) return;
  showMessage("#instalador-message");
  try {
    await requestJson(`${GLOBAL_URL}/${id}`, { method: "DELETE" });
    await loadInstallers();
  } catch (error) {
    showMessage("#instalador-message", error.message);
  }
}

document.querySelector("#new-instalador").addEventListener("click", () => openInstaller());
document.querySelector("#instalador-form").addEventListener("submit", saveInstaller);
document.querySelector("#modal_instalador").addEventListener("hide.bs.modal", (event) => {
  if (saving || loading) event.preventDefault();
});
loadInstallers().catch((error) => showMessage("#instalador-message", error.message));
