package application;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

public class Assignment {

	private int id;
	private String name;
	private Date date;
	private String info;
	private double grade;
	private boolean isComplete;
	private String className;
	private String type;
	//private int daysUntil;
	
	public Assignment(String name, Date date, String info, double grade, boolean isComplete, String className, String type){
		this.name = name;
		this.date = date;
		this.info = info;
		this.grade = grade;
		this.isComplete = isComplete;
		this.className = className;
		this.type = type;
		//daysUntil = getDaysUntil();
	}
	
	public Assignment(String name, Date date, String info, double grade, boolean isComplete, String className, String type, int id){
		this.name = name;
		this.date = date;
		this.info = info;
		this.grade = grade;
		this.isComplete = isComplete;
		this.className = className;
		this.type = type;
		this.id = id;
		//daysUntil = getDaysUntil();
	}
	
	public int getDaysUntil(){
		long todayMillis = System.currentTimeMillis();
		long millis = (getDate().getTime() - todayMillis);
		long hours = (millis / (60 * 60 * 1000l));
		
		if (hours > 0){
			hours += 24;
		}
		
		return (int) hours / 24;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		Date newDate = Date.valueOf(date.toString());
		this.date = newDate;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public double getGrade() {
		return grade;
	}

	public void setGrade(double grade) {
		this.grade = grade;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getId() {
		return id;
	}
	
	
	
}
