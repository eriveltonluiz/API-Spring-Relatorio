package projeto.api.rest.controller;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import projeto.api.rest.ObjetoErro;
import projeto.api.rest.model.Usuario;
import projeto.api.rest.repository.UsuarioRepository;
import projeto.api.rest.service.ServiceEnviaEmail;

@RestController
@RequestMapping(value = "/recuperar")
public class RecuperaController {

	String tipoSolicitacao = "";

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private ServiceEnviaEmail serviceEnviaEmail;

	private ObjetoErro objetoErro = null;

	@PostMapping(value = "/")
	public ResponseEntity<ObjetoErro> recuperar(@RequestBody Usuario login) throws Exception {

		Usuario usuario = usuarioRepository.findUserByLogin(login.getLogin());

		if (usuario == null) {
			objetoErro = new ObjetoErro("404", "Usuário não encontrado");
		} else {
			servicoEmail(usuario);
		}

		return ResponseEntity.ok().body(objetoErro);
	}

	private void servicoEmail(Usuario usuario) throws Exception {
		String novaSenha = String.valueOf(ThreadLocalRandom.current().nextInt(01010101, 99999999));
		String senhaCriptografada = new BCryptPasswordEncoder().encode(novaSenha);

		try {
			
			serviceEnviaEmail.enviarEmail("Recuperação de senha", usuario.getLogin(), "Sua nova senha é: " + novaSenha);
			objetoErro = new ObjetoErro("200", "Acesso enviado para seu e-mail");
			
			if (tipoSolicitacao.equalsIgnoreCase("liberar")) {
				usuario.setSenha(senhaCriptografada);
				usuarioRepository.saveExplicit(usuario.getLogin(), usuario.getSenha());
			} else {
				usuarioRepository.updateSenha(senhaCriptografada, usuario.getId());
			}
		} catch (Exception e) {
			objetoErro = new ObjetoErro("404", "Email inválido");
		}

	}

	@PostMapping(value = "/liberarAcesso")
	public ResponseEntity<ObjetoErro> liberarPrimeiroAcesso(@RequestBody Usuario login) throws Exception {
		tipoSolicitacao = "liberar";

		if (usuarioRepository.findUserByLogin(login.getLogin()) == null) {
			servicoEmail(login);
			if (objetoErro.getError().equalsIgnoreCase("Email inválido")) {
				return ResponseEntity.ok().body(objetoErro);
			}
		} else {
			objetoErro = new ObjetoErro("404",
					"Email já cadastrado no sistema. Para recuperar a senha é necessário ir na opção de Recuperar Acesso");
		}

		return ResponseEntity.ok().body(objetoErro);
	}
}
