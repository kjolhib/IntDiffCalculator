package IntDiffCalculator;

import java.util.List;
import java.util.Scanner;

/**
 * Main application class for the Integration/Differentiation Calculator.
 * This class provides a command-line interface for users to input mathematical expressions and choose whether to differentiate or integrate them. It uses a Parser class to process the expressions and compute
 */
public class IntDiffCalculatorApp {
  private static final Scanner scanner = new Scanner(System.in);
  private static final Parser parser = new Parser();
  
  public static void main(String[] args) {
    try (scanner) {
        System.out.println("=== Integration/Differentiation Calculator ===");
        
        while (true) {
          // Continually scan input
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
            Lexer lexer = new Lexer(expression);
            List<Token> tokens = lexer.tokenise();
            printAllTokens(choice, tokens);
            // Expr ast = parser.parse(expression);
            // String result = evaluator.evaluate(choice, ast);
          } catch (Exception e) {
            System.out.println("Error processing expression: " + e.getMessage() + "\n");
          }
        }
      }
  }
  
  /**
   * Displays the main menu options to the user.
   */
  private static void displayMenu() {
    System.out.println("\nWhat would you like to do?");
    System.out.println("1. Differentiate");
    System.out.println("2. Integrate");
    System.out.println("3. Exit");
  }
  
  /**
   * Gets the user's choice for the operation to perform.
   * @return choice: valid inputs are 1 (diff), 2 (int), or 3 (exit).
   */
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
  
  private static void printAllTokens(int choice, List<Token> tokens) {
    String operation = (choice == 1) ? "Differentiating:" : "Integrating:";
    System.out.println(operation);
    for (Token token : tokens) {
      System.out.println(token.toString());
    }
  }
}
