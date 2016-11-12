package application;

import jfxtras.scene.control.agenda.Agenda.AppointmentImplLocal;

public class ModdedAppointment extends AppointmentImplLocal{
	
	private int id;
	
	public ModdedAppointment(int id) {
		this.id = id;
	}
	
	public int getId(){
		return id;
	}
	
	public void setId(int id){
		this.id = id;
	}

}
