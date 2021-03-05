package projeto.api.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import projeto.api.rest.model.Telefone;

@Repository
public interface TelefoneRepository extends JpaRepository<Telefone, Long>{
	
}
