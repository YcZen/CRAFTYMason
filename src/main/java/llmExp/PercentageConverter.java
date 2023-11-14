package llmExp;

public class PercentageConverter {
    public static String doubleToPercentage(double value) {
        // Multiplying the value by 100 and formatting it as a string with a percentage sign
        return String.format("%.0f%%", value * 100);
    }

    public static void main(String[] args) {
        double number = -0.05;
        System.out.println(doubleToPercentage(number)); // Output: 500%
    }
}
