-- =====================================================================
-- KeroPro — Esquema de banco de dados (MySQL 8+)
-- =====================================================================
CREATE DATABASE IF NOT EXISTS keropro CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE keropro;

-- ---------------------------------------------------------------------
-- USUARIOS: base comum de autenticação para clientes e profissionais.
-- Campos de segurança/LGPD ficam aqui por serem comuns aos dois perfis.
-- ---------------------------------------------------------------------
CREATE TABLE usuarios (
  id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
  nome                  VARCHAR(120)  NOT NULL,
  email                 VARCHAR(160)  NOT NULL UNIQUE,
  senha_hash            VARCHAR(255)  NOT NULL,          -- BCrypt (nunca texto puro)
  tipo                  ENUM('CLIENTE', 'PROFISSIONAL') NOT NULL,
  cpf                   VARCHAR(11),                       -- somente dígitos, validado no cadastro
  cnpj                  VARCHAR(14),                       -- preenchido só quando o profissional opta por MEI
  telefone              VARCHAR(11)   NOT NULL,
  data_nascimento       DATE          NOT NULL,
  lgpd_consentimento    BOOLEAN       NOT NULL DEFAULT FALSE,
  termos_aceitos_em     DATETIME      NULL,
  aceita_marketing      BOOLEAN       NOT NULL DEFAULT FALSE,
  ultimo_login_em       DATETIME      NULL,
  tentativas_login_falhas TINYINT     NOT NULL DEFAULT 0,   -- suporte a bloqueio por força bruta
  criado_em             DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_usuarios_cpf  (cpf),
  UNIQUE KEY uk_usuarios_cnpj (cnpj)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- ENDEREÇOS: reaproveitado por clientes e profissionais (1-para-1)
-- ---------------------------------------------------------------------
CREATE TABLE enderecos (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  usuario_id    BIGINT       NOT NULL UNIQUE,
  cep           VARCHAR(8)   NOT NULL,
  estado        VARCHAR(2)   NOT NULL,
  cidade        VARCHAR(80)  NOT NULL DEFAULT 'Sorocaba',
  bairro        VARCHAR(80)  NOT NULL,
  logradouro    VARCHAR(160) NOT NULL,
  numero        VARCHAR(10)  NOT NULL,
  complemento   VARCHAR(80),
  CONSTRAINT fk_enderecos_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- CLIENTES
-- ---------------------------------------------------------------------
CREATE TABLE clientes (
  id            BIGINT PRIMARY KEY,
  CONSTRAINT fk_clientes_usuario FOREIGN KEY (id) REFERENCES usuarios(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- CATEGORIAS DE SERVIÇO
-- ---------------------------------------------------------------------
CREATE TABLE categorias (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  slug          VARCHAR(40)   NOT NULL UNIQUE,
  nome          VARCHAR(60)   NOT NULL
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- PROFISSIONAIS
-- ---------------------------------------------------------------------
CREATE TABLE profissionais (
  id                       BIGINT PRIMARY KEY,
  categoria_id             BIGINT        NOT NULL,
  especialidade            VARCHAR(120)  NOT NULL,
  anos_experiencia         TINYINT       NOT NULL DEFAULT 0,
  raio_atendimento_km      SMALLINT      NOT NULL DEFAULT 10,
  bio                      VARCHAR(600),
  instituicao_formacao     VARCHAR(160)  NOT NULL,
  curso_formacao           VARCHAR(160)  NOT NULL,
  ano_conclusao            SMALLINT      NOT NULL,
  certificacoes_adicionais VARCHAR(400),
  latitude                 DECIMAL(9,6)  NOT NULL,
  longitude                DECIMAL(9,6)  NOT NULL,
  preco_base               DECIMAL(8,2)  NOT NULL DEFAULT 0,
  status_verificacao       ENUM('PENDENTE','APROVADO','REJEITADO') NOT NULL DEFAULT 'PENDENTE',
  verificado               BOOLEAN       NOT NULL DEFAULT FALSE,
  disponivel               BOOLEAN       NOT NULL DEFAULT TRUE,
  criado_em                DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_profissionais_usuario   FOREIGN KEY (id)           REFERENCES usuarios(id)   ON DELETE CASCADE,
  CONSTRAINT fk_profissionais_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- COMPROVANTES enviados no cadastro do profissional (diploma, certificados)
-- Arquivo físico fica em storage externo (S3/local); aqui guardamos a referência.
-- ---------------------------------------------------------------------
CREATE TABLE comprovantes (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  profissional_id  BIGINT       NOT NULL,
  nome_arquivo      VARCHAR(200) NOT NULL,
  caminho_storage   VARCHAR(400) NOT NULL,
  enviado_em        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_comprovantes_profissional FOREIGN KEY (profissional_id) REFERENCES profissionais(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- SCORE DE EXCELÊNCIA (componentes ponderados do algoritmo)
-- ---------------------------------------------------------------------
CREATE TABLE score_profissional (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  profissional_id  BIGINT   NOT NULL UNIQUE,
  formacao         TINYINT  NOT NULL DEFAULT 0,  -- 0-100: formação acadêmica
  certificacoes    TINYINT  NOT NULL DEFAULT 0,  -- 0-100: certificações técnicas
  avaliacoes       TINYINT  NOT NULL DEFAULT 0,  -- 0-100: histórico de avaliações
  tempo_resposta   TINYINT  NOT NULL DEFAULT 0,  -- 0-100: agilidade de resposta
  score_total      TINYINT  NOT NULL DEFAULT 0,  -- calculado pelo ScoreService
  atualizado_em    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_score_profissional FOREIGN KEY (profissional_id) REFERENCES profissionais(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- PEDIDOS (solicitações de serviço)
-- ---------------------------------------------------------------------
CREATE TABLE pedidos (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  cliente_id       BIGINT        NOT NULL,
  profissional_id  BIGINT        NOT NULL,
  categoria_id     BIGINT        NOT NULL,
  descricao        VARCHAR(255)  NOT NULL,
  status           ENUM('PENDENTE','ACEITO','A_CAMINHO','EM_EXECUCAO','CONCLUIDO') NOT NULL DEFAULT 'PENDENTE',
  distancia_km     DECIMAL(5,2)  NOT NULL,
  valor_estimado   DECIMAL(8,2)  NOT NULL,
  emergencia       BOOLEAN       NOT NULL DEFAULT FALSE,
  criado_em        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  atualizado_em    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_pedidos_cliente      FOREIGN KEY (cliente_id)      REFERENCES clientes(id)      ON DELETE CASCADE,
  CONSTRAINT fk_pedidos_profissional FOREIGN KEY (profissional_id) REFERENCES profissionais(id) ON DELETE CASCADE,
  CONSTRAINT fk_pedidos_categoria    FOREIGN KEY (categoria_id)    REFERENCES categorias(id)    ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- AVALIAÇÕES (feedback pós-serviço, alimenta o Score de Excelência)
-- ---------------------------------------------------------------------
CREATE TABLE avaliacoes (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  pedido_id        BIGINT   NOT NULL UNIQUE,
  nota             TINYINT  NOT NULL CHECK (nota BETWEEN 1 AND 5),
  comentario       VARCHAR(500),
  criado_em        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_avaliacoes_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_profissionais_categoria ON profissionais(categoria_id);
CREATE INDEX idx_profissionais_status    ON profissionais(status_verificacao);
CREATE INDEX idx_pedidos_status          ON pedidos(status);
CREATE INDEX idx_pedidos_profissional    ON pedidos(profissional_id);
