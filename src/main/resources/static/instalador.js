const GLOBAL_URL = `https://6a56ca43b17de7bebbde7b73.mockapi.io/instaladores`;

async function carregarInstaladores() {
  const resposta = await fetch(GLOBAL_URL);
  const instaladores = await resposta.json();

  listarInstaladores(instaladores);
}

function listarInstaladores(instaladores) {
  let html = "";
  for (const instalador of instaladores) {
    html += `
    <tr>
      <td>${instalador.id}</td>
      <td>${instalador.nome}</td>
      <td>${instalador.telefone}</td>
      <td><button class="btn btn-danger" onclick="removerInstalador(${instalador.id})" >Remover</button></td>
    </tr>
    `;

    const tbody = document.querySelector("#table_instaladores tbody");
    tbody.innerHTML = html;
  }
}

function criarObjetoInstalador() {
  return {
    nome: document.querySelector("#nome").value,
    telefone: document.querySelector("#telefone").value || 0,
  };
}

async function adicionarInstalador() {
  const instalador = criarObjetoInstalador();

  try {
    await fetch(GLOBAL_URL, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(instalador),
    });
    limparFormulario();
    fecharModal();
    carregarInstaladores();
  } catch (error) {
    console.error(error);
    alert("Não foi possível cadastrar o instalador");
  }
}

function limparFormulario() {
  document.querySelector("#nome").value = "";
  document.querySelector("#telefone").value = "";
}

function fecharModal() {
  const modalHtml = document.querySelector("#modal_instalador");
  const modal = bootstrap.Modal.getOrCreateInstance(modalHtml);
  modal.hide();
}

async function removerInstalador(id) {
  // Regra de parada.
  if (!confirm("Realmente deseja apagar esse produto?")) {
    return;
  }
  const url = `${GLOBAL_URL}/${id}`;

  try {
    await fetch(url, {
      method: "DELETE",
    });
  } catch (error) {
    consolo.error(error);
    alert("Não foi possível apagar este produto.");
  } finally {
    carregarInstaladores();
  }
}

carregarInstaladores();
