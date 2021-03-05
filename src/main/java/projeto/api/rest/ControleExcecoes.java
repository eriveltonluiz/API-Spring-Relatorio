package projeto.api.rest;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@ControllerAdvice
public class ControleExcecoes extends ResponseEntityExceptionHandler {

	// Interceptar erro mais comuns do projeto
	@ExceptionHandler({ Exception.class, RuntimeException.class, Throwable.class })
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {

		String msg = "";

		if (ex instanceof MethodArgumentNotValidException) {
			List<ObjectError> list = ((MethodArgumentNotValidException) ex).getBindingResult().getAllErrors();

			for (ObjectError objectError : list) {
				msg += objectError.getDefaultMessage() + "\n";
			}

		} else {
			msg = ex.getMessage();
		}

		ObjetoErro objetoErro = new ObjetoErro(msg, status.value() + " ==> " + status.getReasonPhrase());

		return new ResponseEntity<>(objetoErro, headers, status);
	}

	// Tratamento da maioria dos erros a nível de banco de dados
	@ExceptionHandler({ DataIntegrityViolationException.class, ConstraintViolationException.class, SQLException.class })
	protected ResponseEntity<Object> handleExceptionDataIntegry(Exception ex) {
		String msg = "";

		if (ex instanceof DataIntegrityViolationException) {
			msg = ((DataIntegrityViolationException) ex).getCause().getMessage();
		} else if (ex instanceof ConstraintViolationException) {
			msg = ((ConstraintViolationException) ex).getCause().getMessage();
		} else if (ex instanceof SQLException) {
			msg = ((SQLException) ex).getCause().getMessage();
		} else {
			msg = ex.getMessage();
		}

		ObjetoErro objetoErro = new ObjetoErro(msg,
				HttpStatus.INTERNAL_SERVER_ERROR + " ==> " + HttpStatus.INTERNAL_SERVER_ERROR);
		return new ResponseEntity<>(objetoErro, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
