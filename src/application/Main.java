package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {
	Data data[] = new Data[26];
	String rawdata[];
	Connection conn = null;
	PreparedStatement pst = null;
	String sql = null;

	@Override
	public void start(Stage primaryStage) {
		try {
			conn = DB_Connector.getConnection();
			try {
				for (int n = 15; n >= 1; n--) {
				WebScrapper(n);
				filter_data_for_oneday();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				MessageHandler m = new MessageHandler("Error", e.toString(), "OK");
			}
			BorderPane root = FXMLLoader.load(getClass().getResource("MainFXML.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.initStyle(StageStyle.UNDECORATED);
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (SQLException | IOException e) {

			MessageHandler m = new MessageHandler("Error", e.getMessage(), "OK");
		}
	}

	private void move_to_data_array(String rawdata[], int size) {
		int j = 0;
		for (int i = 25; i >= 0; i--) {
			Data temp = new Data(rawdata[j], rawdata[j + 1], rawdata[j + 2], rawdata[j + 3], rawdata[j + 4],
					rawdata[j + 5], rawdata[j + 6]);
			j = j + 7;
			data[i] = temp;
		}
	}

	private String convert_hour_to_column(int i) throws ParseException {
		DateFormat df = new SimpleDateFormat("hh:mm:ss");
		Calendar cal = GregorianCalendar.getInstance();
		java.util.Date date = df.parse(data[i].getHour());
		cal.setTime(date);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		if (hour == 0) {
			if (i + 1 < data.length && data[i + 1].getHour().equals("13:00:00")) {
				hour = hour + 12;
			} else if (i - 1 >= 0 && data[i - 1].getHour().equals("11:00:00")) {
				hour = hour + 12;
			} else
				hour = hour + 24;
		}
		return "Hour" + hour;
	}

	private Date convert_Date(int j) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		java.util.Date date = df.parse(data[j].getDate());
		Date sqldate = new Date(date.getTime());
		return sqldate;
	}

	private String Determine_Data(int j, String TableName) {
		String rData = null;
		switch (TableName) {
		case "[Hourly_Generation_Table]":
			rData = data[j].getGeneration();
			break;
		case "[Hourly_Demand_Table]":
			rData = data[j].getDemand();
			break;
		case "[Hourly_Shortage_Table]":
			rData = data[j].getShortage();
			break;
		case "[Hourly_Loadshed_Table]":
			rData = data[j].getLoadshed();
			break;
		case "[Hourly_Remark_Table]":
			rData = data[j].getRemarks();
			break;

		default:
			break;
		}
		return rData;
	}

	private void insert_initial_data_toDB(int j, String TableName) throws SQLException, ParseException {
		sql = "if not exists( " + "select * from [SCADA_Power_Generation_Report].[dbo]." + TableName
				+ " where Date = ?)" + "begin " + "INSERT INTO [SCADA_Power_Generation_Report].[dbo]." + TableName
				+ " (Date , " + convert_hour_to_column(j) + ") VALUES (?,?)" + "end";

		pst = conn.prepareStatement(sql);
		pst.setDate(1, convert_Date(j));
		pst.setDate(2, convert_Date(j));
		pst.setString(3, Determine_Data(j, TableName));
		pst.executeUpdate();

	}

	private void update_hourly_data(int j, String TableName) throws SQLException, ParseException {
		sql = "UPDATE [SCADA_Power_Generation_Report].[dbo]." + TableName + " SET " + convert_hour_to_column(j) + "= ? "
				+ "WHERE Date = ?";
		pst = conn.prepareStatement(sql);
		pst.setString(1, Determine_Data(j, TableName));
		pst.setDate(2, convert_Date(j));
		pst.executeUpdate();

	}

	private void filter_data_for_oneday() {

		for (int i = 0, j = 1; i < data.length && j < data.length; i++, j++) {
			if (i == 0) {
				try {
					insert_initial_data_toDB(i, "[Hourly_Generation_Table]");
					insert_initial_data_toDB(i, "[Hourly_Demand_Table]");
					insert_initial_data_toDB(i, "[Hourly_Shortage_Table]");
					insert_initial_data_toDB(i, "[Hourly_Loadshed_Table]");
					insert_initial_data_toDB(i, "[Hourly_Remark_Table]");
				} catch (ParseException | SQLException e) {
					MessageHandler m = new MessageHandler("Error", e.toString(), "OK");
				}
			} else if (data[i].getDate().equals(data[j].getDate())) {
				try {
					update_hourly_data(i, "[Hourly_Generation_Table]");
					update_hourly_data(i, "[Hourly_Demand_Table]");
					update_hourly_data(i, "[Hourly_Shortage_Table]");
					update_hourly_data(i, "[Hourly_Loadshed_Table]");
					update_hourly_data(i, "[Hourly_Remark_Table]");
				} catch (SQLException | ParseException e) {
					MessageHandler m = new MessageHandler("Error", e.toString(), "OK");
				}
			} else {
				try {
					update_hourly_data(i, "[Hourly_Generation_Table]");
					update_hourly_data(i, "[Hourly_Demand_Table]");
					update_hourly_data(i, "[Hourly_Shortage_Table]");
					update_hourly_data(i, "[Hourly_Loadshed_Table]");
					update_hourly_data(i, "[Hourly_Remark_Table]");
					insert_initial_data_toDB(j, "[Hourly_Generation_Table]");
					insert_initial_data_toDB(j, "[Hourly_Demand_Table]");
					insert_initial_data_toDB(j, "[Hourly_Shortage_Table]");
					insert_initial_data_toDB(j, "[Hourly_Loadshed_Table]");
					insert_initial_data_toDB(j, "[Hourly_Remark_Table]");
				} catch (ParseException | SQLException e) {
					MessageHandler m = new MessageHandler("Error", e.toString(), "OK");
				}
			}
			if (j == data.length - 1) {
				try {
					update_hourly_data(j, "[Hourly_Generation_Table]");
					update_hourly_data(j, "[Hourly_Demand_Table]");
					update_hourly_data(j, "[Hourly_Shortage_Table]");
					update_hourly_data(j, "[Hourly_Loadshed_Table]");
					update_hourly_data(j, "[Hourly_Remark_Table]");
				} catch (ParseException | SQLException e) {
					// TODO Auto-generated catch block
					MessageHandler m = new MessageHandler("Error", e.toString(), "OK");
				}
			}

		}
	}

	private void WebScrapper(int n) throws IOException {
		Document doc = Jsoup
				.connect("https://www.pgcb.org.bd/PGCB/?a=pages/hourly_generation_loadshed_display.php")
				.validateTLSCertificates(false).get();
		Elements elements = doc.select("div#main_container");
		int i = 0;
		for (Element element : elements) {
			int size = element.getElementsByTag("td").size();
			rawdata = new String[size];
			while (size - 1 > i) {
				rawdata[i] = element.getElementsByTag("td").get(i).text();
				System.err.println(rawdata[i].toString());
				i++;
				
			}
			move_to_data_array(rawdata, size);
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
