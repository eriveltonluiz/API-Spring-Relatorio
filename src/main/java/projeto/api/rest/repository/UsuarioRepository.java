package projeto.api.rest.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import projeto.api.rest.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
	
	@Query("select u from Usuario u where u.login = ?1")
	Usuario findUserByLogin(String login);
	  
	@Query("select u from Usuario u where u.id = ?1")
	Usuario findUserByLogin(Long id);
	
	@Query("select u from Usuario u where u.nome like %?1%")
	List<Usuario> findUserByNome(String login);
	
	@Query(nativeQuery = true, value = "select index_name from information_schema.statistics" + 
			" where table_name = 'usuarios_role'" + 
			" and index_schema = 'api_rest' and column_name = 'role_id' and index_name != 'uq_role_usuario'")
	String consultaConstraintRole();
	
	@Transactional
	@Modifying
	@Query(nativeQuery = true, value= "insert into usuario(login, senha) values(?1, ?2)")
	void saveExplicit(String login, String senha);
	
	@Modifying
	@Query(nativeQuery = true, value = "update usuario set token = ?1 where login = ?2")
	void atualizaTokenUser(String token, String login);

	@Transactional
	@Modifying
	@Query(value = "update usuario set senha = ?1 where id = ?2", nativeQuery = true)
	void updateSenha(String senha, Long codUser);
	
	default Page<Usuario> findUserByNamePage(String nome, PageRequest pageRequest){
		Usuario usuario = new Usuario();
		usuario.setNome(nome);
		
		//Configurando para pesqusar por nome e paginação
		ExampleMatcher exampleMatcher = ExampleMatcher.matchingAny()
				.withMatcher("nome", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
		
		Example<Usuario> exampleList = Example.of(usuario, exampleMatcher);
		
		Page<Usuario> retorno = findAll(exampleList, pageRequest);
		
		return retorno;
	}
}
