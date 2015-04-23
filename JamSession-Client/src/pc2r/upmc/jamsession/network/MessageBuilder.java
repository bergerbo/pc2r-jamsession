package pc2r.upmc.jamsession.network;

import java.util.ArrayList;
import java.util.Arrays;

public class MessageBuilder {
	private final static char SEP = '/';

	public static String build(Message msg) {
		StringBuilder sb = new StringBuilder();

		sb.append(msg.getCmd());

		ArrayList<String> args = msg.getArgs();

		if (!args.isEmpty()) {
			sb.append(SEP);
			for (String arg : args) {
				sb.append(escape(arg));
				sb.append(SEP);
			}
		}
		sb.append("\n");
		return sb.toString();
	}

	int i;

	public static Message parse(String s) throws UnkownCommandException {
		String cmd;
		ArrayList<String> args;
		String str = unescape(s);
		
		String[] parts = str.split("/");

		cmd = parts[0];
		if (!Command.knownCommand(cmd))
			throw new UnkownCommandException("Unknown command : " + cmd);

		args = new ArrayList<String>(Arrays.asList(
				Arrays.copyOfRange(parts, 1, parts.length)));;

		return new Message(cmd, args);
	}

	private static String escape(String s) {
		String escaped;
		escaped = s.replace("\\", "\\\\");
		escaped = escaped.replace("/", "\\/");
		return escaped;
	}

	private static String unescape(String s) {
		String unescaped;
		unescaped = s.replace("\\/", "/");
		unescaped = unescaped.replace("\\\\", "\\");
		return unescaped;
	}
}
