package pc2r.upmc.jamsession.network;

import java.util.ArrayList;

public class Message {
	
	private String cmd;
	private ArrayList<String> args;
	
	public Message(String cmd){
		this.cmd = cmd;
		args = new ArrayList<>();
	}
	
	public Message(String cmd, ArrayList<String> args){
		this.cmd = cmd;
		this.args = args;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public ArrayList<String> getArgs() {
		return args;
	}
	
	public void addArg(String arg){
		args.add(arg);
	}

	public void setArgs(ArrayList<String> args) {
		this.args = args;
	}
	
}
