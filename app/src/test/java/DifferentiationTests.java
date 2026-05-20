import java.util.List;

import IntDiffCalculator.AST.Expr.Expr;
import IntDiffCalculator.Differentiator;
import IntDiffCalculator.Lexer;
import IntDiffCalculator.Parser;
import IntDiffCalculator.Simplifier;
import IntDiffCalculator.Token;

public class DifferentiationTests {
  private static final Expr toAst(String exprString) {
    List<Token> tokens = new Lexer(exprString).tokenise();
    Expr ast = new Parser().parse(tokens);
    Expr diffAst = new Differentiator().differentiate(ast);
    Expr simplifiedAst = new Simplifier().simplify(diffAst);
    return simplifiedAst;
  }
}
