# KeroPro

## Marketplace de Serviços sob Demanda

O **KeroPro** é uma plataforma em desenvolvimento criada para conectar **clientes e profissionais de serviços sob demanda**, facilitando a busca, solicitação e acompanhamento de serviços de acordo com categoria, localização, disponibilidade e nível de urgência.

A proposta também contempla mecanismos de **validação profissional, avaliações, pontuação e organização das informações**, buscando oferecer mais segurança e praticidade para o cliente.

> 🚧 **Status do projeto:** Em desenvolvimento  
> 📅 **Previsão de entrega da versão atual:** Outubro de 2026

---

## 🎯 Objetivo

O objetivo do KeroPro é oferecer uma plataforma que facilite a conexão entre clientes que precisam de um serviço e profissionais disponíveis para atendê-los.

A solução foi pensada para considerar diferentes informações durante esse processo, como:

- Categoria do serviço;
- Localização do cliente;
- Localização do profissional;
- Distância entre cliente e profissional;
- Disponibilidade do profissional;
- Nível de urgência;
- Experiência profissional;
- Formação e certificações;
- Avaliações dos clientes;
- Pontuação do profissional;
- Informações utilizadas para apoiar a escolha do profissional.

---

## 💡 Problema

Encontrar um profissional para realizar um serviço pode ser uma tarefa desorganizada, principalmente quando existe necessidade de atendimento rápido, localização próxima e confiança nas informações apresentadas.

O KeroPro foi idealizado para centralizar essas informações em uma plataforma, permitindo que o cliente encontre profissionais de maneira mais organizada e tenha acesso a dados que possam auxiliar na tomada de decisão.

---

## 🚀 Proposta da solução

A plataforma está sendo desenvolvida para permitir que o cliente possa:

1. Realizar seu cadastro;
2. Buscar profissionais de acordo com a categoria do serviço;
3. Consultar informações do profissional;
4. Considerar localização e distância;
5. Verificar disponibilidade;
6. Solicitar um serviço;
7. Informar o nível de urgência;
8. Acompanhar o andamento da solicitação;
9. Avaliar o profissional após a realização do serviço.

Para os profissionais, a proposta contempla recursos relacionados ao cadastro, informações profissionais, formação, certificações, disponibilidade e localização.

---

# 🏗️ Arquitetura do projeto

O projeto está sendo organizado em diferentes componentes, buscando separar as responsabilidades da aplicação.

```text
KeroPro
│
├── Frontend
│   └── Interface da aplicação
│
├── Backend
│   ├── Controllers
│   ├── Services
│   ├── Repositories
│   ├── DTOs
│   ├── Models
│   └── Configurações
│
├── Database
│   ├── schema.sql
│   └── seed.sql
│
└── Integrações
    ├── Firebase
    └── Google Maps API
```

O backend utiliza uma organização em camadas, separando responsabilidades entre **controllers, services, repositories, DTOs e models**.

---

# ⚙️ Backend

O backend está sendo desenvolvido utilizando **Java e Spring Boot**.

A estrutura atual está organizada da seguinte forma:

```text
backend/
└── src/
    └── main/
        └── java/
            └── com/
                └── keropro/
                    ├── config/
                    ├── controller/
                    ├── dto/
                    ├── model/
                    ├── repository/
                    └── service/
```

## Controllers

A camada de controllers é responsável pelo recebimento das requisições da aplicação e pela exposição dos endpoints.

Atualmente existem estruturas relacionadas a:

- Autenticação;
- Cadastro;
- Clientes;
- Categorias;
- Pedidos;
- Profissionais.

---

## Services

A camada de services concentra regras e operações relacionadas ao funcionamento da aplicação.

Entre as estruturas existentes estão serviços relacionados a:

- Pedidos;
- Orçamentos;
- Score dos profissionais.

O `OrcamentoService`, por exemplo, possui lógica relacionada ao cálculo de distância entre coordenadas geográficas e ao cálculo de orçamento estimado.

---

## Repositories

A camada de repositories é responsável pela persistência e consulta dos dados.

O projeto utiliza **Spring Data JPA** para trabalhar com o banco de dados.

Entre os repositories existentes estão estruturas relacionadas a:

- Usuários;
- Clientes;
- Endereços;
- Categorias;
- Profissionais;
- Pedidos.

---

## DTOs

O projeto utiliza **Data Transfer Objects (DTOs)** para organizar os dados utilizados nas requisições e respostas da aplicação.

Também existem validações utilizando recursos do Jakarta Validation.

Entre os DTOs existentes estão estruturas relacionadas a:

- Cadastro de clientes;
- Cadastro de profissionais;
- Login;
- Pedidos;
- Endereços;
- Formação;
- Respostas da aplicação.

---

# 🗄️ Banco de dados

O KeroPro possui um esquema de banco de dados desenvolvido em **MySQL 8+**.

O arquivo principal de estruturação é:

```text
database/
├── schema.sql
└── seed.sql
```

O `schema.sql` contém a criação do banco, tabelas, relacionamentos, restrições e índices.

---

## 📊 Principais entidades

### Usuários

A tabela `usuarios` representa a base comum de autenticação para clientes e profissionais.

Entre os dados estruturados estão:

- Nome;
- E-mail;
- Senha armazenada como hash;
- Tipo de usuário;
- CPF;
- CNPJ;
- Telefone;
- Data de nascimento;
- Consentimento LGPD;
- Aceite de termos;
- Preferência de marketing;
- Informações relacionadas ao login.

---

### Clientes

A tabela `clientes` representa o perfil específico do cliente associado ao usuário.

---

### Profissionais

A tabela `profissionais` contém informações específicas dos profissionais cadastrados na plataforma.

Entre elas:

- Categoria;
- Especialidade;
- Anos de experiência;
- Raio de atendimento;
- Biografia;
- Instituição de formação;
- Curso de formação;
- Ano de conclusão;
- Certificações;
- Latitude;
- Longitude;
- Preço base;
- Status de verificação;
- Disponibilidade.

---

### Categorias

A tabela `categorias` organiza os diferentes tipos de serviços oferecidos na plataforma.

---

### Endereços

A tabela `enderecos` armazena os dados de endereço associados aos usuários.

---

### Comprovantes

A tabela `comprovantes` foi estruturada para registrar documentos enviados pelo profissional, como:

- Diplomas;
- Certificados;
- Outros comprovantes.

O arquivo físico é previsto para ser armazenado externamente, enquanto o banco mantém sua referência.

---

### Pedidos

A tabela `pedidos` representa as solicitações de serviço realizadas pelos clientes.

Um pedido possui informações como:

- Cliente;
- Profissional;
- Categoria;
- Descrição;
- Status;
- Distância;
- Valor estimado;
- Indicação de emergência;
- Data de criação;
- Data de atualização.

Os status previstos incluem:

```text
PENDENTE
ACEITO
A_CAMINHO
EM_EXECUCAO
CONCLUIDO
```

---

### Avaliações

A tabela `avaliacoes` representa o feedback realizado pelo cliente após um serviço.

Cada avaliação possui:

- Nota;
- Comentário;
- Data da avaliação.

A nota é estruturada em uma escala de **1 a 5**.

---

### Score do profissional

O projeto possui uma estrutura específica para o **Score de Excelência** dos profissionais.

O score considera componentes relacionados a:

- Formação;
- Certificações;
- Avaliações;
- Tempo de resposta;
- Score total.

A estrutura permite que essas informações sejam utilizadas futuramente no processo de classificação dos profissionais.

---

# 📍 Localização

A localização é um dos elementos importantes da proposta do KeroPro.

O projeto possui estrutura para trabalhar com:

- Latitude;
- Longitude;
- Distância entre pontos;
- Raio de atendimento;
- Localização do profissional;
- Localização do cliente.

O backend possui uma implementação para cálculo de distância geográfica entre coordenadas utilizando a **fórmula de Haversine**.

A solução também contempla integração com recursos da **Google Maps API**.

---

# 🔐 Segurança

A segurança é considerada na estrutura atual do projeto.

Entre os recursos existentes ou estruturados estão:

- Armazenamento de senha através de `senha_hash`;
- Utilização de `BCryptPasswordEncoder`;
- Validação de dados através de DTOs;
- Controle de tipos de usuários;
- Estrutura para validação de profissionais;
- Registro de comprovantes;
- Controle relacionado ao consentimento LGPD;
- Controle de tentativas de login.

O projeto também possui uma estrutura inicial relacionada ao Spring Security.

### Autenticação

A autenticação completa com token ainda faz parte da evolução do projeto.

A implementação atual contém uma estrutura inicial para autenticação e segurança, mas determinados mecanismos ainda serão desenvolvidos e integrados durante a evolução da aplicação.

> **Importante:** funcionalidades que ainda estão em desenvolvimento não são apresentadas como concluídas.

---

# ⭐ Avaliações e Score

O sistema foi projetado para utilizar as avaliações dos clientes como uma das informações utilizadas na composição do desempenho do profissional.

O modelo contempla:

```text
Formação
    +
Certificações
    +
Avaliações
    +
Tempo de resposta
    ↓
Score do profissional
```

A estrutura está preparada para que o score seja calculado e atualizado conforme a evolução das regras de negócio.

---

# 💰 Orçamento

O projeto possui uma estrutura de serviço destinada ao cálculo de orçamento.

O `OrcamentoService` trabalha com informações como:

- Distância entre cliente e profissional;
- Taxa por quilômetro;
- Valor base;
- Multiplicador de emergência.

O cálculo de distância utiliza coordenadas geográficas e a fórmula de Haversine.

---

# 🌐 Frontend

O repositório também possui uma estrutura de frontend em:

- HTML5;
- CSS3;
- JavaScript.

Estrutura atual:

```text
frontend-html/
├── assets/
├── css/
├── js/
└── index.html
```

A evolução da solução também contempla o desenvolvimento utilizando:

- Flutter;
- Dart.

---

# 🛠️ Tecnologias

## Backend

- Java
- Spring Boot
- Spring Data JPA
- Spring Security
- BCrypt
- Maven

## Banco de dados

- MySQL 8+

## Frontend e aplicações

- Flutter
- Dart
- HTML5
- CSS3
- JavaScript

## Integrações

- Firebase
- Google Maps API

## Ferramentas

- Git
- GitHub

---

# 📂 Estrutura do repositório

```text
Kero_Pro_Prati/
│
├── backend/
│   ├── src/
│   │   └── main/
│   │       └── java/
│   │           └── com/
│   │               └── keropro/
│   │                   ├── config/
│   │                   ├── controller/
│   │                   ├── dto/
│   │                   ├── model/
│   │                   ├── repository/
│   │                   └── service/
│   │
│   ├── resources/
│   └── pom.xml
│
├── database/
│   ├── schema.sql
│   └── seed.sql
│
├── frontend-html/
│   ├── assets/
│   ├── css/
│   ├── js/
│   └── index.html
│
├── site/
│
└── README.md
```

---

# 🔄 Estratégia de desenvolvimento

O desenvolvimento atual utiliza branches para separar a versão principal do desenvolvimento.

## Branch principal

```text
main
```

A `main` representa a versão principal do projeto.

## Branch de desenvolvimento

```text
dev
```

A `dev` é utilizada para desenvolvimento, testes e integração das novas funcionalidades.

Fluxo de trabalho:

```text
dev
 ↓
Desenvolvimento
 ↓
Testes
 ↓
Revisão
 ↓
Merge
 ↓
main
```

Essa organização permite trabalhar nas funcionalidades sem alterar diretamente a versão principal do projeto.

---

# 👥 Desenvolvimento colaborativo

O KeroPro está sendo desenvolvido de forma colaborativa na etapa atual.

As atividades envolvem:

- Evolução da solução;
- Discussão de requisitos;
- Definição de regras de negócio;
- Desenvolvimento do backend;
- Estruturação do banco de dados;
- Desenvolvimento do frontend;
- Integração das funcionalidades;
- Testes;
- Organização do código utilizando Git e GitHub.

A equipe atual trabalha na evolução do projeto visando a entrega prevista para outubro de 2026.

---

# 📈 Evolução do projeto

O KeroPro teve sua origem durante a formação em **Análise e Desenvolvimento de Sistemas**.

A ideia foi inicialmente desenvolvida em uma etapa acadêmica e posteriormente retomada para uma nova fase de desenvolvimento colaborativo.

Na etapa atual, o projeto está passando por evolução da arquitetura, revisão de requisitos, estruturação do backend, banco de dados, frontend e regras de negócio.

A proposta original também vem sendo ampliada com recursos relacionados a:

- Geolocalização;
- Disponibilidade em tempo real;
- Urgência;
- Validação profissional;
- Certificações;
- Avaliações;
- Score;
- Segurança;
- Integração entre diferentes componentes da aplicação.

---

# 📚 Principais aprendizados

O desenvolvimento do KeroPro proporciona experiência prática em diferentes etapas da construção de uma solução de software.

Entre os principais aprendizados estão:

- Análise de sistemas;
- Levantamento de requisitos;
- Identificação de regras de negócio;
- Modelagem de dados;
- Desenvolvimento de software;
- Desenvolvimento de APIs;
- Organização de aplicações em camadas;
- Persistência de dados;
- Validação de informações;
- Segurança;
- Geolocalização;
- Integração entre sistemas;
- Trabalho colaborativo;
- Controle de versões com Git e GitHub.

---

# 🚧 Status atual

O KeroPro está em desenvolvimento.

### Atualmente estruturado

- Arquitetura inicial do backend;
- Organização por camadas;
- Controllers;
- Services;
- Repositories;
- DTOs;
- Models;
- Estrutura do banco de dados;
- Relacionamentos entre entidades;
- Validações;
- BCrypt para hash de senhas;
- Cálculo de distância geográfica;
- Estrutura inicial de autenticação;
- Estrutura para validação de profissionais;
- Estrutura para avaliações;
- Estrutura para score dos profissionais.

### Em desenvolvimento

- Integração completa entre frontend, backend e banco de dados;
- Evolução da autenticação;
- Fluxo completo de cadastro;
- Validação dos profissionais;
- Implementação e integração das funcionalidades;
- Testes;
- Refinamento das regras de negócio.

### Próximas etapas

- Continuidade do desenvolvimento;
- Integração dos componentes;
- Testes das funcionalidades;
- Correção e refinamento;
- Preparação da versão final para entrega.

---

# 🎓 Contexto acadêmico e profissional

O KeroPro representa uma etapa importante da minha formação e da minha transição de carreira para Tecnologia.

Durante a formação em **Análise e Desenvolvimento de Sistemas**, tive contato com diferentes etapas de desenvolvimento de software e participei da concepção e evolução da solução.

O projeto também permitiu aplicar conhecimentos relacionados à análise de sistemas, desenvolvimento, banco de dados, regras de negócio e trabalho colaborativo.

A experiência adquirida durante o desenvolvimento do KeroPro contribui para minha preparação para oportunidades nas áreas de **Análise de Sistemas, Dados e Desenvolvimento de Software**.

---

# 📌 Observação

Este README descreve o estado atual e a proposta de evolução do projeto.

Como o KeroPro ainda está em desenvolvimento, algumas funcionalidades apresentadas representam **estruturas já implementadas, parcialmente implementadas ou previstas na evolução da solução**.

A documentação será atualizada conforme novas funcionalidades forem desenvolvidas, testadas e integradas.

---

## 👤 Autor

**Celso Batista de Oliveira**

Tecnólogo em Análise e Desenvolvimento de Sistemas

- GitHub: [Celsoibiuna](https://github.com/Celsoibiuna)
- LinkedIn: [Celso Batista de Oliveira](https://www.linkedin.com/in/celso-batista-de-oliveira-b513612b6/)
- Portfólio: [por-tfo-lio-atualizado.vercel.app](https://por-tfo-lio-atualizado.vercel.app/)

---

## 📄 Licença

Este projeto foi desenvolvido no contexto acadêmico e de formação profissional.
