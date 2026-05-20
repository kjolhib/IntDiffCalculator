import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import org.junit.jupiter.api.Test;

import IntDiffCalculator.AST.Expr.BinaryExpr;
import IntDiffCalculator.AST.Expr.Expr;
import IntDiffCalculator.AST.Op.BinaryOp;

public class ParserTests {
  @Test
  void testPrecedence() {
    // 2 + 3 * x should be parsed as 2 + (3 * x) and not (2 + 3) * x
    Expr result = TestingHelpers.parse("2 + 3 * x");
    assertInstanceOf(BinaryExpr.class, result);

    BinaryExpr add = (BinaryExpr) result;
    assertEquals(BinaryOp.ADD, add.operator());
    assertInstanceOf(BinaryExpr.class, add.right());
  }
}
