package Lesson3.server;

import org.apache.log4j.Logger;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ServerChat implements Lesson3.server.Chat {
    private ServerSocket serverSocket;
    private Set<Lesson3.server.ClientHandler> clients;
    private Lesson3.server.AuthenticationService authenticationService;
    private static final Logger logger = Logger.getLogger(ServerChat.class);

    public ServerChat() {
        start();
    }

    @Override
    public boolean getAuthenticationService(String login, String password) {
        return authenticationService.findNicknameByLoginAndPassword(login, password);
    }

    private void start() {
        try {
            logger.debug("Server open");
            serverSocket = new ServerSocket(8888);
            clients = new HashSet<>();
            authenticationService = new Lesson3.server.AuthenticationService();

            while (true) {
                System.out.println("Server is waiting for a connection ...");
                Socket socket = serverSocket.accept();
                Lesson3.server.ClientHandler clientHandler = new Lesson3.server.ClientHandler(socket, this);
                logger.debug("Client connect into server " + clientHandler.getName());
                System.out.println(String.format("[%s] Client[%s] is successfully logged in", new Date(), clientHandler.getName()));
            }
        } catch (Exception e) {
            logger.error("Server close: ", e);
            e.printStackTrace();
        }
    }


    @Override
    public synchronized void broadcastMessage(String message) {
        logger.debug("Send message: " + message);
        for (Lesson3.server.ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    @Override
    public synchronized boolean isNicknameOccupied(String userName) {
        for (Lesson3.server.ClientHandler client : clients) {
            if (client.getName().equals(userName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized void subscribe(Lesson3.server.ClientHandler client) {

        clients.add(client);
        logger.debug("Client authentication into server: " + client);

    }

    @Override
    public synchronized void unsubscribe(Lesson3.server.ClientHandler client) {
        clients.remove(client);
        logger.debug("Client remove from server: " + client);
    }
}