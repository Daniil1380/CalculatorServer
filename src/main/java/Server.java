import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static final int PORT = 61336;

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PORT);
        while (true){
            Socket socket = server.accept();
            Thread thread = new Thread(() -> {
                try {
                    work(socket);
                } catch (IOException | InterruptedException exception) {
                    exception.printStackTrace();
                }
            });
            thread.start();
        }
    }

    private static void work(Socket socket) throws IOException, InterruptedException {
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();
        CalculatorPackage calculatorPackage = new CalculatorPackage(inputStream.readNBytes(20));
        if (calculatorPackage.getSpeed() == 1){
            CalculatorPackage answer = new CalculatorPackage();
            answer.setTime((short) 0);
            answer.setId(calculatorPackage.getId());
            if (calculatorPackage.getSecondArgument() == 0 && calculatorPackage.getOperation() == 3) {
                answer.setError((byte) 2);
            }
            else {
                answer.setFirstArgument(calculateFast(answer.getFirstArgument(), answer.getSecondArgument(), answer.getOperation()));
            }
            answer.setSpeed((byte) 0);
            outputStream.write(answer.toByte());
        }
        else {
            long now = System.currentTimeMillis();
            long thenNeedToSend = now + 1000 * calculatorPackage.getTime();
            CalculatorPackage answer = new CalculatorPackage();
            answer.setTime((short) 0);
            answer.setId(calculatorPackage.getId());
            if (answer.getOperation() > 1){
                answer.setError((byte) 1);
            }
            else {
                answer.setFirstArgument(calculateSlow(answer.getFirstArgument(), answer.getSecondArgument(), answer.getOperation()));
            }
            answer.setSpeed((byte) 0);
            if (System.currentTimeMillis() > thenNeedToSend){
                answer.setError((byte) 6);
            }
            else {
                while (System.currentTimeMillis() < thenNeedToSend){
                    Thread.sleep(1000);
                }
            }
            outputStream.write(answer.toByte());
        }

    }

    private static double calculateFast(Double first, Double second, byte operation) {
        switch (operation){
            case 0:
                return first + second;
            case 1:
                return first - second;
            case 2:
                return first * second;
            case 3:
                return first / second;
        }
        return 0.0;
    }

    private static double calculateSlow(Double first, Double second, byte operation) {
        switch (operation){
            case 0:
                return Math.sqrt(first);
            case 1:
                return fact(first);
        }
        return 0.0;
    }

    private static double fact(Double first) {
        int firstInt = first.intValue();
        int answer = 1;
        for (int i = 1; i <= firstInt; i++) {
            answer *= i;
        }
        return answer;
    }
}
