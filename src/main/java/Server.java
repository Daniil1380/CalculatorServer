import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {
    public static final int PORT = 61336;
    public static Map<Socket, List<Byte>> idMap = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PORT);

        while (!Thread.interrupted()){
            Socket socket = server.accept();
            idMap.put(socket, new ArrayList<>());
            Thread thread = new Thread(() -> {
                try {
                    work(socket);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            });
            thread.start();
        }
    }

    private static void work(Socket socket) throws IOException {
        MessageHandler messageHandler = new MessageHandler(socket);
        while (!Thread.interrupted()) {
            messageHandler.handleMessage();
        }
    }

}
