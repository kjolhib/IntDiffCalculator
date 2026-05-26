package CalculusCalculator;

import java.util.ArrayList;
import java.util.List;

import CalculusCalculator.AST.Expr.BinaryExpr;
import CalculusCalculator.AST.Expr.Expr;
import static CalculusCalculator.AST.Expr.ExprFactory.add;
import static CalculusCalculator.AST.Expr.ExprFactory.cos;
import static CalculusCalculator.AST.Expr.ExprFactory.div;
import static CalculusCalculator.AST.Expr.ExprFactory.exp;
import static CalculusCalculator.AST.Expr.ExprFactory.ln;
import static CalculusCalculator.AST.Expr.ExprFactory.mul;
import static CalculusCalculator.AST.Expr.ExprFactory.neg;
import static CalculusCalculator.AST.Expr.ExprFactory.num;
import static CalculusCalculator.AST.Expr.ExprFactory.pow;
import static CalculusCalculator.AST.Expr.ExprFactory.sin;
import static CalculusCalculator.AST.Expr.ExprFactory.sub;
import static CalculusCalculator.AST.Expr.ExprFactory.var;
import CalculusCalculator.AST.Expr.NumberExpr;
import CalculusCalculator.AST.Expr.UnaryExpr;
import CalculusCalculator.AST.Expr.VariableExpr;
import CalculusCalculator.AST.Op.BinaryOp;
import static CalculusCalculator.AST.Op.BinaryOp.MUL;
import CalculusCalculator.AST.Op.UnaryOp;
import static CalculusCalculator.Helpers.containsVariable;
import static CalculusCalculator.Helpers.isOne;

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
 * 5. U-Sub:
 *  - ∫2x * sin(x^2) dx = -cos(x^2)
 * Currently not supported:
 * 1. Integration by Parts
 *  - ∫x * sin(x) dx = -x*cos(x) + sin(x)
 */
public class Integrator {
  private static final String INTEGRATE_WRT_VAR = "x"; // hardcoded to be x for now
  private static final PrettyPrinter printer = new PrettyPrinter();
  private static final Differentiator differentiator = new Differentiator();
  private static final Simplifier simplifier = new Simplifier();

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

        if (!f.equals(var(INTEGRATE_WRT_VAR))) {
          // TODO: u-sub
          // Composite power expressions
          throw new UnsupportedOperationException("Cannot integrate composite power expression: " + b + ". Base is not a bare variable, hence u-sub or by parts may be required.");
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
        Expr numer = b.left();
        Expr deno = b.right();
        
        if (isOne(numer) && deno.equals(var(INTEGRATE_WRT_VAR))) {
          // ∫1/x dx = ln(x)
          yield ln(var(INTEGRATE_WRT_VAR));
        }

        if (isOne(numer) && deno instanceof BinaryExpr rb
          && rb.operator() == BinaryOp.POW
          && rb.right() instanceof NumberExpr n
          && !containsVariable(rb.right(), INTEGRATE_WRT_VAR)
        ) {
          // ∫1/(x^n) dx = ∫x^(-n) dx: just the power rule
          yield integrate(pow(rb.left(), num(-n.value())));
        }

        if (isOne(numer) && deno instanceof UnaryExpr ue
          && ue.operator() == UnaryOp.EXP
        ) {
          // Same as above for 1/(x ^ n), but with exp
          yield integrate(exp(neg(ue.operand())));
        }

        // Try u sub
        Expr result = tryUSubDiv(numer, deno);
        if (result != null) {
          yield result;
        }

        // TODO: integration by parts
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
        
        // Try u substitution
        List<Expr> flattened = flatten(b);
        Expr result = tryUSubstitutionFactors(flattened);
        if (result != null) {
          yield result;
        }

        throw new UnsupportedOperationException("Cannot integrate the product of 2 variable expressions: " + b + ". This may require integration by parts which is currently unsupported.");
      }
      default -> throw new IllegalArgumentException("This expression cannot be integrated: " + b + " by elementary means.");
    };
  }

  private Expr integrateUn(UnaryExpr u) {
    // For known antiderivatives.
    Expr f = u.operand();

    // Negatives
    if (u.operator() == UnaryOp.NEG) {
      return neg(integrate(f)); // ∫-f(x) dx = -∫f(x)dx
    }

    // Base variable
    if (f.equals(var(INTEGRATE_WRT_VAR))) {
      return integrateUnaryBare(u.operator(), f);
    }

    // Linear u-sub: ∫ h(ax + b) dx = 1/a * H(ax + b)
    // This is applied when inner function's dervative is non-zero
    // e.g. exp(-x): f = -x, f' = -1. ∫ = exp(-x)
    Expr fPrime = simplifier.simplify(differentiator.differentiate(f));
    Double constExp = resolveConstExponent(fPrime);

    if (constExp != null && constExp != 0.0) {
      Expr bareAntiderivative = integrateUnaryBare(u.operator(), var(INTEGRATE_WRT_VAR));
      Expr substitutedExpr = substituteExpr(bareAntiderivative, var(INTEGRATE_WRT_VAR), f);
      double k = 1.0 / constExp; // scale

      // pos and negative
      if (Math.abs(k - 1.0) < 1e-10) {
        return substitutedExpr;
      }
      if (Math.abs(k - (-1.0)) < 1e-10) {
        return neg(substitutedExpr);
      }

      return mul(num(k), substitutedExpr);
    }

    throw new UnsupportedOperationException("Cannot integrate composite function: " + u);
  }

  private Expr integrateUnaryBare(UnaryOp op, Expr f) {
    return switch (op) {
      // Negation is handled before composite function check.
      // Negation is not a function so it always recurses disregarding operands.
      case SIN -> neg(cos(f)); // ∫sin(f) dx = -cos(f)
      case COS -> sin(f); // ∫cos(f) dx = sin(f)
      case TAN -> neg(ln(cos(f))); // ∫tan(f) dx = -ln|cos(f)|. Abs is not handled currently
      case EXP -> exp(f); // ∫exp(f) dx = exp(f)
      case LN -> sub( // ∫ln(x) dx = x * ln(x) - x
        mul(f, ln(f)),
        f
      );
      case NEG -> throw new UnknownError("This should not have happened. Negative detected in base variable after negative handler.");
    };
  }

  /**
   * Flattens an expression. 
   * This is used for left associativity issues in integration.
   * <p>
   * For example: sin(x ^ 2) * 2x would be parsed as (sin(x ^ 2) * 2) * x, which the integrator would not be able to recognise as u-sub.
   * </p>
   * @param e the expression to flatten
   * @return List<Expr>, a list of expressions. E.g. {@code sin(x^2) *2x} would be flattened to {@code [sin(x ^ 2), 2, x]}
   */
  private List<Expr> flatten(Expr e) {
    if (e instanceof BinaryExpr b && b.operator() == MUL) {
      List<Expr> result = new ArrayList<>(flatten(b.left()));
      result.addAll(flatten(b.right()));
      return result;
    }
    return new ArrayList<>(List.of(e));
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

  /**
   * Attempt to use u-substitution on an expression.
   * @param factors a list of expressions containing all the factors, e.g. {@code sin(x^2) * 2 * x} becomes {@code [sin(x ^ 2), 2, x]}
   * @return Expr if applicable, null otherwise
   */
  private Expr tryUSubstitutionFactors(List<Expr> factors) {
    // ∫ f(g(x)) * g'(x) dx = F(g(x))
    /* Mimics the following steps:
      1. Identify a function f(x) is a composite function made up of f(g(x)) for osme inner function g(x)
      2. Check that there exists a g'(x) outside f(x), as shown in the above template example. Ie. g'(x) * f(g(x))
      3. Rename inner g(x) -> u
      4. Compute integral of f(u)
      5. Rename u back to g(x)
    */ 

    for (int i = 0; i < factors.size(); i++) {
      Expr candidate = factors.get(i);
      // 1. Extract inner function g(x)
      Expr g = extractInner(candidate);
      if (g == null) {
        continue;
      }

      // Multiply everything except candidate
      List<Expr> rest = new ArrayList<>(factors);
      rest.remove(i);
      Expr other = rest.stream()
        .reduce((a, b) -> new BinaryExpr(BinaryOp.MUL, a, b)) // all are transformed into bin expr mul'ed together
        .orElse(num(1));

      // 2. g'(x)
      Expr gPrime = differentiator.differentiate(g);
      gPrime = simplifier.simplify(gPrime);
      other = simplifier.simplify(other);
      
      // 3. Extract some scalar k, for k * g'. Is the other expression proportional to gPrime?
      Double k = extractScalar(other, gPrime);
      if (k == null) {
        return null;
      }

      // 4. Rename by rewriting inner as u
      // Ie. sin(x^ 3) -> sin(u), where u = x ^ 3
      Expr u = substituteExpr(candidate, g, var(INTEGRATE_WRT_VAR));

      // 5. Integrate f(u)
      Expr integratedF = integrate(u);

      // 6. Sub u back into g(x)
      Expr result = substituteExpr(integratedF, var(INTEGRATE_WRT_VAR), g);

      // Add k back on, ie. scale by k again
      return k == 1.0 ? result : mul(num(k), result);
    }
    
    return null;
  }
  
  /**
   * Extracts the inner g(x) function from the definition found in tryUSubstitution.
   * @param e
   * @return Expr if expression is a recognised composite form, otherwise null.
   */
  private Expr extractInner(Expr e) {
    return switch (e) {
      case UnaryExpr u -> {
        if (!checkPrintEquals(u.operand(), var(INTEGRATE_WRT_VAR))) {
          // sin(g(x)) -> g(x)
          yield u.operand();
        }
        yield null;
      }
      case BinaryExpr b -> {
        if (b.operator() == BinaryOp.POW // we have a power: g(x) ^ n
        && !containsVariable(b.right(), INTEGRATE_WRT_VAR) // not raised to the power
        && !b.left().equals(var(INTEGRATE_WRT_VAR)) // base is WRT integration var
        ) {
          // g(x) ^ n -> g(x)
          yield b.left();
        }
        yield null;
      }
      default -> null;
    };
  }

  /**
   * Helper to check if the left and right expressions are equal after printing.
   * @param left
   * @param right
   * @return
   */
  private boolean checkPrintEquals(Expr left, Expr right) {
    return printer.print(left).equals(printer.print(right));
  }

  /**
   * Finds and returns the scalar k, where a = k * b. 
   * Or null if u-sub doesn't work because k isn't proportional.
   * <p>
   * For example, {@code 6x * sin(x ^ 2)} is intergrate-able using u-sub, if we directly transform it into {@code 3 * 2x * sin(x ^ 2)}.
   * 
   * Hence, we are checking if it is possible to pull out scalars that match the standard u-sub formula.
   * </p>
   * @param a
   * @param b
   * @return Double if possible, else null
   */
  // TODO: may require extension with more complex expressions
  private Double extractScalar(Expr a, Expr b) {
    // Check if they're the same
    if (checkPrintEquals(a, b)) {
      // 2x(sin(x^2))
      return 1.0;
    }

    // Both are constants, so just handle by division
    if (a instanceof NumberExpr na && b instanceof NumberExpr nb) {
      return nb.value() != 0 ? na.value() / nb.value() : null;
    }

    // a = k * b
    if (a instanceof BinaryExpr ab && ab.operator() == BinaryOp.MUL) {
      // 6xsin(x ^ 2), need to check if 6x = k * 2x for some k.
      // If k does not exist, then this is not u-sub differentiable
      if (ab.left() instanceof NumberExpr n
        && checkPrintEquals(ab.right(), b)) {
        return n.value();
      }
      if (ab.right() instanceof NumberExpr n
        && checkPrintEquals(ab.left(), b)) {
        return n.value();
      }

      // Both sides are na * x and nb * x. Return coefficient ratio na / nb,
      if (b instanceof BinaryExpr bb && bb.operator() == BinaryOp.MUL
        && ab.left() instanceof NumberExpr na
        && bb.left() instanceof NumberExpr nb
        && checkPrintEquals(ab.right(), bb.right())
      ) {
        return nb.value() != 0 ? na.value() / nb.value() : null;
      }
    }

    // reverse case,  a = b * k
    if (b instanceof BinaryExpr bb && bb.operator() == BinaryOp.MUL) {
      if (bb.left() instanceof NumberExpr n
        && checkPrintEquals(bb.right(), a)) {
        return 1.0 / n.value();
      }
      if (bb.right() instanceof NumberExpr n
        && checkPrintEquals(bb.left(), a)) {
        return 1.0 / n.value();
      }
    }

    return null;
  }

  /**
   * Recursively substitutes all occurrences of {@code findExpr} with {@code replaceExpr} in the AST rep.
   * <p>
   * E.g. given {@code sin(x ^ 3)}, {@code substituteExpr(sin(x ^ 3), x ^ 3, u)} will attempt to search through {@code sin(x ^ 3)} and replace all instances of {@code x ^ 3} with {@code u}.
   * </p>
   * @param searchExpr the expression we are searching through
   * @param findExpr the expression to find and replace
   * @param replaceExpr the expression we want to replace {@code findExpr} with
   * @return {@code Expr} of the newly subsituted expression containing {@code replaceExpr}, with all occurrences of {@code findExpr} removed.
   */
  private Expr substituteExpr(Expr searchExpr, Expr findExpr, Expr replaceExpr) {
    // Check the expression we are searching through is the same as the expression we're trying to find and replace.
    if (checkPrintEquals(searchExpr, findExpr)) {
      // If so, just return the replace expression.
      return replaceExpr;
    }

    return switch (searchExpr) {
      case UnaryExpr u -> new UnaryExpr(
        u.operator(),
        substituteExpr(u.operand(), findExpr, replaceExpr)
      );
      case BinaryExpr b -> new BinaryExpr(
        b.operator(),
        substituteExpr(b.left(), findExpr, replaceExpr),
        substituteExpr(b.right(), findExpr, replaceExpr)
      );
      default -> searchExpr; // Number, Variable don't have anythign to sub
    };
  }

  /**
   * Attempt u-sub on numerator and denominator.
   * <p>
   * Two cases:
   *  1. ∫ g'(x) / g(x) dx = ln(g(x))
   *  2. ∫ g'(x) / f(g(x)) dx = F(g(x))
   * </p>
   * @param numerator
   * @param denominator
   * @return Expr if u-sub is applicable, null if not
   */
  private Expr tryUSubDiv(Expr numerator, Expr denominator) {
    Expr num = simplifier.simplify(numerator);

    // 1. Denominator is a bare g(x). Check if numerator is in the form k * g'(x)
    // e.g. ∫ 2x / (x^2 + 1). deno: g(x) = x ^ 2 + 1, numer: g'(x) = 2x
    Expr denoPrime = simplifier.simplify(differentiator.differentiate(denominator));
    Double k = extractScalar(num, denoPrime);
    if (k != null) {
      return k == 1.0 ? ln(denominator) : mul(num(k), ln(denominator));
    }

    // 2. Denominator is a composite f(g(x)). So we need to extract g(x)
    // e.g. ∫ 2x / (sin(x^2)). deno: g(x) = x^2, f(g(x)) = sin(x^2), numer: g'(x) = 2x
    //      we'll need to sub u = x^2 and integrate
    Expr g = extractInner(denominator);
    if (g == null) {
      return null;
    }

    Expr gPrime = simplifier.simplify(differentiator.differentiate(g));
    k = extractScalar(num, gPrime);
    if (k == null) {
      return null;
    }

    // Substitute u = g into denominator
    Expr u = substituteExpr(denominator, g, var(INTEGRATE_WRT_VAR));
    // integrate f(u)
    Expr integrated = integrate(div(num(1), u));
    // sub back
    Expr result = substituteExpr(integrated, var(INTEGRATE_WRT_VAR), g);

    return k == 1.0 ? result : mul(num(k), result);
  }
}
