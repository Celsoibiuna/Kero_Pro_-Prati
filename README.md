# KeroPro — projeto completo

Marketplace mobile que conecta clientes a técnicos de TI e manutenção,
com contratação orientada pelo algoritmo **Score de Excelência** (Content-Based
Filtering). Este repositório reorganiza o protótipo em três camadas separadas,
seguindo a stack pedida: **HTML + CSS + JavaScript/React** no front-end,
**Java (Spring Boot)** no back-end e **MySQL** como banco de dados.

```
keropro/
├── database/       → schema.sql (DDL) e seed.sql (dados de demonstração)
├── backend/        → API REST em Java 17 + Spring Boot + JPA
├── frontend/       → interface do APP em React (build com npm), consome a API
├── frontend-html/  → a MESMA interface do app em HTML + CSS + JS puro (sem build)
└── site/           → site institucional (landing page + cadastro de
                       cliente e de profissional), também em HTML + CSS + JS puro
```

Há duas coisas diferentes neste projeto:

- **`frontend/` e `frontend-html/`** — o **aplicativo** (app mobile), com os
  fluxos de busca, contratação e acompanhamento de pedido.
- **`site/`** — o **site institucional/marketing**, com a apresentação do
  produto e os formulários de cadastro de cliente e de profissional. É por
  aqui que uma pessoa nova conhece o KeroPro e cria a conta antes de baixar
  o app.

Há duas versões de front-end, ambas conversando com a mesma API Java:

- **`frontend/`** — React, precisa de `npm install` / `npm start`.
- **`frontend-html/`** — HTML/CSS/JS puro, sem dependências. Basta abrir
  `frontend-html/index.html` no navegador (ou servir a pasta com qualquer
  servidor estático). Se a API Java não estiver rodando, ela funciona
  sozinha em **modo offline**, usando os dados de `js/mock-data.js` — útil
  para demonstrações rápidas sem precisar subir o back-end.

## 1. Banco de dados (MySQL)

```bash
mysql -u root -p < database/schema.sql
mysql -u root -p < database/seed.sql
```

Isso cria o schema `keropro` com as tabelas `usuarios`, `clientes`,
`profissionais`, `categorias`, `score_profissional`, `pedidos` e `avaliacoes`,
já populadas com os dois personas da proposta (Mariana e Carlos) e mais três
profissionais de exemplo.

## 2. Back-end (Java / Spring Boot)

Requer Java 17+ e Maven.

```bash
cd backend
# ajuste usuário/senha do MySQL em src/main/resources/application.properties
mvn spring-boot:run
```

A API sobe em `http://localhost:8080`. Principais endpoints:

| Método | Rota                                   | Descrição                                             |
|--------|-----------------------------------------|--------------------------------------------------------|
| POST   | `/api/auth/login`                       | Autentica cliente ou profissional                      |
| GET    | `/api/categorias`                       | Lista categorias de serviço                             |
| GET    | `/api/profissionais?categoria=&clienteLat=&clienteLng=&emergencia=` | Lista profissionais ranqueados por Score de Excelência, com distância (Haversine) e orçamento calculados |
| GET    | `/api/pedidos/pendentes/{profissionalId}` | Pedidos pendentes de um profissional (painel)         |
| POST   | `/api/pedidos`                          | Cliente contrata um profissional                        |
| PATCH  | `/api/pedidos/{id}/avancar`             | Avança o status do pedido (simula sincronização em tempo real) |

A lógica de negócio fica isolada em `service/`:
- `ScoreService.java` — calcula o Score de Excelência a partir de formação, certificações, avaliações e tempo de resposta.
- `OrcamentoService.java` — distância via GPS (Haversine) e preço estimado.
- `PedidoService.java` — máquina de estados do pedido.

## 3. Front-end — versão React (build com npm)

Requer Node.js 18+.

```bash
cd frontend
npm install
npm start
```

Abre em `http://localhost:3000` e consome a API acima. Estrutura detalhada em
`frontend/README.md`.

## 4. Front-end — versão HTML/CSS/JS puro (sem build)

Não precisa de Node nem de `npm install`. Duas formas de usar:

```bash
# Opção 1: abrir direto
open frontend-html/index.html          # macOS
# ou apenas dar duplo-clique no arquivo

# Opção 2: servir como página estática (recomendado, evita restrições de CORS)
cd frontend-html
python3 -m http.server 5500
# depois acesse http://localhost:5500
```

Estrutura:

```
frontend-html/
├── index.html          # marcação da página, carrega css e js
├── css/
│   └── style.css        # todo o estilo visual (tokens, layout, telas)
└── js/
    ├── mock-data.js      # dados de fallback + fórmulas replicadas do back-end
    ├── api.js             # chamadas fetch para a API Java
    ├── ui.js               # ícones SVG e funções que geram HTML (gauge, stepper, mapa…)
    └── app.js               # estado da aplicação, navegação e eventos de clique
```

Essa versão tenta sempre falar com a API Java em `http://localhost:8080/api`.
Se a API não responder, ela cai automaticamente para os dados de
`mock-data.js` (mostrando um aviso "modo offline" na tela), então dá para
testar a interface completa mesmo sem MySQL/back-end rodando.

## Fluxo de demonstração

1. Suba o MySQL com o schema/seed, depois o back-end, depois o front-end.
2. Na tela inicial, entre como **Cliente** → escolha uma categoria → veja os
   profissionais ranqueados pelo Score de Excelência → abra o perfil de um
   deles → "Contratar agora" grava um pedido real no MySQL via API.
3. Acompanhe o status do pedido avançando manualmente (simula os updates que,
   em produção, viriam de um serviço de tempo real).
4. Saia e entre como **Profissional** para ver o painel com os pedidos
   pendentes vindos do banco e aceitar um deles.

## 5. Site institucional + cadastro (`site/`)

Também é HTML/CSS/JS puro, sem build. Para rodar:

```bash
cd site
python3 -m http.server 5500
# depois acesse http://localhost:5500
```

```
site/
├── index.html               # landing page (hero, features, Score de Excelência,
│                             #   personas, seção de download do app, FAQ)
├── cadastro-cliente.html     # cadastro completo de cliente
├── cadastro-prestador.html    # cadastro completo de profissional (formação/certificações)
├── css/
│   ├── variables.css          # tokens de marca
│   ├── base.css                # reset, tipografia, grid
│   ├── components.css           # navbar, botões, cards, steps, footer…
│   └── pages.css                 # hero, seções da home e layout dos formulários
├── js/
│   ├── icons.js                  # biblioteca de ícones SVG inline
│   ├── main.js                    # animações de entrada, FAQ, gauge decorativo do hero
│   ├── validation.js               # CPF/CNPJ (módulo 11), máscaras, força de senha
│   ├── auth-api.js                  # chamadas fetch para /api/auth/cadastro/*
│   ├── cadastro-cliente.js           # validação + envio do form de cliente
│   └── cadastro-prestador.js          # idem, + toggle CPF/CNPJ e upload de comprovantes
└── assets/
    ├── logo-mark.png, favicon.png, apple-touch-icon.png, og-image.png
```

### O que os cadastros pedem

- **Cliente**: dados pessoais (nome, CPF, nascimento, e-mail, telefone),
  endereço completo, senha com medidor de força, aceite de Termos e de LGPD,
  opt-in de marketing (opcional).
- **Profissional**: os mesmos dados pessoais (CPF **ou** CNPJ, com toggle),
  categoria/especialidade, anos de experiência, raio de atendimento, bio,
  **formação acadêmica e certificações** (que alimentam o Score de
  Excelência), upload de comprovantes, endereço, senha, e três consentimentos
  obrigatórios (Termos, LGPD e autorização de verificação de dados).

### Segurança implementada

- **Senha nunca em texto puro**: o back-end usa `BCryptPasswordEncoder`
  (`SecurityConfig.java`) para gerar o hash antes de gravar no MySQL.
- **Validação em duas camadas**: o front-end valida em tempo real (CPF/CNPJ
  por módulo 11, e-mail, idade mínima de 18 anos, força de senha, telefone,
  CEP) e o back-end **revalida tudo de novo** via Bean Validation
  (`@NotBlank`, `@Pattern`, `@Email`, `@AssertTrue` para os consentimentos) —
  nunca confie só no front-end.
- **E-mail e CPF/CNPJ únicos**, checados no banco antes de criar a conta.
- **Honeypot anti-bot**: campo invisível (`name="website"`) nos dois
  formulários; se vier preenchido, o envio é silenciosamente descartado.
- **LGPD**: consentimento explícito e obrigatório, com timestamp gravado em
  `termos_aceitos_em`; coleta de dados limitada à finalidade declarada.
- **Verificação de profissionais**: todo cadastro de prestador nasce com
  `status_verificacao = PENDENTE` e `disponivel = false` — só aparece para
  clientes depois de uma validação manual da formação/certificações
  informadas (endpoint de aprovação fica como próximo passo natural do
  back-end).
- Endpoints: `POST /api/auth/cadastro/cliente` e
  `POST /api/auth/cadastro/profissional`, ambos em
  `RegisterController.java`.

### O que fica como próximo passo (fora do escopo deste protótipo)

- Upload real dos arquivos de comprovante para um storage (S3, disco) — hoje
  o formulário já monta a UI e envia a contagem de arquivos, mas o
  armazenamento físico não está implementado.
- Geocodificação do endereço informado no cadastro (hoje o profissional
  recebe coordenadas provisórias do centro de Sorocaba até isso ser feito).
- Um painel administrativo para aprovar/rejeitar profissionais pendentes.
- Autenticação por sessão/JWT no login (hoje o login devolve um token
  fictício — ver `AuthController.java`).

## Identidade visual

A logo (marca "P" com seta de crescimento, pin de localização e estrela) e a
paleta de cores do site e do app — navy `#071B3A`, azul vívido `#00A3E0` e
dourado `#F5A623` — foram extraídas diretamente do arquivo de logo oficial do
projeto, para manter tudo consistente entre o site institucional e o
aplicativo.

## Observações

- Este é um projeto acadêmico/protótipo: autenticação, senha e token são
  simplificados (sem BCrypt/JWT) e alguns valores (coordenadas do cliente,
  IDs de sessão) estão fixos no front-end para fins de demonstração.
- A integração de mapa é uma representação visual simplificada; a proposta
  original prevê uso do OpenStreetMap via Flutter/Dart em um app nativo.
