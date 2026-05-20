package IntDiffCalculator.app.src.main.java.IntDiffCalculator.AST.Expr;

import IntDiffCalculator.app.src.main.java.IntDiffCalculator.AST.Op.BinaryOp;

record BinaryExpr(BinaryOp operator, Expr left, Expr right) implements Expr {

}
