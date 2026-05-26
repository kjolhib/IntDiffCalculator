package CalculusCalculator.AST.Expr;

import CalculusCalculator.AST.Op.BinaryOp;

public record BinaryExpr(BinaryOp operator, Expr left, Expr right) implements Expr {

}
