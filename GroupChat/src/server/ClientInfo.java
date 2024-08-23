package server;

public class ClientInfo {
    private String username;
    private boolean hasEnteredChat;

    public ClientInfo() {
        username = "";
        hasEnteredChat = false;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean hasEnteredChat() {
        return hasEnteredChat;
    }

    public void setHasEnteredChat(boolean hasEnteredChat) {
        this.hasEnteredChat = hasEnteredChat;
    }
}
