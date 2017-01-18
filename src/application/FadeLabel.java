package application;

import javafx.animation.FadeTransition;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class FadeLabel extends Label {

	private int fadeTime = 1000;
	
	public void setFadeDuration(int duration){
		fadeTime = duration;
	}
	
	public void animate(){
		FadeTransition ft = new FadeTransition(Duration.millis(fadeTime), this);
		ft.setFromValue(0.0);
		ft.setToValue(1.0);
		ft.setCycleCount(2);
		ft.setAutoReverse(true);
		ft.play();
	}
	
}
