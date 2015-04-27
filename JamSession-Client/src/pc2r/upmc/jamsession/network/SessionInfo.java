package pc2r.upmc.jamsession.network;

import java.util.ArrayList;

public class SessionInfo {

	
	public String style;
	public int tempo;
	public int nb_mus;
	public boolean full;
	
	public void updateInfos(ArrayList<String> args){
		synchronized (this) {
			style = args.get(0);
			tempo = Integer.parseInt(args.get(1));
			nb_mus = Integer.parseInt(args.get(2));
			full = false;
			notifyAll();
		}

	}
	
	public void updateInfos(SessionInfo s){
		synchronized (this) {
			style = s.style;
			tempo = s.tempo;
			nb_mus = s.nb_mus;
			full = false;
			notifyAll();
		}
	}
	
	public SessionInfo(){
		full = true;
		tempo = 0;
		nb_mus = 0;
		style = "Unknown";
	}
	
	
}
