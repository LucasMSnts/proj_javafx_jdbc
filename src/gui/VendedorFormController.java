package gui;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listerners.AlteracaoDadosListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Vendedor;
import model.exceptions.ValidacaoException;
import model.services.VendedorService;

public class VendedorFormController implements Initializable{

	private Vendedor entity;
	
	private VendedorService servico;
	
	private List<AlteracaoDadosListener> alteracaoDadosListeners = new ArrayList<>();
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtNome;
	
	@FXML
	private TextField txtEmail;
	
	@FXML
	private DatePicker dpDataNasc;
	
	@FXML
	private TextField txtBaseSalarial;
	
	@FXML
	private Label labelErroNome;
	
	@FXML
	private Label labelErroEmail;
	
	@FXML
	private Label labelErroDataNasc;
	
	@FXML
	private Label labelErroBaseSalarial;
	
	@FXML
	private Button btSalvar;
	
	@FXML
	private Button btCancelar;
	
	public void setVendedor(Vendedor entity) {
		this.entity = entity;
	}
	
	public void setVendedorService(VendedorService servico) {
		this.servico = servico;
	}
	
	public void subescreverAlteracaoDadosListener(AlteracaoDadosListener listener) {
		alteracaoDadosListeners.add(listener);
	}
	
	@FXML
	public void onBtSalvarAction(ActionEvent evento) {
		if (entity == null) {
			throw new IllegalStateException("Entity esta nula");
		}
		if (servico == null) {
			throw new IllegalStateException("Servico esta nula");
		}
		try {
			entity = getFormData();
			servico.saveOrUpdate(entity);
			notificacaoAlteracaoDadosListeners();
			Utils.currentStage(evento).close();
		} 
		catch (ValidacaoException e) {
			setErroMensagens(e.getErros());
		}
		catch (DbException e) {
			Alerts.showAlert("Erro salvando objeto", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	private void notificacaoAlteracaoDadosListeners() {
		for (AlteracaoDadosListener listener : alteracaoDadosListeners)	{
			listener.onDataChanged();
		}
	}

	private Vendedor getFormData() {
		Vendedor obj = new Vendedor();
		
		ValidacaoException excecao = new ValidacaoException("Erro de Valida��o");
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		
		if (txtNome.getText() == null || txtNome.getText().trim().equals("")) {
			excecao.addErro("Nome", "Campo n�o pode estar vazio");
		}
		obj.setNome(txtNome.getText());
		
		if (excecao.getErros().size() > 0) {
			throw excecao;
		}
		
		return obj;
	}

	@FXML
	public void onBtCancelarAction(ActionEvent evento) {
		Utils.currentStage(evento).close();
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtNome, 70);
		Constraints.setTextFieldDouble(txtBaseSalarial);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Utils.formatDatePicker(dpDataNasc, "dd/MM/yyyy");
	}
	
	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity esta nula");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtNome.setText(entity.getNome());
		txtEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		txtBaseSalarial.setText(String.format("%.2f", entity.getBaseSalario()));
		if (entity.getDataNasc() != null) {
			// JAVA SE 9
			//dpDataNasc.setValue(LocalDate.ofInstant(entity.getDataNasc().toInstant(), ZoneId.systemDefault()));
			dpDataNasc.setValue(LocalDateTime.ofInstant(entity.getDataNasc().toInstant(), ZoneId.systemDefault()).toLocalDate());
		}
	}
	
	private void setErroMensagens(Map<String, String> erros) {
		Set<String> campos = erros.keySet();
		
		if (campos.contains("Nome")) {
			labelErroNome.setText(erros.get("Nome"));
		}
	}
}

