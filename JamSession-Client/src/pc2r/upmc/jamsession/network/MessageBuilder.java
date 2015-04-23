package pc2r.upmc.jamsession.network;

import java.util.ArrayList;

public class MessageBuilder {
	private final static char SEP = '/';
	
	
	public static String build(Message msg){
		StringBuilder sb = new StringBuilder();
		
		sb.append(msg.getCmd());
		
		ArrayList<String> args = msg.getArgs();
		
		if(!args.isEmpty()){
			sb.append(SEP);
			for(String arg : args ){
				sb.append(arg);
				sb.append(SEP);
			}
		}
		sb.append("\n");
		return sb.toString();
	}
	
	
	public  static Message parse(String str) throws UnkownCommandException{
		int i;
		char c;
		String cmd;
		ArrayList<String> args;
		StringBuilder sb = new StringBuilder();
		
		//Parse Command
		for(i = 0; i < str.length(); i++){
			c = str.charAt(i);
			if(c == SEP){
				i++;
				break;
			}
			sb.append(c);
		}
	
		cmd = sb.toString();
		if (!Command.knownCommand(cmd))
			throw new UnkownCommandException("Unknown command : " + cmd);
		
		
		//Parse args
		sb = new StringBuilder();
		args = new ArrayList<>();
		while(i < str.length()){
			c = str.charAt(i);
			
			if(c == SEP){
				args.add(sb.toString());
				sb = new StringBuilder();
				i++;
				continue;
			}
				
			sb.append(c);
			i++;
		}
		
		return new Message(cmd, args);
	}
}
