package course;

public class Course {
	private String instructor;
	private String name;
	private int roomNumber;
	private int absences;
	private double finalGrade;
	private String classTime;
	private int id;
	
	public Course(String instructor, String name, int roomNumber, int absences, double finalGrade, String classTime){
		this.instructor = instructor;
		this.name = name;
		this.roomNumber = roomNumber;
		this.absences = absences;
		this.finalGrade = finalGrade;
		this.classTime = classTime;
	}
	
	public Course(String instructor, String name, int roomNumber, int absences, double finalGrade, String classTime, int id){
		this.instructor = instructor;
		this.name = name;
		this.roomNumber = roomNumber;
		this.absences = absences;
		this.finalGrade = finalGrade;
		this.classTime = classTime;
		this.id = id;
	}
	
	public String toString(){
		return getName();
	}
	
	public int getId(){
		return id;
	}
	
	public String getInstructor() {
		return instructor;
	}
	public void setInstructor(String instructor) {
		this.instructor = instructor;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getRoomNumber() {
		return roomNumber;
	}
	public void setRoomNumber(int roomNumber) {
		this.roomNumber = roomNumber;
	}
	public int getAbsences() {
		return absences;
	}
	public void setAbsences(int absences) {
		this.absences = absences;
	}
	public void incrementAbsences(){
		this.absences++;
	}
	public double getFinalGrade() {
		return finalGrade;
	}
	public void setFinalGrade(double finalGrade) {
		this.finalGrade = finalGrade;
	}
	public String getClassTime() {
		return classTime;
	}
	public void setClassTime(String classTime) {
		this.classTime = classTime;
	}
}
