import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Server {
    private static List<ClientThread> clientsList;
    private static ServerSocket serverSocket;

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("example: java Server 1234");
            System.exit(0);
        }

        var port = Integer.parseInt(args[0]);
        clientsList = new ArrayList<>();
        serverSocket = new ServerSocket(port);

        System.out.println("server launched on port = " + port);
        System.out.println("waiting for users...");

        while (!serverSocket.isClosed()) {
            var socket = serverSocket.accept();
            System.out.println("new user connected");
            var clientThread = new ClientThread(socket);
            clientsList.add(clientThread);
            clientThread.start();
        }
    }

    public static void broadcast(String sentence, ClientThread currentClient) {
        for (ClientThread client : clientsList) {
            if (client != currentClient) {
                client.sendMessage(sentence);
            }
        }
    }

    public static void sendToUser(String recipient, String sentence) {
        for (ClientThread client : clientsList) {
            if (Objects.equals(client.getUserName(), recipient)) {
                client.sendMessage(sentence);
            }
        }
    }

    public static void removeUser(ClientThread currentClient) {
        clientsList.removeIf(client -> client == currentClient);
    }
}