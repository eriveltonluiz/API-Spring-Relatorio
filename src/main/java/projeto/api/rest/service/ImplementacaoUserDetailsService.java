package projeto.api.rest.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import projeto.api.rest.model.Usuario;
import projeto.api.rest.repository.UsuarioRepository;

@Service
public class ImplementacaoUserDetailsService implements UserDetailsService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario = usuarioRepository.findUserByLogin(username);
		// usuario.setSenha(new BCryptPasswordEncoder().encode(usuario.getSenha()));

		if (usuario == null) {
			throw new UsernameNotFoundException("Usuário não foi encontrado");
		}
		return new User(usuario.getLogin(), usuario.getPassword(), new ArrayList<>());
	}

	/*
	 * public void insereAcessoPadrao(Long id) { String constraint =
	 * usuarioRepository.consultaConstraintRole(); }
	 */

}
