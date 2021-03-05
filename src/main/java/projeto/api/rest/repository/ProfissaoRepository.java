package projeto.api.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import projeto.api.rest.model.Profissao;

@Repository
public interface ProfissaoRepository extends JpaRepository<Profissao, Long>{
	
}
