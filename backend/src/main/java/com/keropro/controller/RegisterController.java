package com.keropro.controller;

import com.keropro.dto.*;
import com.keropro.model.*;
import com.keropro.repository.*;
import com.keropro.service.ScoreService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Optional;

/**
 * Endpoints de cadastro (cliente e profissional). Concentra as regras de
 * segurança: senha nunca chega ao banco em texto puro (BCrypt), e-mail/CPF
 * são únicos, idade mínima de 18 anos é validada no servidor (não apenas no
 * front-end) e o consentimento LGPD é obrigatório e registrado com timestamp.
 */
@RestController
@RequestMapping("/api/auth/cadastro")
public class RegisterController {

    private static final int IDADE_MINIMA = 18;

    private final UsuarioRepository usuarioRepository;
    private final EnderecoRepository enderecoRepository;
    private final ClienteRepository clienteRepository;
    private final ProfissionalRepository profissionalRepository;
    private final CategoriaRepository categoriaRepository;
    private final PasswordEncoder passwordEncoder;
    private final ScoreService scoreService;

    public RegisterController(UsuarioRepository usuarioRepository, EnderecoRepository enderecoRepository,
                               ClienteRepository clienteRepository, ProfissionalRepository profissionalRepository,
                               CategoriaRepository categoriaRepository, PasswordEncoder passwordEncoder,
                               ScoreService scoreService) {
        this.usuarioRepository = usuarioRepository;
        this.enderecoRepository = enderecoRepository;
        this.clienteRepository = clienteRepository;
        this.profissionalRepository = profissionalRepository;
        this.categoriaRepository = categoriaRepository;
        this.passwordEncoder = passwordEncoder;
        this.scoreService = scoreService;
    }

    @PostMapping("/cliente")
    @Transactional
    public ResponseEntity<?> cadastrarCliente(@Valid @RequestBody CadastroClienteDTO body) {
        Optional<ResponseEntity<?>> erroComum = validarDadosComuns(body.getEmail(), body.getCpf(), body.getDataNascimento());
        if (erroComum.isPresent()) return erroComum.get();

        Usuario usuario = new Usuario();
        usuario.setNome(body.getNome());
        usuario.setEmail(body.getEmail().toLowerCase().trim());
        usuario.setSenhaHash(passwordEncoder.encode(body.getSenha())); // <-- nunca texto puro
        usuario.setTipo(TipoUsuario.CLIENTE);
        usuario.setCpf(body.getCpf());
        usuario.setTelefone(body.getTelefone());
        usuario.setDataNascimento(LocalDate.parse(body.getDataNascimento()));
        usuario.setLgpdConsentimento(body.isAceitouLgpd());
        usuario.setTermosAceitosEm(LocalDateTime.now());
        usuario.setAceitaMarketing(body.isAceitouMarketing());
        usuario = usuarioRepository.save(usuario);

        salvarEndereco(usuario, body.getEndereco());

        Cliente cliente = new Cliente();
        cliente.setUsuario(usuario);
        clienteRepository.save(cliente);

        return ResponseEntity.status(HttpStatus.CREATED).body(new CadastroResponseDTO(
                usuario.getId(), usuario.getNome(), "CLIENTE", "ATIVO",
                "Conta criada com sucesso."));
    }

    @PostMapping("/profissional")
    @Transactional
    public ResponseEntity<?> cadastrarProfissional(@Valid @RequestBody CadastroProfissionalDTO body) {
        Optional<ResponseEntity<?>> erroComum = validarDadosComuns(body.getEmail(), null, body.getDataNascimento());
        if (erroComum.isPresent()) return erroComum.get();

        String documentoLimpo = body.getDocumento().replaceAll("\\D", "");
        boolean isCnpj = "CNPJ".equalsIgnoreCase(body.getTipoDocumento());
        if (isCnpj ? documentoLimpo.length() != 14 : documentoLimpo.length() != 11) {
            return erro(HttpStatus.BAD_REQUEST, "Documento inválido para o tipo selecionado.");
        }

        Categoria categoria = categoriaRepository.findAll().stream()
                .filter(c -> c.getSlug().equalsIgnoreCase(body.getCategoria()))
                .findFirst()
                .orElse(null);
        if (categoria == null) return erro(HttpStatus.BAD_REQUEST, "Categoria inválida.");

        Usuario usuario = new Usuario();
        usuario.setNome(body.getNome());
        usuario.setEmail(body.getEmail().toLowerCase().trim());
        usuario.setSenhaHash(passwordEncoder.encode(body.getSenha()));
        usuario.setTipo(TipoUsuario.PROFISSIONAL);
        if (isCnpj) usuario.setCnpj(documentoLimpo); else usuario.setCpf(documentoLimpo);
        usuario.setTelefone(body.getTelefone());
        usuario.setDataNascimento(LocalDate.parse(body.getDataNascimento()));
        usuario.setLgpdConsentimento(body.isAceitouLgpd());
        usuario.setTermosAceitosEm(LocalDateTime.now());
        usuario.setAceitaMarketing(body.isAceitouMarketing());
        usuario = usuarioRepository.save(usuario);

        salvarEndereco(usuario, body.getEndereco());

        Profissional profissional = new Profissional();
        profissional.setUsuario(usuario);
        profissional.setCategoria(categoria);
        profissional.setEspecialidade(body.getEspecialidade());
        profissional.setAnosExperiencia(body.getAnosExperiencia());
        profissional.setRaioAtendimentoKm(body.getRaioAtendimentoKm());
        profissional.setBio(body.getBio());
        profissional.setInstituicaoFormacao(body.getFormacao().getInstituicao());
        profissional.setCursoFormacao(body.getFormacao().getCurso());
        profissional.setAnoConclusao(body.getFormacao().getAnoConclusao());
        profissional.setCertificacoesAdicionais(body.getFormacao().getCertificacoesAdicionais());
        // Coordenadas provisórias (centro de Sorocaba) até a geocodificação do endereço ser implementada.
        profissional.setLatitude(new java.math.BigDecimal("-23.5015"));
        profissional.setLongitude(new java.math.BigDecimal("-47.4526"));
        profissional.setPrecoBase(new java.math.BigDecimal("60.00"));
        profissional.setStatusVerificacao(StatusVerificacao.PENDENTE); // aguarda validação manual da equipe
        profissional.setVerificado(false);
        profissional.setDisponivel(false); // só fica visível a clientes após aprovação
        profissional = profissionalRepository.save(profissional);

        ScoreProfissional score = montarScoreInicial(profissional, body);
        profissional.setScore(score);
        profissionalRepository.save(profissional);

        return ResponseEntity.status(HttpStatus.CREATED).body(new CadastroResponseDTO(
                usuario.getId(), usuario.getNome(), "PROFISSIONAL", "PENDENTE_ANALISE",
                "Cadastro recebido. Nossa equipe vai validar sua formação em até 48h."));
    }

    /* ---------------------------------------------------------------
       Validações compartilhadas
    ---------------------------------------------------------------- */
    private Optional<ResponseEntity<?>> validarDadosComuns(String email, String cpf, String dataNascimentoIso) {
        if (usuarioRepository.findByEmail(email.toLowerCase().trim()).isPresent()) {
            return Optional.of(erro(HttpStatus.CONFLICT, "Este e-mail já está cadastrado."));
        }
        try {
            LocalDate nascimento = LocalDate.parse(dataNascimentoIso);
            if (Period.between(nascimento, LocalDate.now()).getYears() < IDADE_MINIMA) {
                return Optional.of(erro(HttpStatus.BAD_REQUEST, "É necessário ter ao menos 18 anos para se cadastrar."));
            }
        } catch (Exception e) {
            return Optional.of(erro(HttpStatus.BAD_REQUEST, "Data de nascimento inválida."));
        }
        return Optional.empty();
    }

    private void salvarEndereco(Usuario usuario, EnderecoDTO dto) {
        Endereco endereco = new Endereco();
        endereco.setUsuario(usuario);
        endereco.setCep(dto.getCep());
        endereco.setEstado(dto.getEstado());
        endereco.setCidade(dto.getCidade());
        endereco.setBairro(dto.getBairro());
        endereco.setLogradouro(dto.getLogradouro());
        endereco.setNumero(dto.getNumero());
        endereco.setComplemento(dto.getComplemento());
        enderecoRepository.save(endereco);
    }

    /**
     * Score inicial de um profissional recém-cadastrado: parte de uma base neutra
     * em avaliações (ainda sem histórico) e usa formação/certificações informadas
     * como sinal inicial, até a primeira rodada de avaliações reais de clientes.
     */
    private ScoreProfissional montarScoreInicial(Profissional profissional, CadastroProfissionalDTO body) {
        ScoreProfissional score = new ScoreProfissional();
        score.setProfissional(profissional);
        score.setFormacao(60);
        score.setCertificacoes(
                body.getFormacao().getCertificacoesAdicionais() != null
                        && !body.getFormacao().getCertificacoesAdicionais().isBlank() ? 55 : 30);
        score.setAvaliacoes(50); // neutro — ainda sem avaliações de clientes
        score.setTempoResposta(70);
        score.setScoreTotal(scoreService.calcularScoreTotal(score));
        return score;
    }

    private ResponseEntity<?> erro(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(new ErroDTO(mensagem));
    }
}
