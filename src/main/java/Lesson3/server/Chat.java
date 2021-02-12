package Lesson3.server.1.server;

public interface Chat {
    void broadcastMessage(String message);

    boolean isNicknameOccupied(String nickname);

    void subscribe(ClientHandler client);

    void unsubscribe(ClientHandler client);

    boolean getAuthenticationService(String credential, String s);
}