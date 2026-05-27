package CalculusCalculator;

import static CalculusCalculator.AST.Op.BinaryOp.MUL;

import CalculusCalculator.AST.Expr.BinaryExpr;
import CalculusCalculator.AST.Expr.Expr;
import CalculusCalculator.AST.Expr.NumberExpr;
import CalculusCalculator.AST.Expr.UnaryExpr;
import CalculusCalculator.AST.Expr.VariableExpr;
import CalculusCalculator.AST.Op.BinaryOp;
import CalculusCalculator.AST.Op.UnaryOp;

/**
 * A pretty printer for the AST representation of the mathamatical expression. It will convert the AST back to a human readable string format.
 * E.g. Given the AST:
 * NUMBER(3)
 * BINARYOP(+)
 * NUMBER(2)
 * IDENTIFIER(x)
 * This will produce the string "3 + 2x".
 */
public class PrettyPrinter {
  public String print(Expr e) {
    // for parsing precedence
    return printPrecedence(e, 0);
  }
  private String printPrecedence(Expr expr, int parentPrecedence) {
    return switch (expr) {
      case NumberExpr n -> formatNumber(n.value()); // print a regular number
      case VariableExpr v -> v.name(); // print regular variable
      case UnaryExpr u -> switch (u.operator()) { // unary and known function cases
        case UnaryOp.NEG -> "-" + printPrecedence(u.operand(), 0);
        case UnaryOp.SIN -> "sin(" + printPrecedence(u.operand(), 0) + ")";
        case UnaryOp.COS -> "cos(" + printPrecedence(u.operand(), 0) + ")";
        case UnaryOp.TAN -> "tan(" + printPrecedence(u.operand(), 0) + ")";
        case UnaryOp.EXP -> "exp(" + printPrecedence(u.operand(), 0) + ")";
        case UnaryOp.LN -> "ln(" + printPrecedence(u.operand(), 0) + ")";
      };
      case BinaryExpr b -> {
        if (b.operator() == MUL 
          && b.left() instanceof NumberExpr n // left is a number
          && !startsWithNumber(b.right()) // nor is it a binary expression. This case is to guard against 2 * 3(x + 2)
        ) {
          // 3 * x becomes 3x 
          yield formatNumber(n.value()) + printPrecedence(b.right(), findPrecedence(MUL));
        }
        
        if (b.operator() == MUL
          && b.left() instanceof VariableExpr v
          && !startsWithNumber(b.right())
        ) {
          yield v.name() + printPrecedence(b.right(), findPrecedence(MUL));
        }

        /*
        Given the format:
          <term> op <term>,
        we only bracket the first term if it has a lower precedence than the outer.
        Ie. (2 * x) + 1: 2 * x (mul) > + 1 (add), can trivially be seen to be 2x + 1.
            (2 + x) * 3: 2 + x (add) < * 3 (mul), therefore cannot remove brackets. 
        */
        int myPrecedence = findPrecedence(b.operator()); // find the precedence of the op
        String inner = printPrecedence(b.left(), myPrecedence) + " " + b.operator().symbol + " " + printPrecedence(b.right(), myPrecedence); // get inner precedence
        yield myPrecedence < parentPrecedence ? "(" + inner + ")" : inner; // decide to bracket or not
      }
    };
  }

  /**
   * Formats a double to an int if possible.
   * @param value
   * @return String: the "stringified" of the double
   */
  private String formatNumber(double value) {
    return value == Math.floor(value) ? String.valueOf((int) value) : String.valueOf(value);
  }

  /**
   * Returns the precedence of the mathematical operators, + - * / ^.
   * Order follows the mathematical standard:
   * 1. Power
   * 2. Multiply/Division
   * 3. Addition/Subtraction
   * @param op the operator
   * @return int: a number between 1 - 3, with 1 being lowest precedence (add/sub), 2 being mul/div, and pow being highest (pow)
   */
  private int findPrecedence(BinaryOp op) {
    return switch (op) {
      case ADD, SUB -> 1;
      case MUL, DIV -> 2;
      case POW -> 3;
    };
  }

  /**
   * Detects whether or not a multiplication chain starts with a number expression
   * @param e expression to check if starts with NumberExpr
   * @return true or false
   */
  private boolean startsWithNumber(Expr e) {
    return switch (e) {
      case NumberExpr n -> true;
      case BinaryExpr b -> {
        if (b.operator() == MUL) {
          yield b.left() instanceof NumberExpr;
        }
        yield false;
      }
      default -> false;
    };
  }
}
