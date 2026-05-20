package IntDiffCalculator.AST.Expr;

import IntDiffCalculator.AST.Op.BinaryOp;

public record BinaryExpr(BinaryOp operator, Expr left, Expr right) implements Expr {

}
