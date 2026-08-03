const GLOBAL_URL = `https://6a56ca43b17de7bebbde7b73.mockapi.io/instaladores`; // Ver como usar a mesma api ou outra para poder carregar os dados.

async function carregarObras() {
  const resposta = await fetch(GLOBAL_URL);
  const obras = await resposta.json();

  listarInstaladores(obras);
}

function listarInstaladores(obras) {
  let html = "";
  for (const obra of obras) {
    html += `
    <tr>
      <td>${obra.id}</td>
      <td>${obra.nome}</td>
      <td>${obra.construtora}</td>
      <td>${obra.endereco}</td>
      <td><button class="btn btn-danger" onclick="removerObra(${obra.id})" >Remover</button></td>
    </tr>
    `;

    const tbody = document.querySelector("#table_obra tbody");
    tbody.innerHTML = html;
  }
}

function criarObjetoObra() {
  return {
    nome: document.querySelector("#nomeObra").value,
    construtora: document.querySelector("#construtora").value || 0,
    endereco: document.querySelector("#endereco").value || 0,
  };
}

async function adicionarObra() {
  const obra = criarObjetoObra();

  try {
    await fetch(GLOBAL_URL, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(obra),
    });
    limparFormulario();
    fecharModal();
    carregarObras();
  } catch (error) {
    console.error(error);
    alert("Não foi possível cadastrar a obra.");
  }
}

function limparFormulario() {
  document.querySelector("#nome").value = "";
  document.querySelector("#construtora").value = "";
  document.querySelector("#endereco").value = "";
}

function fecharModal() {
  const modalHtml = document.querySelector("#modal_obra");
  const modal = bootstrap.Modal.getOrCreateInstance(modalHtml);
  modal.hide();
}

async function removerObra(id) {
  // Regra de parada.
  if (!confirm("Realmente deseja apagar esse registro?")) {
    return;
  }
  const url = `${GLOBAL_URL}/${id}`;

  try {
    await fetch(url, {
      method: "DELETE",
    });
  } catch (error) {
    consolo.error(error);
    alert("Não foi possível apagar este registro.");
  } finally {
    carregarObras();
  }
}

carregarObras();
