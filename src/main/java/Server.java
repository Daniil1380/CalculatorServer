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
    static Map<Socket, List<Byte>> idMap = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PORT);
        while (!Thread.interrupted()){
            Socket socket = server.accept();
            idMap.put(socket, new ArrayList<>());
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
        while (!Thread.interrupted()) {
            CalculatorPackage calculatorPackage = new CalculatorPackage(inputStream.readNBytes(20));
            if (calculatorPackage.getSpeed() == 1){
                CalculatorPackage answer = new CalculatorPackage();
                answer.setTime((short) 0);
                answer.setId(calculatorPackage.getId());
                if (calculatorPackage.getSecondArgument() == 0 && calculatorPackage.getOperation() == 3) {
                    answer.setError((byte) 2);
                    answer.setSpeed((byte) 0);
                    outputStream.write(answer.toByte());
                    return;
                }
                if (calculatorPackage.getFirstArgument() == 1992 && calculatorPackage.getOperation() == 3
                && calculatorPackage.getSecondArgument() == 4) {
                    answer.setError((byte) 5);
                }

                else {
                    answer.setFirstArgument(
                            calculateFast(calculatorPackage.getFirstArgument(), calculatorPackage.getSecondArgument(),
                                    calculatorPackage.getOperation()));
                }
                answer.setSpeed((byte) 0);
                outputStream.write(answer.toByte());
            }
            else {
                Thread thread = new Thread(() -> {
                    try {
                        List<Byte> bytes = idMap.get(socket);
                        CalculatorPackage answer = new CalculatorPackage();
                        long now = System.currentTimeMillis();
                        long thenNeedToSend = now + 1000 * calculatorPackage.getTime();

                        Thread timer = new Thread(()->{
                            boolean notDelivered = true;
                            while (notDelivered) {
                                try {
                                    Thread.sleep(1000);
                                    if (thenNeedToSend < System.currentTimeMillis()) {
                                        answer.setError((byte) 6);
                                        notDelivered = false;
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        timer.start();

                        if (bytes.contains(calculatorPackage.getId())) {
                            answer.setError((byte) 4);
                            answer.setId(calculatorPackage.getId());
                            outputStream.write(answer.toByte());
                            return;
                        }
                        if ((int) calculatorPackage.getFirstArgument() != calculatorPackage.getFirstArgument()) {
                            answer.setError((byte) 3);
                            answer.setId(calculatorPackage.getId());
                            outputStream.write(answer.toByte());
                            return;
                        }
                        else {
                            bytes.add(calculatorPackage.getId());
                        }
                        answer.setId(calculatorPackage.getId());
                        if (answer.getOperation() > 1){
                            answer.setError((byte) 1);
                        }
                        else {
                            answer.setFirstArgument(calculateSlow(answer.getFirstArgument(), answer.getSecondArgument(), answer.getOperation()));
                        }
                        answer.setSpeed((byte) 0);
                        outputStream.write(answer.toByte());
                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }
                });
                thread.start();


            }
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
