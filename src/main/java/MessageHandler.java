import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class MessageHandler {

    private Socket socket;

    public MessageHandler(Socket socket) {
        this.socket = socket;
    }

    public void handleMessage() throws IOException {
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();
        CalculatorPackage calculatorPackage = new CalculatorPackage(inputStream.readNBytes(20));

        if (calculatorPackage.getSpeed() == 1) {
            handleFast(calculatorPackage, outputStream);
        } else {
            Thread calculation = new Thread(() -> handleLong(calculatorPackage, outputStream));
            calculation.start();
        }
    }

    private static void write(CalculatorPackage answer, OutputStream outputStream) throws IOException {
        System.out.println(answer.toString());
        outputStream.write(answer.toByte());
    }

    private void handleFast(CalculatorPackage calculatorPackage, OutputStream outputStream) throws IOException {
        CalculatorPackage answer = new CalculatorPackage();
        answer.setId(calculatorPackage.getId());
        byte error = findErrorToFast(calculatorPackage);
        if (error == 0) {
            answer.setFirstArgument(
                    MathUtil.calculateFast(calculatorPackage.getFirstArgument(), calculatorPackage.getSecondArgument(),
                            calculatorPackage.getOperation()));
        } else {
            answer.setError(error);
        }
        write(answer, outputStream);
    }


    private void handleLong(CalculatorPackage calculatorPackage, OutputStream outputStream) {
        try {
            List<Byte> bytes = Server.idMap.get(socket);
            CalculatorPackage answer = new CalculatorPackage();
            long now = System.currentTimeMillis();
            long thenNeedToSend = now + 1000 * calculatorPackage.getTime();

            Thread timer = new Thread(() -> timerWork(thenNeedToSend, answer));
            timer.start();

            byte error = findErrorToLong(calculatorPackage, bytes);
            if (error == 0) {
                bytes.add(calculatorPackage.getId());
                answer.setFirstArgument(MathUtil.calculateSlow(calculatorPackage, answer));
            }
            else {
                answer.setError(error);
            }
            answer.setId(calculatorPackage.getId());
            write(answer, outputStream);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void timerWork(long thenNeedToSend, CalculatorPackage answer) {
        boolean stopWatch = true;
        while (stopWatch) {
            try {
                Thread.sleep(1000);
                if (thenNeedToSend < System.currentTimeMillis()) {
                    answer.setError((byte) 6);
                    stopWatch = false;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private byte findErrorToFast(CalculatorPackage calculatorPackage) {
        if (calculatorPackage.getSecondArgument() == 0 && calculatorPackage.getOperation() == 3) {
            return 2;
        }
        if (calculatorPackage.getFirstArgument() == 1992 && calculatorPackage.getOperation() == 3
                && calculatorPackage.getSecondArgument() == 4) {
            return 7;
        }
        return 0;
    }

    private byte findErrorToLong(CalculatorPackage calculatorPackage, List<Byte> bytes) {
        if (bytes.contains(calculatorPackage.getId())) {
            return 4;
        }
        if ((int) calculatorPackage.getFirstArgument() != calculatorPackage.getFirstArgument()) {
            return 3;
        }
        if (calculatorPackage.getOperation() > 1) {
            return 1;
        }
        return 0;
    }

}
