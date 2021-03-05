package projeto.api.rest.controller;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import projeto.api.rest.model.Telefone;
import projeto.api.rest.model.Usuario;
import projeto.api.rest.model.UsuarioChart;
import projeto.api.rest.model.UsuarioReport;
import projeto.api.rest.model.dto.TelefoneDTO;
import projeto.api.rest.model.dto.UsuarioDTO;
import projeto.api.rest.repository.TelefoneRepository;
import projeto.api.rest.repository.UsuarioRepository;
import projeto.api.rest.service.ServiceRelatorio;

//@CrossOrigin
@RestController
@RequestMapping(value = "/usuario")
public class IndexController {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private TelefoneRepository telefoneRepository;

	@Autowired
	private ServiceRelatorio serviceRelatorio;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	// @Autowired
	// private ImplementacaoUserDetailsService implementacaoUserDetailsService;

	@GetMapping(value = "/", produces = "application/json")
	@CachePut("cacheusuarios") // Irá atualizar o cache após recarregar a lista
	public ResponseEntity<Page<Usuario>> init() throws InterruptedException {
		PageRequest page = PageRequest.of(0, 5, Sort.by("nome"));
		Page<Usuario> usuariosPage = usuarioRepository.findAll(page);

		return ResponseEntity.ok().body(usuariosPage);
	}

	@GetMapping(value = "/page/{pagina}", produces = "application/json")
	@CachePut("cacheusuarios") // Irá atualizar o cache após recarregar a lista
	public ResponseEntity<Page<Usuario>> usuarioPorPagina(@PathVariable("pagina") Integer pagina)
			throws InterruptedException {
		
		PageRequest page = PageRequest.of(pagina, 5, Sort.by("nome"));
		Page<Usuario> usuariosPage = usuarioRepository.findAll(page);

		return ResponseEntity.ok().body(usuariosPage);
	}

	@GetMapping(value = "/usuarioPorNome/{nome}", produces = "application/json")
	@CachePut("cacheusuarios") // Irá atualizar o cache após recarregar a lista
	public ResponseEntity<Page<Usuario>> usuarioPorNome(@PathVariable("nome") String nome) throws InterruptedException {

		PageRequest pageRequest = PageRequest.of(0, 5, Sort.by("nome"));
		;
		Page<Usuario> usuariosPage = null;

		if (nome == null || (nome != null && nome.trim().isEmpty()) || nome.equalsIgnoreCase("undefined")) { 
			usuariosPage = usuarioRepository.findAll(pageRequest);
		} else {
			usuariosPage = usuarioRepository.findUserByNamePage(nome, pageRequest);
		}

		// List<Usuario> usuarios = usuarioRepository.findUserByNome(nome);
		// Thread.sleep(3000); // Segura o código por 6 segundos simulando um processo lento

		return ResponseEntity.ok().body(usuariosPage);
	}

	@GetMapping(value = "/usuarioPorNome/{nome}/page/{page}", produces = "application/json")
	@CachePut("cacheusuarios") // Irá atualizar o cache após recarregar a lista
	public ResponseEntity<Page<Usuario>> usuarioPorNome(@PathVariable("nome") String nome,
			@PathVariable("page") int page) throws InterruptedException {

		PageRequest pageRequest = PageRequest.of(page, 5, Sort.by("nome"));
		Page<Usuario> usuariosPage = null;

		//trim tira o espaço da palavra
		if (nome == null || (nome != null && nome.trim().isEmpty()) || nome.equalsIgnoreCase("undefined")) { 
			usuariosPage = usuarioRepository.findAll(pageRequest);
		} else {
			usuariosPage = usuarioRepository.findUserByNamePage(nome, pageRequest);
		}

		return ResponseEntity.ok().body(usuariosPage);
	}

	@GetMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<UsuarioDTO> relatorio(@PathVariable(value = "id") Long id) {
		Usuario usuario = usuarioRepository.findById(id).get();
		return new ResponseEntity<UsuarioDTO>(new UsuarioDTO(usuario), HttpStatus.OK);
	}

	@GetMapping(value = "/buscar/{id}", produces = "application/json")
	public ResponseEntity<Usuario> relatorio2(@PathVariable(value = "id") Long id) {
		Usuario usuario = usuarioRepository.findById(id).get();
		return new ResponseEntity<Usuario>(usuario, HttpStatus.OK);
	}

	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> cadastrar(@Valid @RequestBody Usuario usuario) {
		usuario.setSenha(new BCryptPasswordEncoder().encode(usuario.getSenha()));
		Usuario userSalvo = usuarioRepository.save(usuario);

		return ResponseEntity.ok().body(userSalvo);
	}

	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> atualizar(@Valid @RequestBody Usuario usuario) {
		Usuario userTemp = usuarioRepository.findUserByLogin(usuario.getId());

		if (!userTemp.getSenha().equals(usuario.getSenha())) {
			usuario.setSenha(new BCryptPasswordEncoder().encode(usuario.getSenha()));
		}

		for (Telefone u : usuario.getTelefones()) {
			int index = usuario.getTelefones().indexOf(u);
			u.setUsuario(usuario);
			usuario.getTelefones().set(index, u);
		}

		usuario = usuarioRepository.save(usuario);
		return ResponseEntity.ok().body(usuario);
	}

	@DeleteMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<Usuario> deletar(@PathVariable(value = "id") Long id) {
		usuarioRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}
	
	@PostMapping(value = "/addFone", produces = "application/json")
	public ResponseEntity<Telefone> addFone(@RequestBody TelefoneDTO telefoneDTO){
		Usuario usuario = usuarioRepository.findById(telefoneDTO.getUsuario_id()).get();
		
		Telefone telefone = new Telefone(telefoneDTO.getNumero(), usuario);
		telefone = telefoneRepository.save(telefone);
		telefone.setUsuario(new Usuario());
		return ResponseEntity.ok().body(telefone);
	}

	@DeleteMapping(value = "/removerTelefone/{id}", produces = "application/text")
	public String deleteTelefone(@PathVariable("id") Long id) {
		telefoneRepository.deleteById(id);
		return "ok";
	}

	@GetMapping(value = "/relatorio", produces = "application/text")
	public ResponseEntity<String> downloadRelatorio(HttpServletRequest request) throws Exception {
		byte[] pdf = serviceRelatorio.gerarRelatorio("relatorio-usuario", new HashMap<>(), request.getServletContext());

		String base64Pdf = "data:application/pdf;base64," + Base64.encodeBase64String(pdf);

		return ResponseEntity.ok().body(base64Pdf);
	}

	@PostMapping(value = "/relatorio", produces = "application/text")
	public ResponseEntity<String> downloadRelatorioParm(@RequestBody UsuarioReport usuarioReport,
			HttpServletRequest request) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdfParam = new SimpleDateFormat("yyyy-MM-dd");
		String dataInicio = sdfParam.format(sdf.parse(usuarioReport.getDataInicio()));
		String dataFim = sdfParam.format(sdf.parse(usuarioReport.getDataFim()));
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("DATA_INICIO", dataInicio);
		params.put("DATA_FIM", dataFim);
		
		byte[] pdf = serviceRelatorio.gerarRelatorio("relatorio-usuario-param", params, request.getServletContext());
		String base64Pdf = "data:application/pdf;base64," + Base64.encodeBase64String(pdf);

		return ResponseEntity.ok().body(base64Pdf);
	}
	
	@GetMapping(value = "/grafico", produces = "application/json")
	public ResponseEntity<UsuarioChart> grafico(){
		
		UsuarioChart usuarioChart = new UsuarioChart();
		
		List<String> resultado = jdbcTemplate.queryForList("select array_agg(nome) from usuario where salario > 0" + 
				" union all" + 
				" select cast(array_agg(salario) as character varying[]) from usuario where salario > 0", String.class);
		
		if(!resultado.isEmpty()) {
			String nomes = resultado.get(0).replaceAll("\\{", "").replaceAll("\\}", "");
			String salarios = resultado.get(1).replaceAll("\\{", "").replaceAll("\\}", "");
			
			usuarioChart.setNome(nomes);
			usuarioChart.setSalario(salarios);
		}
		
		return ResponseEntity.ok().body(usuarioChart);
	}
}
