package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDrawer;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

public class FXMLController implements Initializable {
	private int minmax = 0;
	private Connection conn = null;
	private ResultSet rs = null;
	private PreparedStatement pst = null;
	private String sql = null;
	private ObservableList<Data[]> data = FXCollections.observableArrayList();
	private int count;
	private Data[][] table_data;
	private HBox box;
	public static boolean pdf_gen_controller = false;
	private TableColumn generation;
	private TableColumn demand;
	private TableColumn shortage;
	private TableColumn loadshed;
	private TableColumn remarks;

	@FXML
	private Button minified_btn;

	@FXML
	private Button minmax_btn;

	@FXML
	private ImageView minmax_img;

	@FXML
	private Button closebtn;

	@FXML
	private TableView<Data[]> table_1;

	@FXML
	private JFXDrawer filter_drawer;

	@FXML
	private Button filter_btn;

	@FXML
	public void control_btn_act(ActionEvent event) {
		if (event.getSource() == closebtn) {
			Platform.exit();
		}
		if (event.getSource() == minmax_btn) {
			Stage stage = (Stage) minmax_btn.getScene().getWindow();
			Image icon;
			if (minmax == 0) {
				stage.setMaximized(true);
				icon = new Image(getClass().getResourceAsStream("icons/copy-content.png"));
				minmax_img.setImage(icon);
				minmax = 1;
			} else {
				stage.setMaximized(false);
				icon = new Image(getClass().getResourceAsStream("icons/maximize.png"));
				minmax_img.setImage(icon);
				minmax = 0;
			}
		}
		if (event.getSource() == minified_btn) {
			Stage stage = (Stage) minified_btn.getScene().getWindow();
			stage.setIconified(true);

		}
		if (event.getSource() == filter_btn) {

			filter_drawer.setSidePane(box);

			if (filter_drawer.isShown()) {
				filter_drawer.close();

			} else {

				filter_drawer.open();

			}

		}
	}

	@FXML
	public void control_pdf_action(ActionEvent event) {
		if (!pdf_gen_controller) {
			try {
				pdf_gen_controller = true;
				FXMLLoader loader = new FXMLLoader(getClass().getResource("PDFGenerator.fxml"));
				BorderPane pane = loader.load();
				Stage stage = new Stage(StageStyle.UNDECORATED);
				Scene scene = new Scene(pane);
				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				stage.setScene(scene);
				stage.show();
				PDFGeneratorController controller = loader.getController();
				controller.set_table(table_1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				MessageHandler m = new MessageHandler("Error", e.toString(), "OK");
			}
		}
	}

	private TableColumn initialize_Column_generation(TableColumn column, int col_no, boolean value) {
		column.setEditable(true);
		column.setSortable(false);
		column.setCellValueFactory(new Callback<CellDataFeatures<Data[], String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<Data[], String> param) {
				return new SimpleStringProperty(param.getValue()[col_no].getGeneration());
			}

		});
		column.setCellFactory(TextFieldTableCell.forTableColumn());
		column.setVisible(value);
		return column;
	}

	private TableColumn initialize_Column_demand(TableColumn column, int col_no, boolean value) {
		column.setEditable(true);
		column.setSortable(false);
		column.setCellValueFactory(new Callback<CellDataFeatures<Data[], String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<Data[], String> param) {
				return new SimpleStringProperty(param.getValue()[col_no].getDemand());
			}

		});
		column.setCellFactory(TextFieldTableCell.forTableColumn());
		column.setVisible(value);
		return column;
	}

	private TableColumn initialize_Column_Shortage(TableColumn column, int col_no, boolean value) {
		column.setEditable(true);
		column.setSortable(false);
		column.setCellValueFactory(new Callback<CellDataFeatures<Data[], String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<Data[], String> param) {
				return new SimpleStringProperty(param.getValue()[col_no].getShortage());
			}

		});
		column.setCellFactory(TextFieldTableCell.forTableColumn());
		column.setVisible(value);
		return column;
	}

	private TableColumn initialize_Column_loadshed(TableColumn column, int col_no, boolean value) {
		column.setEditable(true);
		column.setSortable(false);
		column.setCellValueFactory(new Callback<CellDataFeatures<Data[], String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<Data[], String> param) {
				return new SimpleStringProperty(param.getValue()[col_no].getLoadshed());
			}

		});
		column.setCellFactory(TextFieldTableCell.forTableColumn());
		column.setVisible(value);
		return column;
	}

	private TableColumn initialize_Column_remarks(TableColumn column, int col_no, boolean value) {
		column.setEditable(true);
		column.setSortable(false);
		column.setCellValueFactory(new Callback<CellDataFeatures<Data[], String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<Data[], String> param) {
				return new SimpleStringProperty(param.getValue()[col_no].getRemarks());
			}

		});
		column.setCellFactory(TextFieldTableCell.forTableColumn());
		column.setVisible(value);
		return column;
	}

	private TableColumn initialize_Column_date(TableColumn column, int col_no, boolean value) {
		column.setEditable(false);
		column.setSortable(true);
		column.setCellValueFactory(new Callback<CellDataFeatures<Data[], String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<Data[], String> param) {
				return new SimpleStringProperty(param.getValue()[col_no].getDate());
			}

		});
		column.setVisible(value);
		return column;
	}

	private TableColumn initialize_Column_id(TableColumn column, int col_no, boolean value) {
		column.setEditable(false);
		column.setSortable(true);
		column.setCellFactory(TextFieldTableCell.forTableColumn());
		column.setCellValueFactory(new Callback<CellDataFeatures<Data[], String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<Data[], String> param) {
				return new SimpleStringProperty(param.getValue()[col_no].getID());
			}

		});

		column.setVisible(value);
		return column;
	}

	private void get_from_DB_to_Object(int i, int ID) throws SQLException {
		int hours = i + 1;
		sql = "SELECT A.ID, A.Date,A.Hour" + hours + " Generation ,B.Hour" + hours + " Demand,C.Hour" + hours
				+ " Shortage,D.Hour" + hours + " Loadshed,E.Hour" + hours + " Remark "
				+ "FROM [Hourly_Generation_Table] A,[Hourly_Demand_Table] B,"
				+ "[Hourly_Loadshed_Table] C, [Hourly_Shortage_Table] D, " + "[Hourly_Remark_Table] E where A.ID = "
				+ ID + " and A.ID = B.ID and B.ID = C.ID and C.ID= D.ID and D.ID=E.ID";
		pst = conn.prepareStatement(sql);
		rs = pst.executeQuery();
		while (rs.next()) {
			Data d = new Data(rs.getString(1), rs.getString(2), "Hours" + hours, rs.getString(3), rs.getString(4),
					rs.getString(5), rs.getString(6), rs.getString(7));

			table_data[ID - 1][i] = d;

		}

	}

	private void getCount() throws SQLException {
		sql = "select COUNT(*) from Hourly_Generation_Table A, Hourly_Demand_Table B,"
				+ "Hourly_Shortage_Table C, Hourly_Loadshed_Table D,"
				+ "Hourly_Remark_Table E where  A.ID = B.ID and B.ID = C.ID and C.ID= D.ID and D.ID=E.ID";
		pst = conn.prepareStatement(sql);
		rs = pst.executeQuery();
		while (rs.next()) {
			count = rs.getInt(1);
		}
	}

	private void handle_hours_combo(int i, JFXCheckBox[] combo) {
		combo[i + 2].setOnAction(e -> {
			if (combo[i + 2].isSelected()) {
				table_1.getColumns().get(i + 2).setVisible(true);
			} else
				table_1.getColumns().get(i + 2).setVisible(false);
		});
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		TableColumn id = new TableColumn("ID");
		id = initialize_Column_id(id, 0, true);
		TableColumn date = new TableColumn("Date");
		date = initialize_Column_date(date, 0, true);
		table_1.getColumns().addAll(id, date);
		TableColumn hours = null;
		for (int i = 0; i < 24; i++) {
			hours = new TableColumn(i + 1 + ":00");
			generation = new TableColumn("Generation");
			demand = new TableColumn("Demand");
			shortage = new TableColumn("Shortage");
			loadshed = new TableColumn("Loadshed");
			remarks = new TableColumn("Remarks");
			generation = initialize_Column_generation(generation, i, true);
			demand = initialize_Column_demand(demand, i, true);
			shortage = initialize_Column_Shortage(shortage, i, true);
			loadshed = initialize_Column_loadshed(loadshed, i, true);
			remarks = initialize_Column_remarks(remarks, i, true);
			hours.getColumns().addAll(generation, demand, shortage, loadshed, remarks);
			table_1.getColumns().add(hours);
		}

		try {
			conn = DB_Connector.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			MessageHandler m = new MessageHandler("Error", e.toString(), "OK");
		}

		int ID = 1;
		try {
			getCount();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		table_data = new Data[count][24];
		while (ID <= count) {

			for (int i = 0; i < 24; i++) {
				try {
					get_from_DB_to_Object(i, ID);

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					MessageHandler m = new MessageHandler("Error", e.toString(), "OK");
				}
			}

			ID++;
		}
		data.addAll(Arrays.asList(table_data));
		table_1.setItems(data);
		TableColumn tc[] = new TableColumn[26];
		table_1.getColumns().toArray(tc);
		box = new HBox();
		box.setAlignment(Pos.CENTER);
		JFXCheckBox filter_options_gen = new JFXCheckBox("Generation");
		filter_options_gen.setSelected(true);
		JFXCheckBox filter_options_dem = new JFXCheckBox("Demand");
		filter_options_dem.setSelected(true);
		JFXCheckBox filter_options_sho = new JFXCheckBox("Shortage");
		filter_options_sho.setSelected(true);
		JFXCheckBox filter_options_loa = new JFXCheckBox("Loadshed");
		filter_options_loa.setSelected(true);
		JFXCheckBox filter_options_rem = new JFXCheckBox("Remarks");
		filter_options_rem.setSelected(true);
		TextField search_field = new TextField();
		search_field.setPromptText("Search using ID or Date");
		search_field.getStyleClass().add("search");
		HBox search_box = new HBox();
		search_box.setPrefWidth(334);
		search_box.setAlignment(Pos.CENTER_RIGHT);
		search_box.getChildren().add(search_field);
		ObservableList<JFXCheckBox> filter_hour = FXCollections.observableArrayList();
		JFXComboBox<JFXCheckBox> hour_combo = new JFXComboBox<JFXCheckBox>();
		hour_combo.setPromptText("Show Hour Colums");
		JFXCheckBox hours_check[] = new JFXCheckBox[24];
		for (int i = 0; i < 24; i++) {
			hours_check[i] = new JFXCheckBox("Hours " + (i + 1));
			hours_check[i].setSelected(true);
			hours_check[i].setVisible(false);
		}
		hour_combo.setItems(filter_hour);
		box.getChildren().addAll(new Label("Filter Columns:"), filter_options_gen, filter_options_dem,
				filter_options_sho, filter_options_loa, filter_options_rem);
		box.setMargin(search_box, new Insets(0, 0, 0, 20));
		filter_options_gen.setOnAction(e -> {
			if (!filter_options_gen.isSelected()) {
				for (int h = 2; h < 26; h++) {
					TableColumn col = (TableColumn) tc[h].getColumns().get(0);
					col.setVisible(false);
				}
			} else {
				for (int h = 2; h < 26; h++) {
					TableColumn col = (TableColumn) tc[h].getColumns().get(0);
					col.setVisible(true);
				}
			}
		});
		filter_options_dem.setOnAction(e -> {
			if (!filter_options_dem.isSelected()) {
				for (int h = 2; h < 26; h++) {
					TableColumn col = (TableColumn) tc[h].getColumns().get(1);
					col.setVisible(false);
				}
			} else {
				for (int h = 2; h < 26; h++) {
					TableColumn col = (TableColumn) tc[h].getColumns().get(1);
					col.setVisible(true);
				}
			}
		});
		filter_options_sho.setOnAction(e -> {
			if (!filter_options_sho.isSelected()) {
				for (int h = 2; h < 26; h++) {
					TableColumn col = (TableColumn) tc[h].getColumns().get(2);
					col.setVisible(false);
				}
			} else {
				for (int h = 2; h < 26; h++) {
					TableColumn col = (TableColumn) tc[h].getColumns().get(2);
					col.setVisible(true);
				}
			}
		});
		filter_options_loa.setOnAction(e -> {
			if (!filter_options_loa.isSelected()) {
				for (int h = 2; h < 26; h++) {
					TableColumn col = (TableColumn) tc[h].getColumns().get(3);
					col.setVisible(false);
				}
			} else {
				for (int h = 2; h < 26; h++) {
					TableColumn col = (TableColumn) tc[h].getColumns().get(3);
					col.setVisible(true);
				}
			}
		});
		filter_options_rem.setOnAction(e -> {
			if (!filter_options_rem.isSelected()) {
				for (int h = 2; h < 26; h++) {
					TableColumn col = (TableColumn) tc[h].getColumns().get(4);
					col.setVisible(false);
				}
			} else {
				for (int h = 2; h < 26; h++) {
					TableColumn col = (TableColumn) tc[h].getColumns().get(4);
					col.setVisible(true);
				}
			}
		});
	}

}
