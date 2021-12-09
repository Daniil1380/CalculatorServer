public class MathUtil {

    static double calculateFast(Double first, Double second, byte operation) {
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

    static double calculateSlow(CalculatorPackage calculatorPackage, CalculatorPackage answerPackage) throws InterruptedException {
        switch (calculatorPackage.getOperation()){
            case 0:
                return Math.sqrt(calculatorPackage.getFirstArgument());
            case 1:
                return fact(calculatorPackage, answerPackage);
        }
        return 0.0;
    }

    static double fact(CalculatorPackage calculatorPackage, CalculatorPackage answerPackage) throws InterruptedException {
        int firstInt = (int) calculatorPackage.getFirstArgument();
        int answer = 1;
        for (int i = 1; i <= firstInt; i++) {
            Thread.sleep(1000);
            if (answerPackage.getError() == 6){
                return 0;
            }
            answer *= i;
        }
        return answer;
    }
}
