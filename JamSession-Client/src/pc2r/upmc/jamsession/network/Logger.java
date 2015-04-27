package pc2r.upmc.jamsession.network;

import java.util.ArrayList;

public class Logger {

	public ArrayList<Log> log;
	
	public Logger(){
		log = new ArrayList<>();
	}
	
	public void log(Log l){
		log.add(l);
	}
	
	
	
}
