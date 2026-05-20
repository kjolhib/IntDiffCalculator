import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import org.junit.jupiter.api.Test;

import IntDiffCalculator.AST.Expr.BinaryExpr;
import IntDiffCalculator.AST.Expr.Expr;
import IntDiffCalculator.AST.Op.BinaryOp;
import IntDiffCalculator.Lexer;
import IntDiffCalculator.Parser;
import IntDiffCalculator.Token;

public class ParserTests {
  private Expr parse(String input) {
    List<Token> tokens = new Lexer(input).tokenise();
    return new Parser().parse(tokens);
  }

  @Test
  void testPrecedence() {
    // 2 + 3 * x should be parsed as 2 + (3 * x) and not (2 + 3) * x
    Expr result = parse("2 + 3 * x");
    assertInstanceOf(BinaryExpr.class, result);

    BinaryExpr add = (BinaryExpr) result;
    assertEquals(BinaryOp.ADD, add.operator());
    assertInstanceOf(BinaryExpr.class, add.right());
  }
}
