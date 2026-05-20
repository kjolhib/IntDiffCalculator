package IntDiffCalculator.AST.Op;

public enum BinaryOp {
  ADD("+"),
  SUB("-"),
  MUL("*"),
  DIV("/"),
  POW("^");
  
  public final String symbol;
  
  BinaryOp(String symbol) {
    this.symbol = symbol;
  }
}
