package projeto.api.rest.security;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

import projeto.api.rest.model.Usuario;

//Estabelece o nosso gerenciador de Token
public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter{

	//Configurando o gerenciador de autenticação
	protected JWTLoginFilter(String url, AuthenticationManager authenticationManager) {
		
		//Obriga a autenticar a url
		super(new AntPathRequestMatcher(url));
		
		//gerenciador de autenticação
		setAuthenticationManager(authenticationManager);
	}

	//Retorna o usuário ao processar a autenticação
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		// Está pegando o token para validar
		Usuario user = new ObjectMapper().readValue(request.getInputStream(), Usuario.class);
			return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(user.getLogin(), user.getSenha(), new ArrayList<>()));
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {

		new JWTTokenAutenticacaoService().addAuthentication(response, authResult.getName());
	}

}
