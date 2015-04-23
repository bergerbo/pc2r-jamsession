package pc2r.upmc.jamsession.network;

import java.lang.reflect.Field;

public final class Command {
	// Client -> Server
	public static final String CONNECT = "CONNECT";
	public static final String EXIT = "EXIT";

	public static final String SET_OPTIONS = "SET_OPTIONS";

	public static final String AUDIO_CHUNK = "AUDIO_CHUNK";
	public static final String AUDIO_ACK = "AUDIO_ACK";

	// Server -> client
	public static final String WELCOME = "WELCOME";
	public static final String AUDIO_PORT = "AUDIO_PORT";
	public static final String AUDIO_OK = "AUDIO_OK";
	public static final String CONNECTED = "CONNECTED";
	public static final String EXITED = "EXITED";

	public static final String EMPTY_SESSION = "EMPTY_SESSION";
	public static final String CURRENT_SESSION = "CURRENT_SESSION";
	public static final String ACK_OPTS = "ACK_OPTS";
	public static final String FULL_SESSION = "FULL_SESSION";

	public static final String AUDIO_SYNC = "AUDIO_SYNC";
	public static final String AUDIO_KO = "AUDIO_KO";
	public static final String AUDIO_MIX = "AUDIO_MIX";

	public static boolean knownCommand(String cmd) {
		Field[] fields = Command.class.getDeclaredFields();
		try {
			for (Field f : fields) {
				if (((String) f.get(null)).equals(cmd))
					return true;
			}
		} catch (IllegalArgumentException e) {
			return false;
		} catch (IllegalAccessException e) {
			return false;
		}

		return false;
	}
}
