package CalculusCalculator.AST.Op;

public enum UnaryOp {
  NEG("-"), // Negation
  SIN("sin"), // Sine
  COS("cos"), // Cosine
  TAN("tan"), // Tangent
  EXP("exp"), // Exponential
  LN("ln");  // Natural Logarithm

  public final String symbol;

  UnaryOp(String symbol) {
    this.symbol = symbol;
  }
}
