
import java.nio.ByteBuffer;

public class CalculatorPackage {

    private byte speed;
    private byte operation;
    private byte error;
    private byte id;
    private short time;
    private double firstArgument;
    private double secondArgument;

    public byte[] toByte(){
        return ByteBuffer.allocate(20)
                    .put((byte) (speed | operation | error))
                    .put(id)
                    .putShort(time)
                    .putDouble(firstArgument)
                    .putDouble(secondArgument)
                    .array();
    }

    public CalculatorPackage(byte[] value) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(value);
        byte first = byteBuffer.get();
        speed = (byte) (first & 0b10000000);
        operation = (byte) (first & 0b01100000);
        error = (byte) (first & 0b00011111);
        id = byteBuffer.get();
        time = byteBuffer.getShort();
        firstArgument = byteBuffer.getDouble();
        secondArgument = byteBuffer.getDouble();
    }

    public byte getSpeed() {
        return (byte) (speed >> 7 & 0b00000001);
    }

    public void setSpeed(byte speed) {
        this.speed = (byte) (speed << 7);
    }

    public byte getOperation() {
        return (byte) (operation >> 5);
    }

    public void setOperation(byte operation) {
        this.operation = (byte) (operation << 5);
    }

    public byte getError() {
        return error;
    }

    public void setError(byte error) {
        this.error = error;
    }

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public short getTime() {
        return time;
    }

    public void setTime(short time) {
        this.time = time;
    }

    public double getFirstArgument() {
        return firstArgument;
    }

    public void setFirstArgument(double firstArgument) {
        this.firstArgument = firstArgument;
    }

    public double getSecondArgument() {
        return secondArgument;
    }

    public void setSecondArgument(double secondArgument) {
        this.secondArgument = secondArgument;
    }

    public CalculatorPackage() {
    }

    @Override
    public String toString() {
        return "CalculatorPackage{" +
                "speed=" + (byte) (speed >> 7 & 0b00000001) +
                ", operation=" + (byte) (operation >> 5 & 0b00000011) +
                ", error=" + error +
                ", id=" + id +
                ", time=" + time +
                ", firstArgument=" + firstArgument +
                ", secondArgument=" + secondArgument +
                '}';
    }
}
