package projeto.api.rest.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import projeto.api.rest.ApplicationContextLoad;
import projeto.api.rest.model.Usuario;
import projeto.api.rest.repository.UsuarioRepository;

@Service
@Component
public class JWTTokenAutenticacaoService {

	// Tempo de validade do token 2 dias
	private static final long EXPIRATION_TIME = 172800000;

	// Uma senha única para compor a autenticação e ajudar na segurança
	private static final String SECRET = "SenhaUltraSecreta";

	// Prefixo padrão de Token
	private static final String TOKEN_PREFIX = "Bearer";

	private static final String HEADER_STRING = "Authorization";

	// Gerando token de autenticação e adicionando ao cabeçalho e resposta http
	public void addAuthentication(HttpServletResponse response, String username) throws IOException {

		// Montagem do Token
		String JWT = Jwts.builder() // Chama o gerador de Token
				.setSubject(username) // Adiciona o usuário
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Tempo de expiração
				.signWith(SignatureAlgorithm.HS512, SECRET).compact(); // Compactação e algoritmos de geração de senha

		// Junta token com o pefixo
		String token = TOKEN_PREFIX + " " + JWT; // Bearer nfis754wyru93iofnrit

		// Adiciona no cabeçalho http
		response.addHeader(HEADER_STRING, token); // Authorization: Bearer nfis754wyru93iofnrit

		/*
		ApplicationContextLoad.getApplicationContext().getBean(UsuarioRepository.class)
		.atualizaTokenUser(JWT, username);
		*/
		
		// Liberando resposta para porta diferente do projeto Angular
		liberacaoCors(response);
		
		// Escreve token como resposta no corpo http
		response.getWriter().write("{\"Authorization\": \"" + token + "\"}");
	}

	// retorna o usuário validado com token ou caso não sejá valido retorna null
	public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) {

		// Pega o token enviado no cabeçalho http
		String token = request.getHeader(HEADER_STRING);

		try {
			if (token != null) {
				// Faz a validação do token do usuário na requisição
				String user = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
						.getBody().getSubject();

				if (user != null) {
					Usuario usuario = ApplicationContextLoad.getApplicationContext().getBean(UsuarioRepository.class)
							.findUserByLogin(user);

					if (usuario != null) {
						return new UsernamePasswordAuthenticationToken(usuario.getLogin(), usuario.getSenha(),
								usuario.getAuthorities());
					}
				}
			}
		} catch (ExpiredJwtException e) {
			try {
				response.getOutputStream()
						.println("Seu TOKEN está expirado, faça o login ou informe um novo token para autenticação");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		liberacaoCors(response);
		return null; // não autorizado
	}
	
	private void liberacaoCors(HttpServletResponse response) {
		if(response.getHeader("Access-Control-Allow-Origin") == null) {
			response.addHeader("Access-Control-Allow-Origin", "*");
		}
		
		if(response.getHeader("Access-Control-Allow-Headers") == null) {
			response.addHeader("Access-Control-Allow-Headers", "*");
		}
		
		if(response.getHeader("Access-Control-Request-Headers") == null) {
			response.addHeader("Access-Control-Request-Headers", "*");
		}
		
		if(response.getHeader("Access-Control-Allow-Methods") == null) {
			response.addHeader("Access-Control-Allow-Methods", "*");
		}
	}
}
