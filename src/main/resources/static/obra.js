const GLOBAL_URL = `https://6a56ca43b17de7bebbde7b73.mockapi.io/obras`; // Ver como usar a mesma api ou outra para poder carregar os dados.

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
    construtora: document.querySelector("#construtora").selectedOptions[0]?.value || 0,
    endereco: document.querySelector("#endereco").value || 0,
  };
}

async function adicionarObra() {
  const obra = criarObjetoObra();


  //try {
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
  // } catch (error) {
  //   console.error(error);
  //   alert("Não foi possível cadastrar a obra.");
  // }
}

function limparFormulario() {
  document.querySelector("#nomeObra").value = "";
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

// Lista de construturas
// TODO: Criar resource na mockapi
const construtoras = [
  { id: 1, nome: "Dallo" },
  { id: 2, nome: "Pascoalotto" },
  { id: 3, nome: "Procave" },
  { id: 4, nome: "FG" },  
];

function init() {
  popularConstrutoras(construtoras);
  carregarObras();
}

init();




function popularConstrutoras(construtoras) {
  const construtoraSelect = document.querySelector("#construtora");
  let html = "";
  for (const construtora of construtoras) {
    html += `<option value="${construtora.id}">${construtora.nome}</option>`;
  }
  construtoraSelect.innerHTML = html;
}



