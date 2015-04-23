package pc2r.upmc.jamsession.network;

import java.util.ArrayList;

public class SessionInfo {

	public String style;
	public int tempo;
	public int nb_mus;
	public boolean full;
	
	public SessionInfo(ArrayList<String> args){
		style = args.get(0);
		tempo = Integer.parseInt(args.get(1));
		nb_mus = Integer.parseInt(args.get(2));
		full = false;
	}
	
	public SessionInfo(){
		full = true;
		tempo = 0;
		nb_mus = 0;
		style = "Unknown";
	}
	
}
