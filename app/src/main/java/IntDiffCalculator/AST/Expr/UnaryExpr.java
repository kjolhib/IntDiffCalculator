package IntDiffCalculator.app.src.main.java.IntDiffCalculator.AST.Expr;

import IntDiffCalculator.app.src.main.java.IntDiffCalculator.AST.Op.UnaryOp;

/**
 * A unary expression node in the abstract syntax tree.
 * @param operator The unary operator. E.g. sin, cos, ln
 * @param operand The operand of the unary expression.
 */
record UnaryExpr(UnaryOp operator, Expr operand) implements Expr {

}
