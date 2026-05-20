package IntDiffCalculator;

import IntDiffCalculator.AST.Expr.BinaryExpr;
import IntDiffCalculator.AST.Expr.Expr;
import static IntDiffCalculator.AST.Expr.ExprFactory.mul;
import static IntDiffCalculator.AST.Expr.ExprFactory.num;
import static IntDiffCalculator.AST.Expr.ExprFactory.pow;
import IntDiffCalculator.AST.Expr.NumberExpr;
import IntDiffCalculator.AST.Expr.UnaryExpr;
import IntDiffCalculator.AST.Expr.VariableExpr;
import IntDiffCalculator.AST.Op.BinaryOp;
import IntDiffCalculator.AST.Op.UnaryOp;

/**
 * A class for simplifying expressions. This is where we can apply algebraic simplification rules, such as combining like terms, applying distributive property, and more.
 * 
 * <p>
 * An example would be x * 0 in the AST to be simplified to just 0.
 * </p>
 */
public class Simplifier {
  /**
   * Simplifies the AST built by the parser to the simplest form. This is done by collecting terms and reducing where possible, ie. x * 0 becomes 0 and x * 1 becomes 1 etc.
   * <p>
   * The idea is to simplify the children first. The atomic expressions are trivial since they're already the simplest form, and act as the base cases.
   * 
   * Therefore, we use recursion to simplify all children down to their simplest possible forms.
   * </p>
   * @param e
   * @return Expr: simplified expression
   */
  public Expr simplify(Expr e) {
    return switch (e) {
      case NumberExpr n -> n; // numbers don't need simplification
      case VariableExpr v -> v; // singular variables also are simplified already
      case UnaryExpr u -> simplifyUnary(
        new UnaryExpr(u.operator(), simplify(u.operand()))
      );
      case BinaryExpr b -> simplifyBinary(
        new BinaryExpr(b.operator(), simplify(b.left()), simplify(b.right()))
      );
    };
  }

  private Expr simplifyBinary(BinaryExpr b) {
    Expr left = b.left();
    Expr right = b.right();

    return switch (b.operator()) {
      case ADD -> {
        if (isZero(left)) {
          // 0 + x = x
          yield right;
        }
        if (isZero(right)) {
          // x + 0 = x
          yield left;
        }
        if (left instanceof NumberExpr l && right instanceof NumberExpr r) {
          // simplifying numebr additions
          yield num(l.value() + r.value());
        }

        yield b;
      }
      case SUB -> {
        if (isZero(left)) {
          // 0 - x = x
          yield right;
        }
        if (isZero(right)) {
          // x - 0 = x
          yield left;
        }
        if (left instanceof NumberExpr l && right instanceof NumberExpr r) {
          // simplifying number subtractions
          yield num(l.value() - r.value());
        }

        yield b;
      }
      case MUL -> {
        if (isZero(left) || isZero(right)) {
          // 0 * x = 0
          yield num(0);
        }
        if (isOne(left)) {
          // 1 * x = x
          yield right;
        }
        if (isOne(right)) {
          // x * 1= x
          yield left;
        }
        if (left instanceof NumberExpr l && right instanceof NumberExpr r) {
          // simplifying number multiplications
          yield num(l.value() * r.value());
        }

        if (left instanceof NumberExpr l && right instanceof BinaryExpr rb && rb.operator() == BinaryOp.MUL) {
          if (rb.left() instanceof NumberExpr rl) {
            yield mul(num(l.value() * rl.value()), rb.right());
          }
        }

        yield b;
      }
      case DIV -> {
        if (isZero(left)) {
          // 0 / x = 0
          yield num(0);
        }
        if (isZero(right)) {
          // x / 0 = error
          throw new ArithmeticException("Division by zero is undefined!");
        }
        if (isOne(right)) {
          // x / 1 = x
          yield left;
        }

        yield b;
      }
      case POW -> {
        if (isZero(left) && isZero(right)) {
          // 0 ^ 0 = 1
          yield num(1);
        }
        if (isZero(left)) {
          // 0 ^ x = 0
          yield num(0);
        }
        if (isZero(right)) {
          // x ^ 0 = 1
          yield num(1);
        }
        if (isOne(left)) {
          // 1 ^ x = 1
          yield num(1);
        }
        if (isOne(right)) {
          // x ^ 1 = x
          yield left;
        }

        if (left instanceof BinaryExpr l && l.operator() == BinaryOp.POW) {
          if (l.right() instanceof NumberExpr x && right instanceof NumberExpr y) {
            yield pow(l.left(), num(x.value() * y.value()));
          }
        }

        yield b;
      }
      default -> throw new IllegalStateException("Unexpected value: " + (b.operator()));
    };
  }

  private Expr simplifyUnary(UnaryExpr u) {
    Expr operand = u.operand();

    return switch (u.operator()) {
      case NEG -> {
        if (operand instanceof UnaryExpr u1 && u1.operator() == UnaryOp.NEG) {
          yield u1.operand(); // --x = x. double negation
        }
        if (isZero(operand)) {
          yield num(0); // -0 = 0
        }
        
        yield u;
      }
      // TODO: handle some cos(0) and sin(0) etc.
      default -> u; // currently trig and others don't simplify
      
    };
  }

  private boolean isZero(Expr e) {
    return e instanceof NumberExpr n && n.value() == 0;
  }

  private boolean isOne(Expr e) {
    return e instanceof NumberExpr n && n.value() == 1;
  }
}
