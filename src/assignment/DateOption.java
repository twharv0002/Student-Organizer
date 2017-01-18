package assignment;

public enum DateOption {
	BLANK(""), UPCOMING("Upcoming"), PAST("Past"), NEXT_7_DAYS("Next 7 Days"), NEXT_30_DAYS("Next 30 Days");
	
	private String name;
	
	DateOption(String name){
		this.name = name;
	}
	
	public String toString(){
		return name;
	}
}
