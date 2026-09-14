-- =====================================================================
-- KeroPro — Dados de demonstração
-- Senha de todos os usuários de exemplo: "Senha@123" (hash BCrypt abaixo)
-- =====================================================================
USE keropro;

INSERT INTO categorias (slug, nome) VALUES
 ('eletrica','Elétrica'), ('hidraulica','Hidráulica'),
 ('ti','TI & Redes'), ('clima','Ar-condicionado'), ('reforma','Reformas');

-- hash BCrypt real de "Senha@123" (gerado com BCryptPasswordEncoder, custo 10)
SET @senha_demo = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy';

INSERT INTO usuarios (id, nome, email, senha_hash, tipo, cpf, telefone, data_nascimento, lgpd_consentimento, termos_aceitos_em) VALUES
 (1, 'Mariana Oliveira',       'mariana@keropro.com', @senha_demo, 'CLIENTE',      '11144477735', '15999990001', '1991-04-12', TRUE, NOW()),
 (2, 'Carlos Henrique Santos', 'carlos@keropro.com',  @senha_demo, 'PROFISSIONAL', '52998224725', '15999990002', '1984-09-03', TRUE, NOW()),
 (3, 'Diego Fontoura',         'diego@keropro.com',   @senha_demo, 'PROFISSIONAL', '15350946056', '15999990003', '1990-01-20', TRUE, NOW()),
 (4, 'Renata Alves',           'renata@keropro.com',  @senha_demo, 'PROFISSIONAL', '90731540009', '15999990004', '1988-07-15', TRUE, NOW()),
 (5, 'Marcos Vinícius',        'marcos@keropro.com',  @senha_demo, 'PROFISSIONAL', '48693544066', '15999990005', '1995-11-30', TRUE, NOW());

INSERT INTO enderecos (usuario_id, cep, estado, cidade, bairro, logradouro, numero) VALUES
 (1, '18010000', 'SP', 'Sorocaba', 'Centro',        'Rua São Bento',        '120'),
 (2, '18020000', 'SP', 'Sorocaba', 'Vila Hortência', 'Rua das Palmeiras',    '45'),
 (3, '18030000', 'SP', 'Sorocaba', 'Jardim Vergueiro','Av. Ipanema',         '900'),
 (4, '18040000', 'SP', 'Sorocaba', 'Campolim',       'Rua dos Lírios',       '312'),
 (5, '18050000', 'SP', 'Sorocaba', 'Éden',           'Rua das Acácias',      '78');

INSERT INTO clientes (id) VALUES (1);

INSERT INTO profissionais
 (id, categoria_id, especialidade, anos_experiencia, raio_atendimento_km, bio, instituicao_formacao, curso_formacao, ano_conclusao, certificacoes_adicionais, latitude, longitude, preco_base, status_verificacao, verificado, disponivel) VALUES
 (2, 1, 'Técnico Eletricista',     12, 15, 'Especialista em instalações residenciais e comerciais.',   'SENAI Sorocaba',       'Técnico em Eletrotécnica',      2013, 'NR-10, NR-35',           -23.4980, -47.4550, 60.00, 'APROVADO', TRUE,  TRUE),
 (3, 3, 'Técnico em Redes',         8, 20, 'Foco em redes residenciais e pequenas empresas.',          'FATEC Sorocaba',       'Análise e Desenv. de Sistemas', 2017, 'CCNA',                   -23.4930, -47.4480, 90.00, 'APROVADO', TRUE,  TRUE),
 (4, 2, 'Encanadora',               6, 12, 'Reparos e instalações hidráulicas em geral.',              'SENAI Sorocaba',       'Técnico em Edificações',        2019, NULL,                     -23.5050, -47.4600, 70.00, 'APROVADO', TRUE,  TRUE),
 (5, 4, 'Técnico em Refrigeração',  3, 10, 'Manutenção e instalação de ar-condicionado split.',        'SENAI Sorocaba',       'Técnico em Refrigeração',       2022, NULL,                     -23.4870, -47.4400, 95.00, 'PENDENTE',  FALSE, TRUE);

INSERT INTO score_profissional (profissional_id, formacao, certificacoes, avaliacoes, tempo_resposta, score_total) VALUES
 (2, 95, 90, 94, 88, 92),
 (3, 88, 84, 86, 80, 85),
 (4, 70, 76, 88, 74, 78),
 (5, 60, 68, 78, 72, 70);

INSERT INTO pedidos (cliente_id, profissional_id, categoria_id, descricao, status, distancia_km, valor_estimado, emergencia) VALUES
 (1, 2, 1, 'Reparo em tomada',                'PENDENTE', 1.2, 85.00, TRUE),
 (1, 2, 1, 'Troca de disjuntor',              'PENDENTE', 0.8, 70.00, TRUE),
 (1, 2, 1, 'Instalação de chuveiro elétrico', 'PENDENTE', 3.4, 110.00, FALSE);
