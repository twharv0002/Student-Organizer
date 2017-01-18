package assignment;

import java.sql.Date;
import java.time.LocalDate;

public class AssignmentSpec {

	private String name;
	private Date date;
	private String info;
	private double grade;
	private boolean isComplete;
	private String className;
	private Type type;
	
	public AssignmentSpec(String name, Date date, String info, double grade, boolean isComplete,
			String className, Type type){
		this.name = name;
		this.date = date;
		this.info = info;
		this.grade = grade;
		this.isComplete = isComplete;
		this.className = className;
		this.type = type;
	}
	
	public boolean matches(AssignmentSpec searchSpec, DateOption dateOption, String gradeOption){
		if(!(searchSpec.getName().equals("") || getName().equals(searchSpec.getName()))){
			return false;
		}
		if(!(searchSpec.getClassName().equals("") || getClassName().equals(searchSpec.getClassName()))){
			return false;
		}
		if(!(searchSpec.getType() == Type.BLANK || getType().equals(searchSpec.getType()))){
			return false;
		}
		if(!(dateOption == DateOption.BLANK || meetsDateCriteria(dateOption))){
			return false;
		}
		if(!(gradeOption.equals("") || meetsGradeCriteria(gradeOption))){
			return false;
		}
		if(searchSpec.isComplete() == true){
			if(isComplete() == true){
				return false;
			}
		}
		return true;
	}
	
	private boolean meetsDateCriteria(DateOption dateOption){
		if(dateOption == DateOption.UPCOMING){
			if(!(getDaysUntil() >= 0))
				return false;
		}
		if(dateOption == DateOption.PAST){
			if(!(getDaysUntil() < 0)){
				return false;
			}
		}
		if(dateOption == DateOption.NEXT_7_DAYS){
			if(!(getDaysUntil() <= 7 && getDaysUntil() >= 0))
				return false;
		}
		if(dateOption == DateOption.NEXT_30_DAYS){
			if(!(getDaysUntil() <= 30 && getDaysUntil() >= 0))
				return false;
		}
		return true;
	}

	private boolean meetsGradeCriteria(String gradeOption){
		if(gradeOption.equals("A")){
			if(!(getGrade() >= 90))
				return false;
		}
		if(gradeOption.equals("B")){
			if(!(getGrade() >= 80 && getGrade() < 90)){
				return false;
			}
		}
		if(gradeOption.equals("C")){
			if(!(getGrade() >= 70 && getGrade() < 80))
				return false;
		}
		if(gradeOption.equals("F")){
			if(!(getGrade() < 70))
				return false;
		}
		return true;
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

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
	
}
