import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;

import IntDiffCalculator.ChoiceConstants;
import IntDiffCalculator.PrettyPrinter;

/**
 * Testing simplification module.
 * Most of the simplification tests is actually already completed in integration/differentiation tests, as a pure byproduct of me not wanting to read the god awful unsimplified, non-canonical, state.
 * <p>
 * Note; I am using differentiation as an input to my test expressions because I have not yet implemented a pure algebra mode (Honestly it's trivial to do at this point but I'm not bothered lol).
 * </p>
 */
public class SimplifierTests {
  private static String simplify(String exprString) {
    // TODO: eventually allow this calculator to simplify algebra
    return new PrettyPrinter().print(TestingHelpers.toAst(exprString, ChoiceConstants.DIFFERENTIATION));
  }
  
  @Test
  void testBasicAlgebra() {
    assertEquals("24x", simplify("3x^2 + 9x ^ 2"));
    assertEquals("3x ^ 2 * 18x + 6x * 9x ^ 2", simplify("3x^2 * 9x ^ 2"));
  }

  @Test
  void testMultipleVars() {
    assertEquals("24x + 26y", simplify("3x^2 + 24yx + 9x ^ 2 + 2xy"));
  }

  /*
  More tests pending... Nearly all simplification cases handled in other integration/differentiation tests.
  I did integration/differentiation first cuz that was the primary function of this calculator, and the simplifier was just a byproduct of seeing how far I can push this.
  */
}
