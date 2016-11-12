package application;

import javafx.scene.control.CheckBox;

public class TableCheckBox extends CheckBox{
	private int index;
	
	public TableCheckBox(int index){
		this.index = index;
	}
	
	public int getIndex(){
		return index;
	}
	
	
}
