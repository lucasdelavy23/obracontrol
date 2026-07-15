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
      <td></td>
    </tr>
    `;

    const tbody = document.querySelector("#table_instaladores tbody");
    tbody.innerHTML = html;
  }
}

carregarInstaladores();
