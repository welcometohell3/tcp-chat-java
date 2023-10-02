import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private static Thread thread1;
    private static Thread thread2;
    private static int port;
    private static BufferedReader inFromUser;
    private static PrintWriter out;
    private static BufferedReader in;
    private static Socket socket;

    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.out.println("example: java Client 1234");
            System.exit(0);
        }

        port = Integer.parseInt(args[0]);
        socket = new Socket("localhost", port);
        inFromUser = new BufferedReader(new InputStreamReader(System.in));
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        System.out.print("Enter your name: ");
        out.println(inFromUser.readLine());

        execute();
    }

    public static void execute() throws IOException {

        thread1 = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    getMessage();
                } catch (IOException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        });

        thread2 = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    writeMessage();
                } catch (IOException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        });

        thread1.start();
        thread2.start();
    }

    private static void getMessage() throws IOException {
        System.out.println(in.readLine());
    }

    private static void writeMessage() throws IOException {
        String message;
        message = inFromUser.readLine();
        out.println(message);
        if (message.startsWith("@quit")) {
            System.err.println("disconnected");
            thread1.interrupt();
            thread2.interrupt();
            socket.close();
        }
    }
}