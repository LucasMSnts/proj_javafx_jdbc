package gui;

import java.net.URL;
import java.sql.Date;
import java.time.Instant;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Departamento;
import model.entities.Vendedor;
import model.exceptions.ValidacaoException;
import model.services.DepartamentoService;
import model.services.VendedorService;

public class VendedorFormController implements Initializable {

	private Vendedor entity;

	private VendedorService servico;

	private DepartamentoService depServico;

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
	private ComboBox<Departamento> comboBoxDepartamento;

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

	private ObservableList<Departamento> obsLista;

	public void setVendedor(Vendedor entity) {
		this.entity = entity;
	}

	public void setServices(VendedorService servico, DepartamentoService depServico) {
		this.servico = servico;
		this.depServico = depServico;
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
		} catch (ValidacaoException e) {
			setErroMensagens(e.getErros());
		} catch (DbException e) {
			Alerts.showAlert("Erro salvando objeto", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notificacaoAlteracaoDadosListeners() {
		for (AlteracaoDadosListener listener : alteracaoDadosListeners) {
			listener.onDataChanged();
		}
	}

	private Vendedor getFormData() {
		Vendedor obj = new Vendedor();

		ValidacaoException excecao = new ValidacaoException("Erro de Validação");
		//Nome
		obj.setId(Utils.tryParseToInt(txtId.getText()));

		//Nome
		if (txtNome.getText() == null || txtNome.getText().trim().equals("")) {
			excecao.addErro("Nome", "Campo não pode estar vazio");
		}
		obj.setNome(txtNome.getText());

		//Email
		if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {
			excecao.addErro("Email", "Campo não pode estar vazio");
		}
		obj.setEmail(txtEmail.getText());
		
		//Data Nascimento
		if (dpDataNasc.getValue() == null) {
			excecao.addErro("DataNasc", "Campo não pode estar vazio");
		} else {
			Instant instant = Instant.from(dpDataNasc.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setDataNasc(Date.from(instant));
		}
		
		//Base Salarial
		if (txtBaseSalarial.getText() == null || txtBaseSalarial.getText().trim().equals("")) {
			excecao.addErro("BaseSalarial", "Campo não pode estar vazio");
		}
		obj.setBaseSalario(Utils.tryParseToDouble(txtBaseSalarial.getText()));
		
		//Departamento
		obj.setDepartamento(comboBoxDepartamento.getValue());
		
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
		
		initializeComboBoxDepartment();
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
			// dpDataNasc.setValue(LocalDate.ofInstant(entity.getDataNasc().toInstant(),
			// ZoneId.systemDefault()));
			dpDataNasc.setValue(
					LocalDateTime.ofInstant(entity.getDataNasc().toInstant(), ZoneId.systemDefault()).toLocalDate());
		}
		if (entity.getDepartamento() == null) {
			comboBoxDepartamento.getSelectionModel().selectFirst();
		} else {
			comboBoxDepartamento.setValue(entity.getDepartamento());
		}
	}

	public void carregaAssociacaoObjects() {
		if (depServico == null) {
			throw new IllegalStateException("DepartamentoService está nulo");
		}
		List<Departamento> lista = depServico.findAll();
		obsLista = FXCollections.observableArrayList(lista);
		comboBoxDepartamento.setItems(obsLista);
	}

	private void setErroMensagens(Map<String, String> erros) {
		Set<String> campos = erros.keySet();

		labelErroNome.setText((campos.contains("Nome") ? erros.get("Nome") : ""));
		
		labelErroEmail.setText((campos.contains("Email") ? erros.get("Email") : ""));
		
		labelErroDataNasc.setText((campos.contains("DataNasc") ? erros.get("DataNasc") : ""));
		
		labelErroBaseSalarial.setText((campos.contains("BaseSalarial") ? erros.get("BaseSalarial") : ""));
					
	}

	private void initializeComboBoxDepartment() {
		Callback<ListView<Departamento>, ListCell<Departamento>> factory = lv -> new ListCell<Departamento>() {
			@Override
			protected void updateItem(Departamento item, boolean vazio) {
				super.updateItem(item, vazio);
				setText(vazio ? "" : item.getNome());
			}
		};
		comboBoxDepartamento.setCellFactory(factory);
		comboBoxDepartamento.setButtonCell(factory.call(null));
	}
}
