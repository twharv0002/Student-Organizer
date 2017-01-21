package course;

public class Course {
	private CourseData data;
	private int id;
	
	public Course(CourseData data){
		this.data = data;
	}
	
	public CourseData getData(){
		return data;
	}
	
	public String toString(){
		return data.getName();
	}
	
	public int getId(){
		return id;
	}

}
