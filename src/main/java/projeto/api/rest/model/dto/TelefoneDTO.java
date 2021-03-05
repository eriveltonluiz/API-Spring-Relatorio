package projeto.api.rest.model.dto;

public class TelefoneDTO {
	
	private String numero;
	private Long usuario_id;
	
	public TelefoneDTO() {
	}
	
	public TelefoneDTO(String numero, Long usuario_id) {
		super();
		this.numero = numero;
		this.usuario_id = usuario_id;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public Long getUsuario_id() {
		return usuario_id;
	}
	public void setUsuario_id(Long usuario_id) {
		this.usuario_id = usuario_id;
	}
	
	
}
