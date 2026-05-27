import java.util.List;

import CalculusCalculator.ChoiceConstants;
import CalculusCalculator.Differentiator;
import CalculusCalculator.Lexer;
import CalculusCalculator.Parser;
import CalculusCalculator.Simplifier;
import CalculusCalculator.Token;
import CalculusCalculator.AST.Expr.Expr;
import CalculusCalculator.Integrator;

public class TestingHelpers {
  public static Expr parse(String input) {
    List<Token> tokens = new Lexer(input).tokenise();
    return new Parser().parse(tokens);
  }

  public static Expr toAst(String exprString, ChoiceConstants mode) {
    List<Token> tokens = new Lexer(exprString).tokenise();
    Expr ast = new Parser().parse(tokens);
    Expr resAst;
    switch (mode) {
      case DIFFERENTIATION -> resAst = new Differentiator().differentiate(ast);
      case INTEGRATION -> resAst = new Integrator().integrate(ast);
      case SIMPLIFY -> resAst = ast;
      default -> throw new IllegalArgumentException("An illegal mode was parsed in: " + mode);
    }
    Expr simplifiedAst = new Simplifier().simplify(resAst);
    return simplifiedAst;
  }
}
