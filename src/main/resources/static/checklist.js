const PORTAS_URL = "/api/portas";
const OBRAS_URL = "/api/obras";
const APARTAMENTOS_URL = "/api/apartamentos";
const INSTALADORES_URL = "/api/instaladores";
const ROTULOS_ETAPA = {
  montagem: "Montagem",
  fixacao: "Fixação",
  fechadura: "Fechadura",
  vistas: "Vistas",
  acabamento: "Acabamento",
};
let editingId = null;
let saving = false;
let loading = false;
let formReady = false;
let catalogoObras = [];
let catalogoApartamentos = [];
let catalogoInstaladores = [];

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
  document.querySelector("#porta-fields").disabled = saving || loading || !formReady;
  document.querySelector("#save-porta").disabled = saving || loading || !formReady;
}

function populate(select, placeholder, itens) {
  select.replaceChildren(new Option(placeholder, ""));
  for (const item of itens) {
    select.add(new Option(item.rotulo, item.id));
  }
  select.disabled = itens.length === 0;
}

function catalogoObrasComoOpcoes() {
  return catalogoObras.map((obra) => ({ id: obra.id, rotulo: obra.nome }));
}

function catalogoApartamentosComoOpcoes() {
  return catalogoApartamentos.map((apartamento) => ({ id: apartamento.id, rotulo: apartamento.numero }));
}

function catalogoInstaladoresComoOpcoes() {
  return catalogoInstaladores.map((instalador) => ({ id: instalador.id, rotulo: instalador.nome }));
}

function rotuloDaEtapa(etapa) {
  return ROTULOS_ETAPA[etapa] ?? etapa;
}

function atualizarTitulo() {
  const obra = catalogoObras.find((item) => String(item.id) === document.querySelector("#obra-filtro").value);
  const apartamento = catalogoApartamentos.find(
    (item) => String(item.id) === document.querySelector("#apartamento-filtro").value,
  );
  const partes = [];
  if (obra) partes.push(`Obra: ${obra.nome}`);
  if (apartamento) partes.push(`Apartamento: ${apartamento.numero}`);
  document.querySelector("#checklist-titulo").textContent = partes.join(" | ");
}

function atualizarProgresso(row) {
  const checkboxes = row.querySelectorAll('input[type="checkbox"]');
  const concluidas = [...checkboxes].filter((checkbox) => checkbox.checked).length;
  const badge = row.querySelector("#progresso-badge");
  badge.textContent = `${concluidas}/${checkboxes.length}`;
  badge.className = `badge ${concluidas === checkboxes.length ? "text-bg-success" : "text-bg-secondary"}`;
}

async function alternarEtapa(porta, etapa, checkbox) {
  const concluida = checkbox.checked;
  checkbox.disabled = true;
  try {
    await requestJson(`${PORTAS_URL}/${porta.id}/etapas/${encodeURIComponent(etapa)}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(concluida),
    });
    showMessage("#checklist-message");
    atualizarProgresso(checkbox.closest("tr"));
  } catch (error) {
    checkbox.checked = !concluida;
    showMessage("#checklist-message", error.message);
  } finally {
    checkbox.disabled = false;
  }
}

/**
 * Troca o instalador da porta reaproveitando o PUT de edição da própria porta,
 * mantendo um único caminho de escrita para local, apartamento e instalador.
 */
async function atribuirInstalador(porta, select) {
  const instaladorId = instaladorIdDoSelect(select);
  select.disabled = true;
  try {
    await requestJson(`${PORTAS_URL}/${porta.id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        local: porta.local,
        apartamentoId: porta.apartamentoId,
        instaladorId,
      }),
    });
    porta.instaladorId = instaladorId;
    showMessage("#checklist-message");
  } catch (error) {
    select.value = porta.instaladorId ?? "";
    showMessage("#checklist-message", error.message);
  } finally {
    select.disabled = false;
  }
}

function instaladorIdDoSelect(select) {
  return select.value === "" ? null : Number(select.value);
}

function montarLinha(porta) {
  const row = document.createElement("tr");
  row.insertCell().textContent = porta.id;
  row.insertCell().textContent = porta.local;

  const celulaInstalador = row.insertCell();
  const selectInstalador = document.createElement("select");
  populate(selectInstalador, "Sem instalador", catalogoInstaladoresComoOpcoes());
  selectInstalador.className = "form-select form-select-sm";
  selectInstalador.setAttribute("aria-label", "Instalador da porta");
  selectInstalador.value = porta.instaladorId ?? "";
  selectInstalador.addEventListener("change", () => atribuirInstalador(porta, selectInstalador));
  celulaInstalador.append(selectInstalador);

  for (const [etapa, concluida] of Object.entries(porta.etapas)) {
    const celula = row.insertCell();
    celula.className = "text-center";
    const checkbox = document.createElement("input");
    checkbox.type = "checkbox";
    checkbox.className = "form-check-input";
    checkbox.checked = concluida;
    checkbox.setAttribute("aria-label", rotuloDaEtapa(etapa));
    checkbox.addEventListener("change", () => alternarEtapa(porta, etapa, checkbox));
    celula.append(checkbox);
  }

  const progresso = row.insertCell();
  progresso.className = "text-center";
  const badge = document.createElement("span");
  badge.id = "progresso-badge";
  progresso.append(badge);

  const opcoes = row.insertCell();
  opcoes.className = "text-end text-nowrap";
  const edit = document.createElement("button");
  edit.type = "button";
  edit.className = "btn btn-primary me-2";
  edit.textContent = "Editar";
  edit.addEventListener("click", () => openPorta(porta.id));
  const remove = document.createElement("button");
  remove.type = "button";
  remove.className = "btn btn-danger";
  remove.textContent = "Remover";
  remove.addEventListener("click", () => removePorta(porta.id));
  opcoes.append(edit, remove);

  atualizarProgresso(row);
  return row;
}

function montarCabecalhoEtapas(etapas) {
  document.querySelector("#etapas-header").replaceChildren();
  for (const etapa of etapas) {
    const th = document.createElement("span");
    th.className = "d-block small fw-semibold";
    th.textContent = rotuloDaEtapa(etapa);
    document.querySelector("#etapas-header").append(th);
  }
}

async function loadPortas() {
  const apartmentId = document.querySelector("#apartamento-filtro").value;
  const tbody = document.querySelector("#table_portas tbody");
  tbody.replaceChildren();
  document.querySelector("#new-porta").disabled = !apartmentId;
  if (!apartmentId) {
    atualizarTitulo();
    return;
  }
  const portas = await requestJson(`${PORTAS_URL}?apartamentoId=${apartmentId}`);
  if (!portas.length) {
    return;
  }
  atualizarTitulo();
  montarCabecalhoEtapas(Object.keys(portas[0].etapas));
  for (const porta of portas) {
    tbody.append(montarLinha(porta));
  }
}

async function loadApartamentos(obraId) {
  const select = document.querySelector("#apartamento-filtro");
  document.querySelector("#table_portas tbody").replaceChildren();
  if (!obraId) {
    catalogoApartamentos = [];
    populate(select, "Nenhuma obra selecionada", []);
    return;
  }
  catalogoApartamentos = await requestJson(`${APARTAMENTOS_URL}?obraId=${obraId}`);
  const placeholder = catalogoApartamentos.length ? "Selecione um apartamento" : "Nenhum apartamento cadastrado";
  populate(select, placeholder, catalogoApartamentosComoOpcoes());
  if (catalogoApartamentos.length) {
    select.value = catalogoApartamentos[0].id;
  }
}

async function openPorta(id = null) {
  if (saving || loading) return;
  editingId = id;
  loading = true;
  formReady = false;
  document.querySelector("#porta-form").reset();
  document.querySelector("#modal_porta .modal-title").textContent =
    id ? "Editar Porta" : "Cadastrar Nova Porta";
  document.querySelector("#save-porta").textContent = id ? "Salvar alterações" : "Cadastrar Porta";
  showMessage("#porta-form-message");
  updateFormState();
  bootstrap.Modal.getOrCreateInstance(document.querySelector("#modal_porta")).show();
  try {
    populate(
      document.querySelector("#apartamento"),
      "Selecione um apartamento",
      catalogoApartamentosComoOpcoes(),
    );
    populate(
      document.querySelector("#instalador"),
      "Sem instalador",
      catalogoInstaladoresComoOpcoes(),
    );
    if (id) {
      const porta = await requestJson(`${PORTAS_URL}/${id}`);
      document.querySelector("#local").value = porta.local;
      document.querySelector("#apartamento").value = porta.apartamentoId;
      document.querySelector("#instalador").value = porta.instaladorId ?? "";
    } else {
      document.querySelector("#apartamento").value = document.querySelector("#apartamento-filtro").value;
    }
    formReady = true;
  } catch (error) {
    showMessage("#porta-form-message", error.message);
  } finally {
    loading = false;
    updateFormState();
  }
}

async function savePorta(event) {
  event.preventDefault();
  if (saving || loading || !formReady) return;
  if (!document.querySelector("#porta-form").reportValidity()) return;
  saving = true;
  updateFormState();
  showMessage("#porta-form-message");
  try {
    await requestJson(editingId ? `${PORTAS_URL}/${editingId}` : PORTAS_URL, {
      method: editingId ? "PUT" : "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        local: document.querySelector("#local").value,
        apartamentoId: Number(document.querySelector("#apartamento").value),
        instaladorId: instaladorIdDoSelect(document.querySelector("#instalador")),
      }),
    });
  } catch (error) {
    showMessage("#porta-form-message", error.message);
    return;
  } finally {
    saving = false;
    updateFormState();
  }
  bootstrap.Modal.getOrCreateInstance(document.querySelector("#modal_porta")).hide();
  showMessage("#checklist-message");
  try {
    await loadPortas();
  } catch (error) {
    showMessage("#checklist-message", "Porta salva, mas a listagem não pôde ser atualizada. Recarregue a página.");
  }
}

async function removePorta(id) {
  if (!confirm("Realmente deseja apagar essa porta?")) return;
  showMessage("#checklist-message");
  try {
    await requestJson(`${PORTAS_URL}/${id}`, { method: "DELETE" });
    await loadPortas();
  } catch (error) {
    showMessage("#checklist-message", error.message);
  }
}

async function init() {
  document.querySelector("#new-porta").addEventListener("click", () => openPorta());
  document.querySelector("#porta-form").addEventListener("submit", savePorta);
  document.querySelector("#obra-filtro").addEventListener("change", async (event) => {
    showMessage("#checklist-message");
    try {
      await loadApartamentos(event.target.value);
      await loadPortas();
    } catch (error) {
      showMessage("#checklist-message", error.message);
    }
  });
  document.querySelector("#apartamento-filtro").addEventListener("change", async () => {
    showMessage("#checklist-message");
    try {
      await loadPortas();
    } catch (error) {
      showMessage("#checklist-message", error.message);
    }
  });
  document.querySelector("#modal_porta").addEventListener("hide.bs.modal", (event) => {
    if (saving || loading) event.preventDefault();
  });

  try {
    [catalogoObras, catalogoInstaladores] = await Promise.all([
      requestJson(OBRAS_URL),
      requestJson(INSTALADORES_URL),
    ]);
    const placeholder = catalogoObras.length ? "Selecione uma obra" : "Nenhuma obra cadastrada";
    populate(document.querySelector("#obra-filtro"), placeholder, catalogoObrasComoOpcoes());
    if (catalogoObras.length) {
      document.querySelector("#obra-filtro").value = catalogoObras[0].id;
      await loadApartamentos(catalogoObras[0].id);
      await loadPortas();
    }
  } catch (error) {
    showMessage("#checklist-message", error.message);
  }
}

init();
