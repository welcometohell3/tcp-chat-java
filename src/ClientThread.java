import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

public class ClientThread extends Thread {
    private final BufferedReader in;
    private final PrintWriter out;
    private String userName;

    public ClientThread(Socket socket) throws IOException {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        userName = in.readLine();
    }

    @Override
    public void run() {
        while (true) {
            try {
                String message = in.readLine();

                if (message.startsWith("@quit")) {
                    System.out.println(userName + " disconnected");
                    Server.broadcast(userName + " left the chat", this);
                    Server.removeUser(this);
                    break;

                } else if (message.startsWith("@name") && message.length() > 6) {
                    userName = message.substring(6);

                } else if (message.startsWith("@senduser")) {
                    String[] parts = message.split(" ");
                    String recipient = parts[1];
                    message = String.join(" ", Arrays.copyOfRange(parts, 2, parts.length));
                    message = userName + "(whispers): " + message;
                    Server.sendToUser(recipient, message);

                } else if (!message.isEmpty()) {
                    Server.broadcast(userName + ": " + message, this);
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public String getUserName() {
        return userName;
    }

    public void sendMessage(String sentence) {
        out.println(sentence);
        out.flush();
    }
}
