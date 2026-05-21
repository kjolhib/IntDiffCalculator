package IntDiffCalculator;

import static IntDiffCalculator.AST.Expr.ExprFactory.*;

import IntDiffCalculator.AST.Expr.*;
import IntDiffCalculator.AST.Op.*;

import static IntDiffCalculator.Helpers.*;

/**
 * Integration rules implemented:
 * 1. Sum/Difference
 *  - ∫(f + g)dx = ∫f dx + ∫g dx
 *  - ∫(f - g)dx = ∫f dx - ∫g dx
 * 2. Constant
 *  - ∫n dx = n*x
 * 3. Power
 *  - ∫x^n dx = x^(n+1) / (n+1), where n ≠ -1
 *  - ∫x^-1 dx = ln(x), special case
 * 4. Known Antiderivatives
 *  - ∫sin(x) dx = -cos(x)
 *  - ∫cos(x) dx = sin(x)
 *  - ∫exp(x) dx = exp(x)
 *  - ∫1/x dx    = ln(x)
 * Currently not supported:
 * 1. U-Sub:
 *  - ∫2x * sin(x^2) dx = -cos(x^2)
 * 2. Integration by Parts
 *  - ∫x * sin(x) dx = -x*cos(x) + sin(x)
 */
public class Integrator {
  private static final String INTEGRATE_WRT_VAR = "x"; // hardcoded to be x for now

  public Expr integrate(Expr e) {
    return switch (e) {
      case NumberExpr n -> integrateNum(n);
      case VariableExpr v -> integrateVar(v);
      case BinaryExpr b -> integrateBin(b);
      case UnaryExpr u -> integrateUn(u);
    };
  }

  private Expr integrateNum(NumberExpr n) {
    // ∫(n) = nx
    if (n.value() == 0) {
      return num(0);
    }
    if (n.value() == 1) {
      return var(INTEGRATE_WRT_VAR);
    }
    return mul(num(n.value()), var(INTEGRATE_WRT_VAR));
  }

  private Expr integrateVar(VariableExpr v) {
    // ∫(x) = (1/2)x ^ 2
    if (v.name().equals(INTEGRATE_WRT_VAR)) {
      return div(pow(v, num(2)), num(2));
    }

    // ∫y dx = yx
    return mul(v, var(INTEGRATE_WRT_VAR));
  }

  private Expr integrateBin(BinaryExpr b) {
    // ∫(f(x) <op> g(x))...
    Expr f = b.left();
    Expr g = b.right();
    return switch (b.operator()) {
      case ADD -> add(integrate(f), integrate(g));
      case SUB -> sub(integrate(f), integrate(g));
      case POW -> {
        Double expValue = resolveConstExponent(g);
        if (expValue == null) {
          // TODO: integration by parts etc
          throw new UnsupportedOperationException("Cannot integrate variable exponents just yet. Or this integral is unsolvable through elementary means: " + b);
        }

        if (expValue == -1) {
          //∫ (f ^ (-1)) dx = ln(f)
          yield ln(f);
        }

        // ∫ (f ^ (n)) dx = (f ^ (n + 1)) / (n + 1)
        yield div(
          pow(f, num(expValue + 1)),
          num(expValue + 1)
        );
      }
      case DIV -> {
        // ∫1/x dx = ln(x)
        if (isOne(b.left()) && b.right().equals(var(INTEGRATE_WRT_VAR))) {
          yield ln(var(INTEGRATE_WRT_VAR));
        }

        // ∫1/(x^n) dx = ∫x^(-n) dx: just the power rule
        if (isOne(b.left()) && b.right() instanceof BinaryExpr rb
          && rb.operator() == BinaryOp.POW
          && rb.right() instanceof NumberExpr n
          && !containsVariable(rb.right(), INTEGRATE_WRT_VAR)) {

          yield integrate(pow(rb.left(), num(-n.value())));
        }
        // TODO: integration by parts etc
        throw new UnsupportedOperationException("Cannot integrate division: " + b + ". This may require integration by parts or u-sub, which are both currently unsupported.");
      }
      case MUL -> {
        boolean leftIsConst = !containsVariable(b.left(), INTEGRATE_WRT_VAR);
        boolean rightIsConst = !containsVariable(b.right(), INTEGRATE_WRT_VAR);

        if (leftIsConst) {
          // ∫c*f(x) dx = c * ∫f(x) dx
          yield mul(b.left(), integrate(b.right()));
        }
        if (rightIsConst) {
          // ∫f(x)*c dx = c * ∫f(x) dx
          yield mul(b.right(), integrate(b.left()));
        }
        // TODO: integration by parts etc
        throw new UnsupportedOperationException("Cannot integrate the product of 2 variable expressions: " + b + ". This may require integration by parts or u-sub, which are both currently unsupported.");
      }
      default -> throw new IllegalArgumentException("This expression cannot be integrated: " + b);
    };
  }

  private Expr integrateUn(UnaryExpr u) {
    // For known antiderivatives.
    Expr f = u.operand();

    // currently can only handle simpler cases without u-sub or by parts.
    // composite funcs like sin(x^2) requires u-sub
    if (!f.equals(var(INTEGRATE_WRT_VAR))) {
      throw new UnsupportedOperationException("Cannot integrate composite function: " + u);
    }

    return switch (u.operator()) {
      // Negation is handled before composite function check.
      // Negation is not a function so it always recurses disregarding operands.
      case NEG -> neg(integrate(f)); // ∫-fdx = -∫fdx
      case SIN -> neg(cos(f)); // ∫sin(f) dx = -cos(f)
      case COS -> sin(f); // ∫cos(f) dx = sin(f)
      case TAN -> neg(ln(cos(f))); // ∫tan(f) dx = -ln|cos(f)|. Abs is not handled currently
      case EXP -> exp(f); // ∫exp(f) dx = exp(f)
      case LN -> sub( // ∫ln(x) dx = x * ln(x) - x
        mul(f, ln(f)),
        f
      );
    };
  }

  private Double resolveConstExponent(Expr e) {
    if (e instanceof NumberExpr n) {
      return n.value();
    }

    if (e instanceof UnaryExpr u && u.operator() == UnaryOp.NEG
      && u.operand() instanceof NumberExpr n) {
      return -n.value();
    }
    return null;
  }
}
