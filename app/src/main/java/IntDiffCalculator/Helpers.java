package IntDiffCalculator;

import IntDiffCalculator.AST.Expr.BinaryExpr;
import IntDiffCalculator.AST.Expr.Expr;
import IntDiffCalculator.AST.Expr.NumberExpr;
import IntDiffCalculator.AST.Expr.UnaryExpr;
import IntDiffCalculator.AST.Expr.VariableExpr;

public class Helpers {
  public static boolean containsVariable(Expr e, String wrt) {
    return switch (e) {
      case NumberExpr n -> false;
      case VariableExpr v -> v.name().equals(wrt);
      case UnaryExpr u -> containsVariable(u.operand(), wrt);
      case BinaryExpr b -> containsVariable(b.left(), wrt) || containsVariable(b.right(), wrt);
    };
  }

  public static boolean isZero(Expr e) {
    return e instanceof NumberExpr n && n.value() == 0;
  }

  public static boolean isOne(Expr e) {
    return e instanceof NumberExpr n && n.value() == 1;
  }
}
