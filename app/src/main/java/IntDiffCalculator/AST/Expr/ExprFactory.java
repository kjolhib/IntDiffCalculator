package IntDiffCalculator.AST.Expr;

import IntDiffCalculator.AST.Op.BinaryOp;
import IntDiffCalculator.AST.Op.UnaryOp;

/**
 * A factor that provides methods that generate expressions given their respective inputs.
 */
public class ExprFactory {
  /**
   * Helper method to create a new number expression with the given value.
   * @param value
   * @return NumberExpr: the new number expression with the given value.
   */
  public static NumberExpr num(double x) {
    return new NumberExpr(x);
  }

  /**
   * Helper method to create a new VariableExpr given the identifier.
   * @param ident
   * @return VariableExpr: the new VariableExpr with ident as its name.
   */
  public static VariableExpr var(String ident) {
    return new VariableExpr(ident);
  }

  /**
   * Helper method to create a new addition BinaryExpr with the given operator and left and right expressions.
   * @param left
   * @param right
   * @return BinaryExpr: the new BinaryExpr with the given operator and left and right expressions.
   */
  public static BinaryExpr add(Expr left, Expr right) {
    return new BinaryExpr(BinaryOp.ADD, left, right);
  }
  
  /**
   * Helper method to create a new subtraction BinaryExpr with the given operator and left and right expressions.
   * @param left
   * @param right
   * @return BinaryExpr: the new BinaryExpr with the given operator and left and right expressions.
   */
  public static BinaryExpr sub(Expr left, Expr right) {
    return new BinaryExpr(BinaryOp.SUB, left, right);
  }

  /**
   * Helper method to create a new multiplication BinaryExpr with the given operator and left and right expressions.
   * @param left
   * @param right
   * @return BinaryExpr: the new BinaryExpr with the given operator and left and right expressions.
   */
  public static BinaryExpr mul(Expr left, Expr right) {
    return new BinaryExpr(BinaryOp.MUL, left, right);
  }

  /**
   * Helper method to create a new division BinaryExpr with the given operator and left and right expressions.
   * @param left
   * @param right
   * @return BinaryExpr: the new BinaryExpr with the given operator and left and right expressions.
   */
  public static BinaryExpr div(Expr left, Expr right) {
    return new BinaryExpr(BinaryOp.DIV, left, right);
  }

  /**
   * Helper method to create a new exponential BinaryExpr with the given operator and left and right expressions.
   * @param left
   * @param right
   * @return BinaryExpr: the new BinaryExpr with the given operator and left and right expressions.
   */
  public static BinaryExpr pow(Expr left, Expr right) {
    return new BinaryExpr(BinaryOp.POW, left, right);
  }

  /**
   * Helper method to create a new negation UnaryExpr with the given operator and operand.
   * @param o
   * @return UnaryExpr
   */
  public static UnaryExpr neg(Expr o) {
    return new UnaryExpr(UnaryOp.NEG, o);
  }

  /**
   * Helper method to create a new sin UnaryExpr with the given operator and operand.
   * @param o
   * @return UnaryExpr
   */
  public static UnaryExpr sin(Expr o) {
    return new UnaryExpr(UnaryOp.SIN, o);
  }

  /**
   * Helper method to create a new cos UnaryExpr with the given operator and operand.
   * @param o
   * @return UnaryExpr
   */
  public static UnaryExpr cos(Expr o) {
    return new UnaryExpr(UnaryOp.COS, o);
  }

  /**
   * Helper method to create a new tan UnaryExpr with the given operator and operand.
   * @param o
   * @return UnaryExpr
   */
  public static UnaryExpr tan(Expr o) {
    return new UnaryExpr(UnaryOp.TAN, o);
  }

  /**
   * Helper method to create a new exp UnaryExpr with the given operator and operand.
   * @param o
   * @return UnaryExpr
   */
  public static UnaryExpr exp(Expr o) {
    return new UnaryExpr(UnaryOp.EXP, o);
  }

  /**
   * Helper method to create a new natural log UnaryExpr with the given operator and operand.
   * @param o
   * @return UnaryExpr
   */
  public static UnaryExpr ln(Expr o) {
    return new UnaryExpr(UnaryOp.LN, o);
  }
}
