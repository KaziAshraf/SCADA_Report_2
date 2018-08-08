package application;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Data {

	private SimpleStringProperty Generation;
	private SimpleStringProperty Demand;
	private SimpleStringProperty Shortage;
	private SimpleStringProperty Loadshed;
	private SimpleStringProperty Date;
	private SimpleStringProperty Hour;
	private SimpleStringProperty Remarks;
	private SimpleStringProperty ID;

	public Data(String id, String date, String hour, String generation, String demand, String shortage, String loadshed,
			String remarks) {

		this.Generation = new SimpleStringProperty(generation);
		this.Demand = new SimpleStringProperty(demand);
		this.Shortage = new SimpleStringProperty(shortage);
		this.Loadshed = new SimpleStringProperty(loadshed);
		this.Date = new SimpleStringProperty(date);
		this.Hour = new SimpleStringProperty(hour);
		this.Remarks = new SimpleStringProperty(remarks);
		this.ID = new SimpleStringProperty(id);
	}
	public Data(String date, String hour, String generation, String demand, String shortage, String loadshed,
			String remarks) {

		this.Generation = new SimpleStringProperty(generation);
		this.Demand = new SimpleStringProperty(demand);
		this.Shortage = new SimpleStringProperty(shortage);
		this.Loadshed = new SimpleStringProperty(loadshed);
		this.Date = new SimpleStringProperty(date);
		this.Hour = new SimpleStringProperty(hour);
		this.Remarks = new SimpleStringProperty(remarks);
		
	}

	public Data() {

	}

	public SimpleStringProperty GenerationProperty() {
		return this.Generation;
	}

	public String getGeneration() {
		return this.GenerationProperty().get();
	}

	public void setGeneration(String generation) {
		this.GenerationProperty().set(generation);
	}

	public SimpleStringProperty DemandProperty() {
		return this.Demand;
	}

	public String getDemand() {
		return this.DemandProperty().get();
	}

	public void setDemand(String demand) {
		this.DemandProperty().set(demand);
	}

	public SimpleStringProperty ShortageProperty() {
		return this.Shortage;
	}

	public String getShortage() {
		return this.ShortageProperty().get();
	}

	public void setShortage(String Shortage) {
		this.ShortageProperty().set(Shortage);
	}

	public SimpleStringProperty LoadshedProperty() {
		return this.Loadshed;
	}

	public String getLoadshed() {
		return this.LoadshedProperty().get();
	}

	public void setLoadshed(String Loadshed) {
		this.LoadshedProperty().set(Loadshed);
	}

	

	public SimpleStringProperty HourProperty() {
		return this.Hour;
	}

	public String getHour() {
		return this.HourProperty().get();
	}

	public void setHour(String Hour) {
		this.HourProperty().set(Hour);
	}

	public SimpleStringProperty RemarksProperty() {
		return this.Remarks;
	}

	public String getRemarks() {
		return this.RemarksProperty().get();
	}

	public void setRemarks(final String Remarks) {
		this.RemarksProperty().set(Remarks);
	}

	public SimpleStringProperty DateProperty() {
		return this.Date;
	}
	

	public String getDate() {
		return this.DateProperty().get();
	}
	

	public void setDate(final String Date) {
		this.DateProperty().set(Date);
	}

	public SimpleStringProperty IDProperty() {
		return this.ID;
	}
	

	public String getID() {
		return this.IDProperty().get();
	}
	

	public void setID(final String ID) {
		this.IDProperty().set(ID);
	}
	
	

	
	

}
