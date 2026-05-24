package IntDiffCalculator;

import IntDiffCalculator.AST.Expr.Expr;

public record Term(double coefficient, Expr base) {
  // Coefficient is the number in front of the base, which is the variable.
  // 2x: coefficient = 2, base = x.
}
