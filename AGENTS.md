# AGENTS.md

Projeto de TCC (escola): aplicação web Spring Boot 4.1.0 / Java 26 / Thymeleaf MVC para acompanhar checklists de instalação de portas em obras. Textos da interface, nomes de views e mensagens de commit são em português brasileiro — mantenha assim.

## Como buildar e rodar
- Use o wrapper do Maven. No Windows: `.\mvnw.cmd` (comandos aqui assumem PowerShell). Maven 3.9.16 fixado via `wrapper/maven-wrapper.properties` (o Maven em si não é garantido no PATH).
- Banco local de dev: `docker compose up -d mysql` (MySQL 8.4, padrões `localhost:3306`, db/user/senha todos `obracontrol`, sobrescrevíveis via `.env`; `.env` é gitignored, `.env.example` lista as variáveis).
- Rodar a aplicação: `.\mvnw.cmd spring-boot:run` (porta 8080). `docker compose up` também builda e roda o container da app.
- Testes: `.\mvnw.cmd test`. Classes `@SpringBootTest` cobrem context-loads, operações dos repositórios JDBC e cadastro de obras/consultas auxiliares via MockMvc. O perfil de teste usa H2 em modo MySQL com Flyway **desativado** e aplica `schema.sql`/`data.sql` (réplica enxuta de `sql/`) via `spring.sql.init.mode=always`; nunca toca no MySQL. Testes que gravam dados usam `@Transactional` para isolamento.

## Pegadinhas de arquitetura (verificadas — não "corrigir")
- Persistência é híbrida: `PortaRepository` (com.cursojava.ObraControl.repository) é uma **`ArrayList` em memória** feita na mão, NÃO o banco — não tocar. Já `ObraRepository` e `InstaladorRepository` persistem no **MySQL real via `JdbcTemplate`** (`spring-boot-starter-jdbc`) contra o schema canônico de `sql/` (JOIN construtora/cidade devolvem os nomes que a UI espera; POST de obra recebe `CadastroObra` com `cidadeId` e `construtoraId`, valida vínculos existentes e não cria cadastros auxiliares). `CadastroRestController` expõe `/api/estados`, `/api/estados/{estadoId}/cidades` e `/api/construtoras` para os seletores. Não existe camada JPA/Spring Data. As classes de modelo (`Porta`, `Obra`, `Instalador`, `Apartamento`) são POJOs simples.
- O JS do front-end (`static/obra.js`, `static/instalador.js`) chama **endpoints REST do próprio backend Spring** via `GLOBAL_URL` relativo: `/api/obras` (`ObraRestController`) e `/api/instaladores` (`InstaladorRestController`), ambos JDBC → MySQL. Resta `PortaRestController` (`/api/portas`), em memória.
- Edição: `PUT /api/obras/{id}` recebe `CadastroObra`; `PUT /api/instaladores/{id}` recebe `CadastroInstalador` (`nome`, `telefone`), também usado no POST. Os serviços validam criação/edição e retornam 404 na consulta/edição de registros inexistentes. Obras retornam nomes e `cidadeId`, `estadoId`, `construtoraId` para preencher os seletores. As telas de cadastro reutilizam os modais para editar.
- Schema é gerido por **Flyway ativo no boot** (`spring.flyway.enabled=true`): migrações canônicas em `src/main/resources/db/migration/` (`V001__create_tables.sql` cria as tabelas, `V002__seed_data.sql` insere o seed). O Flyway só roda na config principal; o perfil de teste o desativa (H2). Migrações novas seguem `V00N__descricao.sql`. **Não** aplicar `sql/001`–`002` à mão: num banco que já tem o schema pré-existente (aplicado manualmente), o boot falha com `Validate failed` — para dev, resetar o banco (`docker compose down -v` apaga o volume do MySQL e o Flyway recria tudo do zero; ou DROP DATABASE + CREATE DATABASE e deixe o Flyway recriar).
- O nome do pacote é de caixa mista: `com.cursojava.ObraControl` (casando com a classe da aplicação e os testes). Renomear para `com.cursojava.obracontrol` quebra pacotes/testes até tudo ser reorganizado junto — trate qualquer "correção" de caixa como um refactor grande e deliberado.

## Padrões do projeto
- **Idiomas**: campos do banco e textos exibidos ao usuário em pt-BR; nomes de **funções/métodos em inglês**.
- **Banco de dados**: PKs sempre como `id`; FKs sempre como `entidade_id` (ex.: `obra_id`, `apartamento_id`, `instalador_id`). Tabelas associativas (junção) usam `_` no nome (ex.: `obra_instalador`).
- **Camadas**: `controller` -> `service` -> `repository` (injeção via construtor). Restes novos sigam `PortaRestController`.

## Convenções
- Controllers MVC mapeiam URLs para nomes de views Thymeleaf em `src/main/resources/templates`; as views estendem `layout/base.html` (thymeleaf-layout-dialect).
- Os scripts em `sql/` são a fonte canônica do schema; mantenha-os em sincronia ao mudar tabelas (o Flyway não pega drift). Note que `sql/001`–`002` foram convertidos nas migrações `db/migration`; `sql/003`–`008` são queries de consulta usadas como referência.
- `target/`, `.env` e `.vscode/` são gitignored. A UI usa Bootstrap 5.3 + bootstrap-icons via CDN.
