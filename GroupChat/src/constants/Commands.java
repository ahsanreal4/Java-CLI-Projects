package constants;

public class Commands {
    public static final String PING_COMMAND = "ping";
    public static final String STOP_COMMAND = "stop";
    public static final String CLIENT_LOGIN_COMMAND = "login";
    public static final String CLIENT_MESSAGE_COMMAND = "message";
    public static final String CLIENT_HELP_FLAG = "--help";
    public static final String CLIENT_JOIN_COMMAND = "join";
    public static final String CLIENT_LEAVE_COMMAND = "leave";

    public static final String PAYLOAD_REGEX = ",";
    public static final String COMMAND_REGEX = ":";

    public static final String SERVER_OK_COMMAND = "ok";
    public static final String SERVER_REJECT_COMMAND = "reject";
}
