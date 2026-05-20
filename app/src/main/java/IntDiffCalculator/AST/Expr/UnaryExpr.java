package IntDiffCalculator.AST.Expr;

import IntDiffCalculator.AST.Op.UnaryOp;

/**
 * A unary expression node in the abstract syntax tree.
 * @param operator The unary operator. E.g. sin, cos, ln
 * @param operand The operand of the unary expression.
 */
public record UnaryExpr(UnaryOp operator, Expr operand) implements Expr {

}
