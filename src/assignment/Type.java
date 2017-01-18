package assignment;

public enum Type {
	BLANK(""), HOMEWORK("Homework"), QUIZ("Quiz"), TEST("Test"), PAPER("Paper"), LAB("Lab"), FINAL("Final"),
	DISCUSSION("Discussion"), PROJECT("Project");
	
	private String name;
	
	Type(String name){
		this.name = name;
	}
	
	public String toString(){
		return name;
	}
}
