import java.util.List;

import IntDiffCalculator.Lexer;
import IntDiffCalculator.Parser;
import IntDiffCalculator.Token;
import IntDiffCalculator.AST.Expr.Expr;

public class TestingHelpers {
  public static Expr parse(String input) {
    List<Token> tokens = new Lexer(input).tokenise();
    return new Parser().parse(tokens);
  }
}
