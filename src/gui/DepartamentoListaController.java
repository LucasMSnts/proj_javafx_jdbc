package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbException;
import gui.listerners.AlteracaoDadosListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Departamento;
import model.services.DepartamentoService;

public class DepartamentoListaController implements Initializable, AlteracaoDadosListener {

	private DepartamentoService servico;

	@FXML
	private TableView<Departamento> tableViewDepartamento;

	@FXML
	private TableColumn<Departamento, Integer> tableColumnId;

	@FXML
	private TableColumn<Departamento, String> tableColumnNome;

	@FXML
	private TableColumn<Departamento, Departamento> tableColumnEditar;
	
	@FXML
	private TableColumn<Departamento, Departamento> tableColumnRemover;

	@FXML
	private Button btNovo;

	private ObservableList<Departamento> obsLista;

	@FXML
	public void onBtNovoAction(ActionEvent evento) {
		Stage parentStage = Utils.currentStage(evento);
		Departamento obj = new Departamento();
		createDialogForm(obj, "/gui/DepartamentoForm.fxml", parentStage);
	}

	public void setDepartamentoService(DepartamentoService servico) {
		this.servico = servico;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializaNodes();
	}

	private void initializaNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewDepartamento.prefHeightProperty().bind(stage.heightProperty());
	}

	public void updateTableView() {
		if (servico == null) {
			throw new IllegalStateException("Servi�o estava Nulo");
		}
		List<Departamento> lista = servico.findAll();
		obsLista = FXCollections.observableArrayList(lista);
		tableViewDepartamento.setItems(obsLista);
		initEditButtons();
		initRemoveButtons();
	}

	private void createDialogForm(Departamento obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			DepartamentoFormController controller = loader.getController();
			controller.setDepartamento(obj);
			controller.setDepartamentoService(new DepartamentoService());
			controller.subescreverAlteracaoDadosListener(this);
			controller.updateFormData();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Novo Departamento");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();

		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {
		updateTableView();
	}

	private void initEditButtons() {
		tableColumnEditar.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEditar.setCellFactory(param -> new TableCell<Departamento, Departamento>() {
			private final Button button = new Button("Editar");

			@Override
			protected void updateItem(Departamento obj, boolean empty) {
				super.updateItem(obj, empty);

				if (obj == null) {
					setGraphic(null);
					return;
				}

				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/DepartamentoForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnRemover.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnRemover.setCellFactory(param -> new TableCell<Departamento, Departamento>() {
			private final Button button = new Button("Remover");

			@Override
			protected void updateItem(Departamento obj, boolean empty) {
				super.updateItem(obj, empty);
				
				if (obj == null) {
					setGraphic(null);
					return;
				}
				
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}			
		});
	}
	
	private void removeEntity(Departamento obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirma��o", "Tem certeza que quer deletar?");
		
		if (result.get() == ButtonType.OK) {
			if (servico == null) {
				throw new IllegalStateException("Servi�o estava Nulo");
			}
			try {
				servico.remove(obj);
				updateTableView();
			}
			catch (DbException e) {
				Alerts.showAlert("Erro removendo objeto", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
}
