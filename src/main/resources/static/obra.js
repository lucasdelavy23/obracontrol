const GLOBAL_URL = "/api/obras";
let cityRequest = 0;
let saving = false;
let editingId = null;
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

function populateSelect(selector, items, placeholder, label = (item) => item.nome) {
  const select = document.querySelector(selector);
  select.replaceChildren(new Option(placeholder, ""));
  for (const item of items) {
    select.add(new Option(label(item), item.id));
  }
  select.disabled = items.length === 0;
}

function updateSaveButton() {
  document.querySelector("#obra-fields").disabled = saving || loading || !formReady;
  document.querySelector("#save-obra").disabled = saving || loading || !formReady
    || !document.querySelector("#cidade").value
    || !document.querySelector("#construtora").value;
}

async function loadObras() {
  const obras = await requestJson(GLOBAL_URL);
  const tbody = document.querySelector("#table_obra tbody");
  tbody.replaceChildren();
  for (const obra of obras) {
    const row = tbody.insertRow();
    for (const value of [obra.id, obra.nome, obra.construtora, obra.cidade, obra.endereco]) {
      row.insertCell().textContent = value ?? "";
    }
    const statusCell = row.insertCell();
    const statusBadge = document.createElement("span");
    const finalizada = obra.status === "finalizada";
    statusBadge.className = `badge ${finalizada ? "text-bg-success" : "text-bg-primary"}`;
    statusBadge.textContent = finalizada ? "Finalizada" : "Aberta";
    statusCell.append(statusBadge);

    const button = document.createElement("button");
    button.type = "button";
    button.className = "btn btn-danger";
    button.textContent = "Remover";
    button.addEventListener("click", () => removeObra(obra.id));
    const editButton = document.createElement("button");
    editButton.type = "button";
    editButton.className = "btn btn-primary me-2";
    editButton.textContent = "Editar";
    editButton.addEventListener("click", () => openObra(obra.id));
    const statusButton = document.createElement("button");
    statusButton.type = "button";
    statusButton.className = `${finalizada ? "btn btn-outline-primary" : "btn btn-outline-success"} me-2`;
    statusButton.textContent = finalizada ? "Reabrir" : "Finalizar";
    statusButton.addEventListener("click", () => updateObraStatus(obra, statusButton));
    const optionsCell = row.insertCell();
    optionsCell.className = "text-nowrap";
    optionsCell.append(editButton, statusButton, button);
  }
}

async function updateObraStatus(obra, button) {
  const finalizada = obra.status === "finalizada";
  const novoStatus = finalizada ? "aberta" : "finalizada";
  const acao = finalizada ? "reabrir" : "finalizar";
  if (!confirm(`Realmente deseja ${acao} a obra ${obra.nome}?`)) return;
  button.disabled = true;
  showMessage("#obra-message");
  try {
    await requestJson(`${GLOBAL_URL}/${obra.id}/status`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ status: novoStatus }),
    });
    await loadObras();
  } catch (error) {
    button.disabled = false;
    showMessage("#obra-message", error.message);
  }
}

async function loadCities() {
  const request = ++cityRequest;
  const estadoId = document.querySelector("#estado").value;
  populateSelect("#cidade", [], estadoId ? "Carregando cidades..." : "Selecione um estado primeiro");
  showMessage("#obra-form-message");
  updateSaveButton();
  if (!estadoId) return;
  try {
    const cidades = await requestJson(`/api/estados/${estadoId}/cidades`);
    if (request !== cityRequest) return;
    populateSelect("#cidade", cidades, cidades.length ? "Selecione uma cidade" : "Nenhuma cidade cadastrada neste estado");
  } catch (error) {
    if (request !== cityRequest) return;
    populateSelect("#cidade", [], "Não foi possível carregar as cidades");
    showMessage("#obra-form-message", error.message);
  }
  updateSaveButton();
}

async function openObra(id = null) {
  if (saving || loading) return;
  editingId = id;
  loading = true;
  formReady = false;
  ++cityRequest;
  document.querySelector("#obra-form").reset();
  populateSelect("#cidade", [], "Selecione um estado primeiro");
  document.querySelector("#modal_obra .modal-title").textContent = id ? "Editar Obra" : "Cadastrar Nova Obra";
  document.querySelector("#save-obra").textContent = id ? "Salvar alterações" : "Cadastrar Obra";
  showMessage("#obra-form-message");
  updateSaveButton();
  bootstrap.Modal.getOrCreateInstance(document.querySelector("#modal_obra")).show();
  try {
    const [, obra] = await Promise.all([loadCatalogs(), id ? requestJson(`${GLOBAL_URL}/${id}`) : null]);
    if (obra) {
      document.querySelector("#nomeObra").value = obra.nome;
      document.querySelector("#endereco").value = obra.endereco ?? "";
      document.querySelector("#construtora").value = obra.construtoraId;
      document.querySelector("#estado").value = obra.estadoId;
      await loadCities();
      document.querySelector("#cidade").value = obra.cidadeId;
    }
    formReady = true;
  } catch (error) {
    showMessage("#obra-form-message", error.message);
  } finally {
    loading = false;
    updateSaveButton();
  }
}

async function saveObra(event) {
  event.preventDefault();
  if (saving || loading || !formReady) return;
  const form = document.querySelector("#obra-form");
  if (!form.reportValidity()) return;
  saving = true;
  updateSaveButton();
  showMessage("#obra-form-message");
  try {
    await requestJson(editingId ? `${GLOBAL_URL}/${editingId}` : GLOBAL_URL, {
      method: editingId ? "PUT" : "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        nome: document.querySelector("#nomeObra").value,
        cidadeId: Number(document.querySelector("#cidade").value),
        construtoraId: Number(document.querySelector("#construtora").value),
        endereco: document.querySelector("#endereco").value,
      }),
    });
  } catch (error) {
    showMessage("#obra-form-message", error.message);
    return;
  } finally {
    saving = false;
    updateSaveButton();
  }
  form.reset();
  ++cityRequest;
  populateSelect("#cidade", [], "Selecione um estado primeiro");
  updateSaveButton();
  bootstrap.Modal.getOrCreateInstance(document.querySelector("#modal_obra")).hide();
  showMessage("#obra-message");
  try {
    await loadObras();
  } catch (error) {
    showMessage("#obra-message", "Obra salva, mas a listagem não pôde ser atualizada. Recarregue a página.");
  }
}

async function removeObra(id) {
  if (!confirm("Realmente deseja apagar esse registro?")) return;
  showMessage("#obra-message");
  try {
    await requestJson(`${GLOBAL_URL}/${id}`, { method: "DELETE" });
    await loadObras();
  } catch (error) {
    showMessage("#obra-message", error.message);
  }
}

async function loadCatalogs() {
  const [estados, construtoras] = await Promise.all([
    requestJson("/api/estados"),
    requestJson("/api/construtoras"),
  ]);
  populateSelect("#estado", estados, estados.length ? "Selecione um estado" : "Nenhum estado cadastrado",
    (estado) => `${estado.nome} (${estado.sigla})`);
  populateSelect("#construtora", construtoras,
    construtoras.length ? "Selecione uma construtora" : "Nenhuma construtora cadastrada");
  updateSaveButton();
}

async function init() {
  document.querySelector("#obra-form").addEventListener("submit", saveObra);
  document.querySelector("#new-obra").addEventListener("click", () => openObra());
  document.querySelector("#modal_obra").addEventListener("hide.bs.modal", (event) => {
    if (saving || loading) event.preventDefault();
  });
  document.querySelector("#estado").addEventListener("change", () => loadCities());
  document.querySelector("#cidade").addEventListener("change", updateSaveButton);
  document.querySelector("#construtora").addEventListener("change", updateSaveButton);
  await loadObras().catch((error) => showMessage("#obra-message", error.message));
}

init();
