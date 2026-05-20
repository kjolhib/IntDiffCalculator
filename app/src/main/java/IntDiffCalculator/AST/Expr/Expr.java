package IntDiffCalculator.app.src.main.java.IntDiffCalculator.AST.Expr;

public sealed interface Expr permits NumberExpr, VariableExpr, BinaryExpr, UnaryExpr {

}
