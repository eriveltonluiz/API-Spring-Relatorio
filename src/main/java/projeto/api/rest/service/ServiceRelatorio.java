package projeto.api.rest.service;

import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@Service
public class ServiceRelatorio implements Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public byte[] gerarRelatorio(String nomeRelatorio, Map<String, Object> params, ServletContext servletContext)
			throws Exception {

		// Obter a conexão com o banco de dados
		Connection con = jdbcTemplate.getDataSource().getConnection();

		JasperPrint print = null;

		// Carregar o caminho do arquivo Jasper
		InputStream jasperStream = this.getClass().getResourceAsStream("/relatorios/" + nomeRelatorio + ".jrxml");
		JasperReport jasperReport = JasperCompileManager.compileReport(jasperStream);

		// Gerar o relatorio com os dados e conexão
		print = JasperFillManager.fillReport(jasperReport, params, con);

		// Exporta para byte o Pdf para fazer o download
		byte[] retorno = JasperExportManager.exportReportToPdf(print);

		con.close();
		return retorno;
	}
}
