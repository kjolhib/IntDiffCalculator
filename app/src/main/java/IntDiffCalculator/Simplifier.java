package IntDiffCalculator;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import IntDiffCalculator.AST.Expr.BinaryExpr;
import IntDiffCalculator.AST.Expr.Expr;
import static IntDiffCalculator.AST.Expr.ExprFactory.add;
import static IntDiffCalculator.AST.Expr.ExprFactory.mul;
import static IntDiffCalculator.AST.Expr.ExprFactory.neg;
import static IntDiffCalculator.AST.Expr.ExprFactory.num;
import static IntDiffCalculator.AST.Expr.ExprFactory.pow;
import static IntDiffCalculator.AST.Expr.ExprFactory.sub;

import IntDiffCalculator.AST.Expr.NumberExpr;
import IntDiffCalculator.AST.Expr.UnaryExpr;
import IntDiffCalculator.AST.Expr.VariableExpr;
import IntDiffCalculator.AST.Op.BinaryOp;
import static IntDiffCalculator.AST.Op.BinaryOp.ADD;
import static IntDiffCalculator.AST.Op.BinaryOp.MUL;
import static IntDiffCalculator.AST.Op.BinaryOp.SUB;
import IntDiffCalculator.AST.Op.UnaryOp;
import static IntDiffCalculator.Helpers.isOne;
import static IntDiffCalculator.Helpers.isZero;

/**
 * A class for simplifying expressions. This is where we can apply algebraic simplification rules, such as combining like terms, applying distributive property, and more.
 * 
 * <p>
 * An example would be x * 0 in the AST to be simplified to just 0.
 * </p>
 */
public class Simplifier {
  private final PrettyPrinter printer = new PrettyPrinter();
  /**
   * Simplifies the AST built by the parser to the simplest form. This is done by flattening terms and reducing where possible, ie. x * 0 becomes 0 and x * 1 becomes 1 etc.
   * <p>
   * The idea is to simplify the children first. The atomic expressions are trivial since they're already the simplest form, and act as the base cases.
   * 
   * Therefore, we use recursion to simplify all children down to their simplest possible forms.
   * </p>
   * @param e
   * @return Expr: simplified expression
   */
  public Expr simplify(Expr e) {
    while (true) {
      // continually simplify until we cannot anymore
      String oldString = printer.print(e);

      // Simplify locally
      e = localSimplify(e);

      // Collecting like terms
      e = collectTerms(e);

      e = localSimplify(e);
      String newString = printer.print(e);

      if (oldString.equals(newString)) {
        // can't simplify further
        break;
      }
    }

    return e;
  }

  /**
   * Locally simplify, handles simple x * 0, x * 1, etc.
   * @param e
   * @return Expr
   */
  private Expr localSimplify(Expr e) {
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

  /**
   * Flattens the expression into a list.
   * 
   * Allows collectinging like terms and further simplification.
   * @param e the expression to flatten
   * @param sign the sign of the expression, defaulted to +1
   * @return List<Term>: a list of terms flattened out
   */
  private List<Term> flatten(Expr e, double sign) {
    return switch (e) {
      case NumberExpr n -> List.of(new Term(sign * n.value(), num(1)));
      case BinaryExpr b -> {
        if (b.operator() == ADD) {
          yield Stream.concat(
            flatten(b.left(), sign).stream(),
            flatten(b.right(), sign).stream()
          ).toList();
        } else if (b.operator() == SUB) {
          yield Stream.concat(
            flatten(b.left(), sign).stream(),
            flatten(b.right(), -sign).stream()
          ).toList();
        } else if (b.operator() == MUL && b.left() instanceof NumberExpr n) {
          // 3 * x ^ 2: coefficient = 3, base = x^2
          yield flatten(b.right(), sign * n.value()); // extract coefficient
        } else if (b.operator() == MUL &&b.right() instanceof NumberExpr n) {
          // similarly to above, but x ^ 2 * 3. same result
          yield flatten(b.left(), sign * n.value());
        }
        yield List.of(new Term(sign, e));
      }
      case UnaryExpr u -> {
        if (u.operator() == UnaryOp.NEG) {
          // -(x): coefficient = -1, base = x
          yield flatten(u.operand(), -sign);
        }
        yield List.of(new Term(sign, e));
      }
      default -> List.of(new Term(sign, e)); // whole expression becomes the new base
    };
  }

  /**
   * Collects like terms.
   * @param e the expression
   * @return Expr: the new expression in AST format, with like terms collected
   */
  private Expr collectTerms(Expr e) {
    List<Term> terms = flatten(e, 1.0);

    // Group by the like bases
    // Ie. Term1 = 3x
    //     Term2 = 4x
    // This will be filtered together into the form:
    // "x": [Term(3, x), Term(4, x)]
    Map<String, List<Term>> group = terms.stream()
      .collect(Collectors
        .groupingBy(t -> printer.print(t.base()))
      );

    // Sum the coefficients in each grouped base, ignore 0
    List<Term> reducedCoefficients = group.values().stream()
      .map(g -> new Term(
        g.stream().mapToDouble(Term::coefficient).sum(), // sum together all coefficients in the map that have the same base groupings
        g.get(0).base() // the base is the same for each like term that gets summed, just take the 1st one
      ))
      .filter(t -> t.coefficient() != 0) // filter out 0s
      .sorted(Comparator.comparing(t -> printer.print(t.base()))) // makes sure 3x + 4 and 4 + 3x won't cause infinite loops in the simplifier
      .toList();
    
      // Nothing left, ie. everything calcelled out
      if (reducedCoefficients.isEmpty()) {
        return num(0); // x - x = 0
      }

      // Rebuild in the form coefficient * base, appended together using ADD or SUb if negative
      // Ie. ["4x^4", "82x", "42"] becomes folded together into 4x^4 + 82x + 42
      return reducedCoefficients.stream()
        .map(t -> {
          // if the coefficient is 1, ust return the base. Otherwise we multiply base and coefficient. Epsilon check in case of floating point issues. ie. 0.999999999... != 1 causing infinite loops
            if (Math.abs(t.coefficient() - 1.0) < 1e-10) {
              return t.base();
            }
            if (Math.abs(t.coefficient() - (-1.0)) < 1e-10) {
              // -1 * x = -x
              return neg(t.base());
            }
            if (t.base() instanceof NumberExpr n && Math.abs(n.value() - 1.0) < 1e-10) {
              // Handle case where 4 * 1 is not simplified to 4. Causes infinite loops previous
              return num(t.coefficient()); 
            }
            return mul(num(t.coefficient()), t.base());
          }
        )
        .reduce((a, b) -> b instanceof UnaryExpr u && u.operator() == UnaryOp.NEG
          ? sub(a, u.operand()) // a + (-x) is parsed as a - x
          : add(a, b) // anything else just add it
        )
        .get();
  }
}
