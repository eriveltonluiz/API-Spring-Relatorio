package projeto.api.rest.service;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class ServiceEnviaEmail {
	
	public void enviarEmail(String assunto, String emailDestino, String mensagem) throws Exception {
		
		String username = "eriveltonprojetos@gmail.com";
		
		Properties properties = new Properties();
		properties.put("mail.smtp.ssl.trust", "*");   //Autorização
		properties.put("mail.smtp.auth", "true");   //Autorização
		properties.put("mail.smtp.starttls", "true");   //Autenticação
		properties.put("mail.smtp.host", "smtp.gmail.com");   //Servidor Google
		properties.put("mail.smtp.port", "465");    //Porta servidor
		properties.put("mail.smtp.socketFactory.port", "465");    //Expecifica Porta socket
		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");    //Classe de conexão socket
		
		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, "reddragonas135");
			}
		});
		
		Address[] toUser = InternetAddress.parse(emailDestino);
		
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(username));
		message.setRecipients(Message.RecipientType.TO, toUser);    //Para quem vai o email - Quem irá receber o email
		message.setSubject(assunto);
		message.setText(mensagem);
		
		Transport.send(message);
	}
	
	public boolean validarEmail(String email) {
		boolean result = true;
	    try {
	        InternetAddress emailAddr = new InternetAddress(email);
	        emailAddr.validate();
	    } catch (AddressException ex) {
	        result = false;
	    }
	    return result;
	}
}
