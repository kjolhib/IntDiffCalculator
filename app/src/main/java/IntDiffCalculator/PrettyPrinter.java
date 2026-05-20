package IntDiffCalculator;

import IntDiffCalculator.AST.Expr.BinaryExpr;
import IntDiffCalculator.AST.Expr.Expr;
import IntDiffCalculator.AST.Expr.NumberExpr;
import IntDiffCalculator.AST.Expr.UnaryExpr;
import IntDiffCalculator.AST.Expr.VariableExpr;
import IntDiffCalculator.AST.Op.UnaryOp;

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
  public String print(Expr expr) {
    return switch (expr) {
      case NumberExpr n -> formatNumber(n.value());
      case VariableExpr v -> v.name();
      case UnaryExpr u -> switch (u.operator()) {
        case UnaryOp.NEG -> "-" + print(u.operand());
        case UnaryOp.SIN -> "sin(" + print(u.operand()) + ")";
        case UnaryOp.COS -> "cos(" + print(u.operand()) + ")";
        case UnaryOp.TAN -> "tan(" + print(u.operand()) + ")";
        case UnaryOp.EXP -> "exp(" + print(u.operand()) + ")";
        case UnaryOp.LN -> "ln(" + print(u.operand()) + ")";
      };
      case BinaryExpr b -> "(" + print(b.left()) + " " + b.operator().symbol + " " + print(b.right()) + ")";
    };
  }

  private String formatNumber(double value) {
    return value == Math.floor(value) ? String.valueOf((int) value) : String.valueOf(value);
  }
}
