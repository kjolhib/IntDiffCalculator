import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import CalculusCalculator.ChoiceConstants;
import CalculusCalculator.PrettyPrinter;

/**
 * Testing simplification module.
 */
public class SimplifierTests {
  private static String simplify(String exprString) {
    return new PrettyPrinter().print(TestingHelpers.toAst(exprString, ChoiceConstants.SIMPLIFY));
  }
  
  @Test
  void testBasicAlgebra() {
    assertEquals("12x ^ 2", simplify("3x^2 + 9x ^ 2"));
    assertEquals("27x ^ 4", simplify("3x^2 * 9x ^ 2"));
  }

  @Test
  void testMultipleVars() {
    assertEquals("12x ^ 2 + 26xy", simplify("3x^2 + 24yx + 9x ^ 2 + 2xy"));
  }

  @Test
  void testWrongSideVariable() {
    assertThrows(IllegalArgumentException.class, () -> simplify("x3"));
  }

  @Test
  void testSimpleNumber() {
    assertEquals("3", simplify("3"));
  }

  @Test
  void testSingleDigit() {
    assertEquals("342", simplify("342"));
    assertEquals("-88", simplify("-88"));
    assertEquals("3.4", simplify("3.4"));
  }

  @Test
  void testSimpleVariable() {
    assertEquals("x", simplify("x"));
    assertEquals("42x", simplify("42x"));
    assertEquals("3.4x", simplify("3.4x"));
    assertThrows(IllegalArgumentException.class, () -> simplify("x3"));
  }

  @Test
  void testSimpleAddition() {
    assertEquals("5", simplify("2 + 3"));
    assertEquals("x + 5", simplify("x + 5"));
    assertEquals("5x + 5", simplify("x + 5 + 4x"));
    assertEquals("28.5", simplify("3.4 + 25.1"));
  }

  @Test
  void testSimpleMultiplication() {
    assertEquals("6", simplify("2 * 3"));
    assertEquals("5x", simplify("x * 5"));
    assertEquals("4x ^ 2", simplify("x * 4x"));
    assertEquals("85.34", simplify("3.4 * 25.1"));
  }

  @Test
  void testPrecedence() {
    assertEquals("ax", simplify("xa"));
    assertEquals("4x + 12", simplify("(3+x)*4"));
    assertEquals("4x + 3", simplify("3 + 4 * x"));
    assertEquals("65536", simplify("2^2^4"));
    assertEquals("x ^ 20 + x ^ 2 + 40", simplify("x^2 + 40 + x^(5*4) "));
  }

  @Test
  void testImplicitMultiplication() {
    assertEquals("5x", simplify("(2+3)x"));
    assertEquals("3x + 6", simplify("3(x+2)"));
    assertEquals("6x + 12", simplify("2*3(x+2)"));
  }
}
