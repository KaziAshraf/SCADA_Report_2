package application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;

import com.itextpdf.awt.geom.Rectangle;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class PDFGeneratorController implements Initializable {
	private ObservableList<String> filter_dym = FXCollections.observableArrayList();
	private File file = null;
	private static TableView<Data[]> table;
	private TableColumn[] table_columns;
	private String reportType = null;
	private PdfPTable mainTable;
	private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	private Document report;
	private PdfWriter writer;
	@FXML
	private JFXDatePicker from_Date;

	@FXML
	private JFXDatePicker to_Date;

	@FXML
	private JFXTextField from_Name;

	@FXML
	private JFXTextField to_Name;

	@FXML
	private JFXTextField from_Designation;

	@FXML
	private JFXTextField to_Designation;

	@FXML
	private JFXTextField from_Division;

	@FXML
	private JFXTextField to_Division;

	@FXML
	private JFXToggleButton toggle_filter;

	@FXML
	private JFXComboBox<String> filter_Combo_Box;

	@FXML
	private TextField file_path;

	@FXML
	private Button generate_btn;

	@FXML
	private Button cancel_btn;

	@FXML
	private Button browse_btn;

	public static void set_table(TableView<Data[]> t) {
		table = t;
	}

	private void construct_pdf_table_for_one_Month(int Month, int column_no, Document doc)
			throws DocumentException, ParseException, MalformedURLException, IOException {
		if (column_no < table.getColumns().size()) {
			add_title_date();
			Calendar cal = GregorianCalendar.getInstance();
			DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date;
			int n = table.getColumns().size();
			table_columns = new TableColumn[n];
			table.getColumns().toArray(table_columns);
			int com_column_count = 0;
			int total_column_count = 0;
			for (int i = 0; i < 5; i++) {
				TableColumn t = (TableColumn) table_columns[2].getColumns().get(i);
				if (t.isVisible()) {
					com_column_count++;
				} else
					continue;
			}
			switch (com_column_count) {
			case 0:
				mainTable = new PdfPTable(2);
				total_column_count = 2;
				break;
			case 1:
				mainTable = new PdfPTable(14);
				mainTable.setTotalWidth(new float[] { 25, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60 });
				mainTable.setLockedWidth(true);
				total_column_count = 14;
				break;
			case 2:
				mainTable = new PdfPTable(8);
				mainTable.setTotalWidth(new float[] { 25, 60, 120, 120, 120, 120, 120, 120 });
				mainTable.setLockedWidth(true);
				total_column_count = 8;
				break;
			case 3:
				mainTable = new PdfPTable(6);
				mainTable.setTotalWidth(new float[] { 25, 60, 160, 160, 160, 160 });
				mainTable.setLockedWidth(true);
				total_column_count = 6;
				break;
			case 4:
				mainTable = new PdfPTable(5);
				mainTable.setTotalWidth(new float[] { 25, 60, 220, 220, 220 });
				mainTable.setLockedWidth(true);
				total_column_count = 5;
				break;
			case 5:
				mainTable = new PdfPTable(4);
				mainTable.setTotalWidth(new float[] { 25, 60, 300, 300 });
				mainTable.setLockedWidth(true);
				total_column_count = 4;
				break;

			default:
				break;
			}
			PdfPCell cell = new PdfPCell(
					new Paragraph(table_columns[0].getText(), FontFactory.getFont(FontFactory.HELVETICA, 10)));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			mainTable.addCell(cell);
			cell = new PdfPCell(
					new Paragraph(table_columns[1].getText(), FontFactory.getFont(FontFactory.HELVETICA, 10)));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			mainTable.addCell(cell);
			TableColumn[] composite_columns;
			PdfPTable com_table;
			TableColumn[] composite_rows;
			int hour_col = column_no;
			for (int i = 2; i < total_column_count; i++) {
				composite_columns = new TableColumn[5];
				table_columns[hour_col].getColumns().toArray(composite_columns);
				com_table = new PdfPTable(com_column_count);
				PdfPCell cell1 = new PdfPCell(new Paragraph(table_columns[hour_col].getText(),
						FontFactory.getFont(FontFactory.HELVETICA, 10)));
				cell1.setColspan(com_column_count);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setVerticalAlignment(Element.ALIGN_CENTER);
				com_table.addCell(cell1);
				for (int r = 0; r < 5; r++) {
					if (composite_columns[r].isVisible()) {
						PdfPCell cell_content = new PdfPCell(new Paragraph(composite_columns[r].getText(),
								FontFactory.getFont(FontFactory.HELVETICA, 9)));
						cell_content.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell_content.setVerticalAlignment(Element.ALIGN_CENTER);
						com_table.addCell(cell_content);
					}
				}
				mainTable.addCell(com_table);
				hour_col++;
			}
			for (int i = 0; i < table.getItems().size(); i++) {
				int year = Calendar.getInstance().get(Calendar.YEAR);
				date = sdf.parse(table_columns[1].getCellObservableValue(i).getValue().toString());
				cal.setTime(date);
				if ((cal.get(Calendar.MONTH) + 1) == Month && cal.get(Calendar.YEAR) == year) {
					int col_no = column_no;
					for (int c = 0; c < total_column_count; c++) {
						cell = new PdfPCell();
						if (c == 0 || c == 1) {
							cell.addElement(
									new Paragraph(table_columns[c].getCellObservableValue(i).getValue().toString(),
											FontFactory.getFont(FontFactory.HELVETICA, 9)));
							mainTable.addCell(cell);
						} else {
							composite_rows = new TableColumn[5];
							table_columns[col_no].getColumns().toArray(composite_rows);
							col_no++;
							com_table = new PdfPTable(com_column_count);
							for (int r = 0; r < 5; r++) {
								if (composite_rows[r].isVisible()) {
									if (composite_rows[r].getCellObservableValue(i).getValue() == null) {
										com_table.addCell(
												new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 10)));

									} else {
										PdfPCell rows = new PdfPCell(new Paragraph(
												composite_rows[r].getCellObservableValue(i).getValue().toString(),
												FontFactory.getFont(FontFactory.HELVETICA, 10)));
										rows.setHorizontalAlignment(Element.ALIGN_CENTER);
										rows.setVerticalAlignment(Element.ALIGN_CENTER);
										com_table.addCell(rows);
									}

								}
							}
							mainTable.addCell(com_table);
						}
					}
				}
			}
			mainTable.setSpacingBefore(20);
			doc.add(mainTable);
			if (column_no + total_column_count - 2 <= 24) {
				doc.newPage();
			}
			construct_pdf_table_for_one_Month(Month, column_no + total_column_count - 2, doc);
		}

	}

	private void add_title_date() throws MalformedURLException, IOException, DocumentException {
		Date currentDate = new Date();
		Image image = null;
		image = Image.getInstance(
				"C:\\Users\\User\\workspace\\SCADA_dpdc_Web_Scrapper\\src\\application\\icons\\DPDC_Logo.png");
		image.setAlignment(Element.ALIGN_CENTER);

		Paragraph title = new Paragraph("DHAKA POWER DISTRIBUTION COMPANY",
				FontFactory.getFont(FontFactory.HELVETICA, 18, Font.BOLD));
		Paragraph subtitle = new Paragraph("System Control & SCADA Circle",
				FontFactory.getFont(FontFactory.HELVETICA, 14));
		Paragraph date = new Paragraph("Date: " + sdf.format(currentDate).toString());
		Paragraph reportName = new Paragraph();
		if (toggle_filter.isSelected()) {
			reportName.add("Report of Hourly Generation and Loadshed from " + from_Date.getValue().toString() + " to "
					+ to_Date.getValue().toString());
		} else {
			if (filter_Combo_Box.getSelectionModel().getSelectedItem().equals("One Day")) {
				reportName.add("Report of Hourly Generation and Loadshed for " + from_Date.getValue().toString());
			} else
				reportName.add("Report of Hourly Generation and Loadshed for " + filter_Combo_Box.getValue() + ", "
						+ String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
		}
		date.setAlignment(Element.ALIGN_RIGHT);
		// Paragraph reportname = new Paragraph("Hourly ")
		title.setAlignment(Element.ALIGN_CENTER);
		subtitle.setAlignment(Element.ALIGN_CENTER);
		reportName.setAlignment(Element.ALIGN_CENTER);
		report.add(image);
		report.add(title);
		report.add(subtitle);
		report.add(reportName);
		report.add(date);
	}

	private PdfPTable add_signature() throws DocumentException {
		JFXTextField text_fileds[] = { from_Name, to_Name, from_Designation, to_Designation, from_Division,
				to_Division };
		PdfPTable sign = new PdfPTable(2);
		sign.setTotalWidth(new float[] { 400, 400 });
		sign.setLockedWidth(true);
		sign.setSpacingBefore(100);
		for (int n = 0; n < text_fileds.length; n++) {
			PdfPCell cell = new PdfPCell(new Paragraph(text_fileds[n].getText()));
			cell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			sign.addCell(cell);
		}
		return sign;
	}

	private void construct_void_for_one_day(String date)
			throws ParseException, MalformedURLException, IOException, DocumentException {
		Date d = sdf.parse(date);
		table_columns = new TableColumn[table.getColumns().size()];
		table.getColumns().toArray(table_columns);
		int n;
		for (n = 0; n < table.getItems().size(); n++) {
			Date od = sdf.parse(table_columns[1].getCellObservableValue(n).getValue().toString());
			System.out.println(n);
			if (d.equals(od)) {
				break;
			} else
				continue;
		}
		add_title_date();
		String Data_for_one_day[][] = new String[24][7];
		TableColumn[] composite_column = new TableColumn[5];
		if (n != table.getItems().size()) {
			for (int r = 0; r < 24; r++) {
				table_columns[r + 2].getColumns().toArray(composite_column);
				Data_for_one_day[r][0] = table_columns[1].getCellObservableValue(n).getValue().toString();
				Data_for_one_day[r][1] = table_columns[r + 2].getText();
				for (int c = 0; c < 5; c++) {
					if (composite_column[c].getCellObservableValue(n).getValue() == null) {
						Data_for_one_day[r][c + 2] = "";
					} else
						Data_for_one_day[r][c + 2] = composite_column[c].getCellObservableValue(n).getValue()
								.toString();
				}
			}
			mainTable = new PdfPTable(7);
			mainTable.setSpacingBefore(10);
			mainTable.addCell(new Paragraph("Date"));
			mainTable.addCell(new Paragraph("Time"));
			mainTable.addCell(new Paragraph("Generation"));
			mainTable.addCell(new Paragraph("Demand"));
			mainTable.addCell(new Paragraph("Shortage"));
			mainTable.addCell(new Paragraph("Loadshed"));
			mainTable.addCell(new Paragraph("Remark"));
			for (int r = 0; r < 24; r++) {
				for (int c = 0; c < 7; c++) {
					mainTable.addCell(new Paragraph(Data_for_one_day[r][c]));
				}
			}
			report.add(mainTable);
		}

	}

	private void construct_pdf_using_date(String fromDate, String toDate, int column_no, Document doc)
			throws MalformedURLException, IOException, DocumentException, ParseException {
		if (column_no < table.getColumns().size()) {
			int n;
			add_title_date();
			table_columns = new TableColumn[26];
			table.getColumns().toArray(table_columns);
			int r;
			Date f_date = sdf.parse(fromDate);
			Date t_date = sdf.parse(toDate);
			for (n = 0; !sdf.parse(table_columns[1].getCellObservableValue(n).getValue().toString())
					.equals(f_date); n++)
				;
			for (r = n; !sdf.parse(table_columns[1].getCellObservableValue(r).getValue().toString())
					.equals(t_date); r++)
				;
			int no_of_data = r - n + 1;
			Data[][] d = new Data[no_of_data][24];
			for (int i = 0; i < no_of_data; i++) {
				for (int j = 0; j < 24; j++) {
					String ID = table_columns[0].getCellObservableValue(n).getValue().toString();
					String Date = table_columns[1].getCellObservableValue(n).getValue().toString();
					TableColumn[] cc = new TableColumn[5];
					table_columns[j + 2].getColumns().toArray(cc);
					String Generation = (String) cc[0].getCellObservableValue(n).getValue();
					String Demand = (String) cc[1].getCellObservableValue(n).getValue();
					String Shortage = (String) cc[2].getCellObservableValue(n).getValue();
					String Loadshed = (String) cc[3].getCellObservableValue(n).getValue();
					String Remark = (String) cc[4].getCellObservableValue(n).getValue();
					Data data = new Data(ID, Date, String.valueOf(j + 1), Generation, Demand, Shortage, Loadshed,
							Remark);
					d[i][j] = data;
				}
				n++;
			}
			int com_column_count = 0;
			int total_column_count = 0;
			for (int i = 0; i < 5; i++) {
				TableColumn t = (TableColumn) table_columns[2].getColumns().get(i);
				if (t.isVisible()) {
					com_column_count++;
				} else
					continue;
			}
			switch (com_column_count) {
			case 0:
				mainTable = new PdfPTable(2);
				total_column_count = 2;
				break;
			case 1:
				mainTable = new PdfPTable(14);
				mainTable.setTotalWidth(new float[] { 25, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60 });
				mainTable.setLockedWidth(true);
				total_column_count = 14;
				break;
			case 2:
				mainTable = new PdfPTable(8);
				mainTable.setTotalWidth(new float[] { 25, 60, 120, 120, 120, 120, 120, 120 });
				mainTable.setLockedWidth(true);
				total_column_count = 8;
				break;
			case 3:
				mainTable = new PdfPTable(6);
				mainTable.setTotalWidth(new float[] { 25, 60, 160, 160, 160, 160 });
				mainTable.setLockedWidth(true);
				total_column_count = 6;
				break;
			case 4:
				mainTable = new PdfPTable(5);
				mainTable.setTotalWidth(new float[] { 25, 60, 220, 220, 220 });
				mainTable.setLockedWidth(true);
				total_column_count = 5;
				break;
			case 5:
				mainTable = new PdfPTable(4);
				mainTable.setTotalWidth(new float[] { 25, 60, 300, 300 });
				mainTable.setLockedWidth(true);
				total_column_count = 4;
				break;

			default:
				break;
			}
			PdfPCell cell = new PdfPCell(
					new Paragraph(table_columns[0].getText(), FontFactory.getFont(FontFactory.HELVETICA, 10)));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			mainTable.addCell(cell);
			cell = new PdfPCell(
					new Paragraph(table_columns[1].getText(), FontFactory.getFont(FontFactory.HELVETICA, 10)));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			mainTable.addCell(cell);
			TableColumn[] composite_columns;
			PdfPTable com_table;
			TableColumn[] composite_rows;
			int hour_col = column_no;
			for (int i = 2; i < total_column_count; i++) {
				composite_columns = new TableColumn[5];
				table_columns[hour_col].getColumns().toArray(composite_columns);
				com_table = new PdfPTable(com_column_count);
				PdfPCell cell1 = new PdfPCell(new Paragraph(table_columns[hour_col].getText(),
						FontFactory.getFont(FontFactory.HELVETICA, 10)));
				cell1.setColspan(com_column_count);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setVerticalAlignment(Element.ALIGN_CENTER);
				com_table.addCell(cell1);
				for (int q = 0; q < 5; q++) {
					if (composite_columns[q].isVisible()) {
						PdfPCell cell_content = new PdfPCell(new Paragraph(composite_columns[q].getText(),
								FontFactory.getFont(FontFactory.HELVETICA, 9)));
						cell_content.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell_content.setVerticalAlignment(Element.ALIGN_CENTER);
						com_table.addCell(cell_content);
					}
				}
				mainTable.addCell(com_table);
				hour_col++;
			}
			composite_columns = new TableColumn[5];

			for (int i = 0; i < no_of_data; i++) {
				mainTable.addCell(new Paragraph(d[i][0].getID(), FontFactory.getFont(FontFactory.HELVETICA, 8)));
				mainTable.addCell(new Paragraph(d[i][0].getDate(), FontFactory.getFont(FontFactory.HELVETICA, 8)));
				int col_no = column_no;
				for (int j = 2; j < total_column_count; j++) {
					com_table = new PdfPTable(com_column_count);
					table_columns[col_no].getColumns().toArray(composite_columns);
					if (composite_columns[0].isVisible()) {
						cell = new PdfPCell(new Paragraph(d[i][col_no - 2].getGeneration(),
								FontFactory.getFont(FontFactory.HELVETICA, 9)));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						com_table.addCell(cell);
					}
					if (composite_columns[1].isVisible()) {
						cell = new PdfPCell(new Paragraph(d[i][col_no - 2].getDemand(),
								FontFactory.getFont(FontFactory.HELVETICA, 9)));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						com_table.addCell(cell);
					}
					if (composite_columns[2].isVisible()) {
						cell = new PdfPCell(new Paragraph(d[i][col_no - 2].getShortage(),
								FontFactory.getFont(FontFactory.HELVETICA, 9)));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						com_table.addCell(cell);
					}
					if (composite_columns[3].isVisible()) {
						cell = new PdfPCell(new Paragraph(d[i][col_no - 2].getLoadshed(),
								FontFactory.getFont(FontFactory.HELVETICA, 9)));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						com_table.addCell(cell);
					}
					if (composite_columns[4].isVisible()) {
						cell = new PdfPCell(new Paragraph(d[i][col_no - 2].getRemarks(),
								FontFactory.getFont(FontFactory.HELVETICA, 9)));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						com_table.addCell(cell);
					}
					col_no++;
					mainTable.addCell(com_table);
				}
			}
			mainTable.setSpacingBefore(20);
			doc.add(mainTable);
			if (column_no + total_column_count - 2 <= 24) {
				doc.newPage();
			}
			construct_pdf_using_date(fromDate, toDate, total_column_count + column_no - 2, doc);
		}

	}

	private void construct_pdf() throws MalformedURLException, IOException, DocumentException, ParseException {
		report = new Document(PageSize.A4.rotate());
		writer = PdfWriter.getInstance(report, new FileOutputStream(file_path.getText()));
		writer.setPageEvent(new Footer());
		report.open();
		if (!toggle_filter.isSelected()) {
			switch (filter_Combo_Box.getValue()) {
			case "One Day":
				construct_void_for_one_day(from_Date.getValue().toString());
				mainTable.setSpacingAfter(50);
				report.add(add_signature());
				break;
			case "January":
				construct_pdf_table_for_one_Month(1, 2, report);
				mainTable.setSpacingAfter(50);
				report.add(add_signature());
				break;
			case "February":
				construct_pdf_table_for_one_Month(2, 2, report);
				mainTable.setSpacingAfter(50);
				report.add(add_signature());
				break;
			case "March":
				construct_pdf_table_for_one_Month(3, 2, report);
				mainTable.setSpacingAfter(50);
				report.add(add_signature());
				break;
			case "April":
				construct_pdf_table_for_one_Month(4, 2, report);
				mainTable.setSpacingAfter(50);
				report.add(add_signature());
				break;
			case "May":
				construct_pdf_table_for_one_Month(5, 2, report);
				mainTable.setSpacingAfter(50);
				report.add(add_signature());
				break;
			case "June":
				construct_pdf_table_for_one_Month(6, 2, report);
				mainTable.setSpacingAfter(50);
				report.add(add_signature());
				break;
			case "July":
				construct_pdf_table_for_one_Month(7, 2, report);
				mainTable.setSpacingAfter(50);
				report.add(add_signature());
				break;
			case "August":
				construct_pdf_table_for_one_Month(8, 2, report);
				mainTable.setSpacingAfter(50);
				report.add(add_signature());
				break;
			case "September":
				construct_pdf_table_for_one_Month(9, 2, report);
				mainTable.setSpacingAfter(50);
				report.add(add_signature());
				break;
			case "October":
				construct_pdf_table_for_one_Month(10, 2, report);
				mainTable.setSpacingAfter(50);
				report.add(add_signature());
				break;
			case "November":
				construct_pdf_table_for_one_Month(11, 2, report);
				mainTable.setSpacingAfter(50);
				report.add(add_signature());
				break;
			case "December":
				construct_pdf_table_for_one_Month(12, 2, report);
				mainTable.setSpacingAfter(50);
				report.add(add_signature());
				break;
			default:
				break;
			}
		} else {
			construct_pdf_using_date(from_Date.getValue().toString(), to_Date.getValue().toString(), 2, report);
			mainTable.setSpacingAfter(50);
			report.add(add_signature());
		}

		report.close();

	}

	@FXML
	private void btn_Action(ActionEvent event) {
		if (event.getSource() == cancel_btn) {
			FXMLController.pdf_gen_controller = false;
			Stage stage = (Stage) cancel_btn.getScene().getWindow();
			stage.close();
		}
		if (toggle_filter.isSelected()) {
			filter_Combo_Box.setDisable(true);
			from_Date.setDisable(false);
			to_Date.setDisable(false);
		}
		if (!toggle_filter.isSelected()) {
			filter_Combo_Box.setDisable(false);
			from_Date.setDisable(true);
			to_Date.setDisable(true);
		}
		if (event.getSource() == browse_btn) {
			FileChooser fc = new FileChooser();
			fc.getExtensionFilters().addAll(new ExtensionFilter("PDF Files", "*.pdf"));
			File file = fc.showSaveDialog(null);
			if (file != null) {
				file_path.setText(file.getAbsolutePath());
			}
		}
		if (filter_Combo_Box.getSelectionModel().getSelectedItem() == "One Day") {
			from_Date.setDisable(false);
		}

	}

	@FXML
	private void generate_pdf() {
		if (toggle_filter.isSelected()) {
			if (from_Date.getValue() == null) {
				MessageHandler m = new MessageHandler("No from date", "Please pick a 'From' date", "Okay");
			} else if (to_Date.getValue() == null) {
				MessageHandler m = new MessageHandler("No from date", "Please pick a 'To' date", "Okay");
			} else {
				if (!file_path.getText().isEmpty()) {
					if (file_path.getText().endsWith(".pdf")) {
						try {
							boolean exists_from = false;
							boolean exists_to = false;
							int i = 0;
							while (i < table.getItems().size()) {
								if (sdf.parse(table.getColumns().get(1).getCellObservableValue(i).getValue().toString())
										.equals(sdf.parse(from_Date.getValue().toString()))) {
									exists_from = true;
									break;
								}
								i++;
							}
							i = 0;
							while (i < table.getItems().size()) {
								if (sdf.parse(table.getColumns().get(1).getCellObservableValue(i).getValue().toString())
										.equals(sdf.parse(to_Date.getValue().toString()))) {
									exists_to = true;
									break;
								}
								i++;
							}
							if (from_Date.getValue().isBefore(to_Date.getValue())
									&& from_Date.getValue().isBefore(LocalDate.now()) && exists_from == true
									&& exists_to == true) {
								FXMLController.pdf_gen_controller = false;
								construct_pdf();
								MessageHandler m = new MessageHandler("Successful", "Report Generated Succesfully",
										"Okay");
								Stage stage = (Stage) generate_btn.getScene().getWindow();
								stage.close();
							} else {
								MessageHandler m = new MessageHandler("Error", "There is no data for this duration", "Okay");
							}

						} catch (IOException | DocumentException | ParseException e) {
							// TODO Auto-generated catch block
							MessageHandler m = new MessageHandler("Error", e.toString(), "Okay");
						}
					} else {
						MessageHandler m = new MessageHandler("Error", "File is not in PDF Format", "Okay");
					}
				} else {
					MessageHandler m = new MessageHandler("Error", "Specify a file path/name", "Okay");
				}

			}
		} else {
			if (filter_Combo_Box.getSelectionModel().isEmpty()) {
				MessageHandler m = new MessageHandler("No from date", "Please pick a Day/Month from the Combobox",
						"Okay");
			} else {
				if (filter_Combo_Box.getSelectionModel().getSelectedItem().equals("One Day")) {
					if (from_Date.getValue() == null) {
						MessageHandler m = new MessageHandler("No from date", "Please pick a 'From' date", "Okay");
					} else if (file_path.getText().isEmpty()) {
						MessageHandler m = new MessageHandler("Error", "Specify a file path/name", "Okay");
					} else {
						if (file_path.getText().endsWith(".pdf")) {
							try {
								FXMLController.pdf_gen_controller = false;
								construct_pdf();
								MessageHandler m = new MessageHandler("Successful", "Report Generated Succesfully",
										"Okay");
								Stage stage = (Stage) generate_btn.getScene().getWindow();
								stage.close();
							} catch (IOException | DocumentException | ParseException e) {
								// TODO Auto-generated catch block
								MessageHandler m = new MessageHandler("Error", e.toString(), "Okay");
							}
						} else {
							MessageHandler m = new MessageHandler("Error", "File is not in PDF Format", "Okay");
						}
					}
				} else {
					if (!file_path.getText().isEmpty()) {
						if (file_path.getText().endsWith(".pdf")) {
							try {
								FXMLController.pdf_gen_controller = false;
								construct_pdf();
								MessageHandler m = new MessageHandler("Successful", "Report Generated Succesfully",
										"Okay");
								Stage stage = (Stage) generate_btn.getScene().getWindow();
								stage.close();
							} catch (IOException | DocumentException | ParseException e) {
								// TODO Auto-generated catch block
								MessageHandler m = new MessageHandler("Error", e.toString(), "Okay");
							}
						} else {
							MessageHandler m = new MessageHandler("Error", "File is not in PDF Format", "Okay");
						}
					} else {
						MessageHandler m = new MessageHandler("Error", "Specify a file path/name", "Okay");
					}
				}

			}
		}

	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		filter_dym.add("One Day");
		filter_dym.add("January");
		filter_dym.add("February");
		filter_dym.add("March");
		filter_dym.add("April");
		filter_dym.add("May");
		filter_dym.add("June");
		filter_dym.add("July");
		filter_dym.add("August");
		filter_dym.add("September");
		filter_dym.add("October");
		filter_dym.add("November");
		filter_dym.add("December");
		filter_Combo_Box.setItems(filter_dym);

	}

}
