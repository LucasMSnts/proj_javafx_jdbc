package model.exceptions;

import java.util.HashMap;
import java.util.Map;

public class ValidacaoException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private Map<String, String> erros = new HashMap<>();
	
	public ValidacaoException(String msg) {
		super(msg);
	}	
	
	public Map<String, String> getErros() {
		return erros;
	}
	
	public void addErro(String nomeCampo, String mensagemErro) {
		erros.put(nomeCampo, mensagemErro);
	}
}
