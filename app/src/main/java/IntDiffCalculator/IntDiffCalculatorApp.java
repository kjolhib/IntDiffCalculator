package IntDiffCalculator.app.src.main.java.IntDiffCalculator;

import java.util.Scanner;

public class IntDiffCalculatorApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Parser parser = new Parser();
    
    public static void main(String[] args) {
        try (scanner) {
            System.out.println("=== Integration/Differentiation Calculator ===");
            
            while (true) {
                displayMenu();
                int choice = getUserChoice();
                
                if (choice == 3) {
                    System.out.println("Exiting calculator. Goodbye!");
                    break;
                }
                
                System.out.print("Enter the mathematical expression: ");
                String expression = scanner.nextLine().trim();
                
                if (expression.isEmpty()) {
                    System.out.println("Error: Expression cannot be empty.\n");
                    continue;
                }
                
                try {
                    handleCalculation(choice, expression);
                } catch (Exception e) {
                    System.out.println("Error processing expression: " + e.getMessage() + "\n");
                }
            }
        }
    }
    
    private static void displayMenu() {
        System.out.println("\nWhat would you like to do?");
        System.out.println("1. Differentiate");
        System.out.println("2. Integrate");
        System.out.println("3. Exit");
    }
    
    private static int getUserChoice() {
        int choice = -1;
        while (choice < 1 || choice > 3) {
            System.out.print("Enter your choice (1-3): ");
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice < 1 || choice > 3) {
                    System.out.println("Invalid choice. Please enter 1, 2, or 3.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
        return choice;
    }
    
    private static void handleCalculation(int choice, String expression) {
        if (choice == 1) {
            System.out.println("\nDifferentiating: " + expression);
            // TODO: Implement differentiation
            String result = parser.differentiate(expression);
            System.out.println("Result: " + result + "\n");
        } else if (choice == 2) {
            System.out.println("\nIntegrating: " + expression);
            // TODO: Implement integration
            String result = parser.integrate(expression);
            System.out.println("Result: " + result + "\n");
        }
    }
}
