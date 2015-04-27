package pc2r.upmc.jamsession.network;

import java.util.Calendar;
import java.util.Date;

public class Log {

	public Date t;
	public String s;
	
	
	public Log(String s){
		this.s = s;
		t = Calendar.getInstance().getTime();
	}
}
