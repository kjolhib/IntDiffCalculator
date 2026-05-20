package IntDiffCalculator;

import IntDiffCalculator.AST.Expr.BinaryExpr;
import IntDiffCalculator.AST.Expr.Expr;
import static IntDiffCalculator.AST.Expr.ExprFactory.add;
import static IntDiffCalculator.AST.Expr.ExprFactory.cos;
import static IntDiffCalculator.AST.Expr.ExprFactory.div;
import static IntDiffCalculator.AST.Expr.ExprFactory.exp;
import static IntDiffCalculator.AST.Expr.ExprFactory.mul;
import static IntDiffCalculator.AST.Expr.ExprFactory.neg;
import static IntDiffCalculator.AST.Expr.ExprFactory.num;
import static IntDiffCalculator.AST.Expr.ExprFactory.pow;
import static IntDiffCalculator.AST.Expr.ExprFactory.sin;
import static IntDiffCalculator.AST.Expr.ExprFactory.sub;
import IntDiffCalculator.AST.Expr.NumberExpr;
import IntDiffCalculator.AST.Expr.UnaryExpr;
import IntDiffCalculator.AST.Expr.VariableExpr;

/**
 * A differentiator class that takes an AST, and then attempts to differentiate it with respect to a given variable.
 */
public class Differentiator {
  private static final String DIFFERENTIATE_WRT_VAR = "x"; // hardcoded to diff w.r.t. x for now.

  public Expr differentiate(Expr expr) {
    return switch (expr) {
      case NumberExpr n -> diffNum();
      case VariableExpr v -> diffVar(v);
      case BinaryExpr b -> diffBin(b);
      case UnaryExpr u -> diffUn(u);
    };
  }

  private Expr diffNum() {
    // d/dx of a constant is 0.
    return num(0);
  }

  private Expr diffVar(VariableExpr v) {
    // d/dx of x is 1.
    return v.name().equals(DIFFERENTIATE_WRT_VAR) ? num(1) : num(0);
  }

  private Expr diffBin(BinaryExpr b) {
    // d/dx of u + v is du/dx + dv/dx
    return switch (b.operator()) {
      case ADD -> add( // (f + g)' = f' + g'
        differentiate(b.left()), differentiate(b.right())
      );
      case SUB -> sub( // (f - g)' = f' - g'
        differentiate(b.left()), differentiate(b.right())
      );
      case MUL -> add( // (f * g)' = f'g + fg'
        mul(differentiate(b.left()), b.right()), // f'g
        mul(b.left(), differentiate(b.right())) // fg'
      );
      case DIV -> div( // (f / g)' = (f'g - fg') / g^2
        sub (
          mul(differentiate(b.left()), b.right()), // f'g
          mul(b.left(), differentiate(b.right())) // fg'
        ),
        pow(b.right(), num(2)) // g^2
      );
      case POW -> {
        // (f^g)' = g * f^(g - 1) * f' where g is a constant.
        if (!containsVariable(b.left()) && !containsVariable(b.right())) {
          // Num^Num = 0
          yield num(0);
        } else if (!containsVariable(b.right())) {
          // If right variable contains the diff variable, differentiate it 
          yield mul(
            mul(b.right(), pow(b.left(), sub(b.right(), num(1)))), // n * x^(n - 1)
            differentiate(b.left())
          );
        } else if (b.right() instanceof VariableExpr) {
          // Where n is a variable, e.g. x^x. This is more complicated and requires logarithmic differentiation.
          throw new UnsupportedOperationException("Differentiation of variable exponents is not supported yet.");
        } else {
          throw new IllegalArgumentException("Invalid exponent type: " + b.right());
        }
      }
    };
  }

  private Expr diffUn(UnaryExpr u) {
    Expr f = u.operand();
    Expr fPrime = differentiate(f);

    return switch (u.operator()) {
      case NEG -> neg(fPrime); // (-f)' = -f'
      case SIN -> mul( // sin(f)' = cos(f) * f'
        cos(f), // cos(f) is the derivative of sin(f)
        fPrime // f'
      );
      case COS -> mul( // cos(f)' = -sin(f) * f'
        neg(sin(f)), // -sin(f) is the derivative of cos(f)
        fPrime // f'
      );
      case TAN -> mul( // tan(f)' = 1/(cos^2(f)) * f'
        div( // 1/(cos^2(f))
          num(1),
          pow(cos(f), num(2)) // cos^2(f)
        ),
        fPrime
      );
      case EXP -> mul( // exp(f)' = exp(f) * f'
        exp(f), // exp(f) is the derivative of exp(f)
        fPrime // f'
      );
      case LN -> div( // ln(f)' = f' / f
        fPrime, // f' is the derivative of f
        f // f is the original function
      );
    };
  }

  private boolean containsVariable(Expr e) {
    return switch (e) {
      case NumberExpr n -> false;
      case VariableExpr v -> v.name().equals(DIFFERENTIATE_WRT_VAR);
      case UnaryExpr u -> containsVariable(u.operand());
      case BinaryExpr b -> containsVariable(b.left()) || containsVariable(b.right());
    };
  }
}
