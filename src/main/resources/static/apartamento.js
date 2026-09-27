const GLOBAL_URL = "/api/apartamentos";
const OBRAS_URL = "/api/obras";
let editingId = null;
let saving = false;
let loading = false;
let formReady = false;
let catalogoObras = [];

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
  document.querySelector("#apartamento-fields").disabled = saving || loading || !formReady;
  document.querySelector("#save-apartamento").disabled = saving || loading || !formReady;
}

function populateObras(select, placeholder) {
  select.replaceChildren(new Option(placeholder, ""));
  for (const obra of catalogoObras) {
    select.add(new Option(obra.nome, obra.id));
  }
  select.disabled = catalogoObras.length === 0;
}

async function loadObras() {
  catalogoObras = await requestJson(OBRAS_URL);
  const placeholder = catalogoObras.length ? "Selecione uma obra" : "Nenhuma obra cadastrada";
  populateObras(document.querySelector("#obra-filtro"), placeholder);
  populateObras(document.querySelector("#obra"), placeholder);
}

async function loadApartamentos() {
  const obraId = document.querySelector("#obra-filtro").value;
  const tbody = document.querySelector("#table_apartamentos tbody");
  tbody.replaceChildren();
  if (!obraId) {
    return;
  }
  const apartamentos = await requestJson(`${GLOBAL_URL}?obraId=${obraId}`);
  for (const apartamento of apartamentos) {
    const row = tbody.insertRow();
    for (const value of [apartamento.id, apartamento.numero, apartamento.obra, apartamento.quantidadePortas]) {
      row.insertCell().textContent = value ?? "";
    }
    const edit = document.createElement("button");
    edit.type = "button";
    edit.className = "btn btn-primary me-2";
    edit.textContent = "Editar";
    edit.addEventListener("click", () => openApartamento(apartamento.id));
    const remove = document.createElement("button");
    remove.type = "button";
    remove.className = "btn btn-danger";
    remove.textContent = "Remover";
    remove.addEventListener("click", () => removeApartamento(apartamento.id));
    row.insertCell().append(edit, remove);
  }
}

async function openApartamento(id = null) {
  if (saving || loading) return;
  editingId = id;
  loading = true;
  formReady = false;
  document.querySelector("#apartamento-form").reset();
  document.querySelector("#modal_apartamento .modal-title").textContent =
    id ? "Editar Apartamento" : "Cadastrar Novo Apartamento";
  document.querySelector("#save-apartamento").textContent =
    id ? "Salvar alterações" : "Cadastrar Apartamento";
  showMessage("#apartamento-form-message");
  updateFormState();
  bootstrap.Modal.getOrCreateInstance(document.querySelector("#modal_apartamento")).show();
  try {
    if (id) {
      const apartamento = await requestJson(`${GLOBAL_URL}/${id}`);
      document.querySelector("#obra").value = apartamento.obraId;
      document.querySelector("#numero").value = apartamento.numero;
    } else {
      document.querySelector("#obra").value = document.querySelector("#obra-filtro").value;
    }
    formReady = true;
  } catch (error) {
    showMessage("#apartamento-form-message", error.message);
  } finally {
    loading = false;
    updateFormState();
  }
}

async function saveApartamento(event) {
  event.preventDefault();
  if (saving || loading || !formReady) return;
  if (!document.querySelector("#apartamento-form").reportValidity()) return;
  saving = true;
  updateFormState();
  showMessage("#apartamento-form-message");
  try {
    await requestJson(editingId ? `${GLOBAL_URL}/${editingId}` : GLOBAL_URL, {
      method: editingId ? "PUT" : "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        numero: document.querySelector("#numero").value,
        obraId: Number(document.querySelector("#obra").value),
      }),
    });
  } catch (error) {
    showMessage("#apartamento-form-message", error.message);
    return;
  } finally {
    saving = false;
    updateFormState();
  }
  bootstrap.Modal.getOrCreateInstance(document.querySelector("#modal_apartamento")).hide();
  showMessage("#apartamento-message");
  try {
    await loadApartamentos();
  } catch (error) {
    showMessage("#apartamento-message", "Apartamento salvo, mas a listagem não pôde ser atualizada. Recarregue a página.");
  }
}

async function removeApartamento(id) {
  if (!confirm("Realmente deseja apagar esse registro? As portas do apartamento também serão apagadas.")) return;
  showMessage("#apartamento-message");
  try {
    await requestJson(`${GLOBAL_URL}/${id}`, { method: "DELETE" });
    await loadApartamentos();
  } catch (error) {
    showMessage("#apartamento-message", error.message);
  }
}

async function init() {
  document.querySelector("#new-apartamento").addEventListener("click", () => openApartamento());
  document.querySelector("#apartamento-form").addEventListener("submit", saveApartamento);
  document.querySelector("#obra-filtro").addEventListener("change", () => {
    showMessage("#apartamento-message");
    loadApartamentos().catch((error) => showMessage("#apartamento-message", error.message));
  });
  document.querySelector("#modal_apartamento").addEventListener("hide.bs.modal", (event) => {
    if (saving || loading) event.preventDefault();
  });
  try {
    await loadObras();
  } catch (error) {
    showMessage("#apartamento-message", error.message);
    return;
  }
  if (catalogoObras.length) {
    document.querySelector("#obra-filtro").value = catalogoObras[0].id;
    await loadApartamentos().catch((error) => showMessage("#apartamento-message", error.message));
  }
}

init();
