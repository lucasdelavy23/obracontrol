# AGENTS.md

Projeto de TCC (escola): aplicação web Spring Boot 4.1.0 / Java 26 / Thymeleaf MVC para acompanhar checklists de instalação de portas em obras. Textos da interface, nomes de views e mensagens de commit são em português brasileiro — mantenha assim.

## Como buildar e rodar
- Use o wrapper do Maven. No Windows: `.\mvnw.cmd` (comandos aqui assumem PowerShell). Maven 3.9.16 fixado via `wrapper/maven-wrapper.properties` (o Maven em si não é garantido no PATH).
- Banco local de dev: `docker compose up -d mysql` (MySQL 8.4, padrões `localhost:3306`, db/user/senha todos `obracontrol`, sobrescrevíveis via `.env`; `.env` é gitignored, `.env.example` lista as variáveis).
- Rodar a aplicação: `.\mvnw.cmd spring-boot:run` (porta 8080). `docker compose up` também builda e roda o container da app.
- Testes: `.\mvnw.cmd test`. Existe exatamente um teste `@SpringBootTest` (context-loads). O perfil de teste usa H2 em modo MySQL com Flyway **desativado**; nunca toca no MySQL.

## Pegadinhas de arquitetura (verificadas — não "corrigir")
- A única persistência do backend é `PortaRepository` (com.cursojava.ObraControl.repository): uma **`ArrayList` em memória** feita na mão, NÃO o banco. Não existe camada JPA/DAO em lugar nenhum. As classes de modelo (`Porta`, `Obra`, `Apartamento`) são POJOs simples.
- O JS do front-end (`static/obra.js`, `static/instalador.js`) chama uma **API mock (mockapi.io)** via `GLOBAL_URL`, não o backend Spring. Não existe endpoint REST `/api/obras` nem `/api/instaladores`. Só existe `PortaRestController` (`/api/portas`).
- `spring.flyway.enabled=true` na config principal, mas **não existe diretório `db/migration`** — o schema em `sql/` (numerados `001`–`008`) é aplicado manualmente. O Flyway nunca roda migration aqui.
- O nome do pacote é de caixa mista: `com.cursojava.ObraControl` (casando com a classe da aplicação e os testes). Renomear para `com.cursojava.obracontrol` quebra pacotes/testes até tudo ser reorganizado junto — trate qualquer "correção" de caixa como um refactor grande e deliberado.

## Padrões do projeto
- **Idiomas**: campos do banco e textos exibidos ao usuário em pt-BR; nomes de **funções/métodos em inglês**.
- **Banco de dados**: PKs sempre como `id`; FKs sempre como `entidade_id` (ex.: `obra_id`, `apartamento_id`, `instalador_id`). Tabelas associativas (junção) usam `_` no nome (ex.: `obra_instalador`).
- **Camadas**: `controller` -> `service` -> `repository` (injeção via construtor). Restes novos sigam `PortaRestController`.

## Convenções
- Controllers MVC mapeiam URLs para nomes de views Thymeleaf em `src/main/resources/templates`; as views estendem `layout/base.html` (thymeleaf-layout-dialect).
- Os scripts em `sql/` são a fonte canônica do schema; mantenha-os em sincronia ao mudar tabelas (o Flyway não pega drift).
- `target/`, `.env` e `.vscode/` são gitignored. A UI usa Bootstrap 5.3 + bootstrap-icons via CDN.