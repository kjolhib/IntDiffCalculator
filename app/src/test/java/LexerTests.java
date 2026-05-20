import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import IntDiffCalculator.Lexer;
import IntDiffCalculator.Token;
import IntDiffCalculator.TokenType;


public class LexerTests {
  private List<Token> tokenise(String input) {
    return new Lexer(input).tokenise();
  }

  private boolean tokenEquals(Token token, TokenType type, String value) {
    return token.type() == type && token.value().equals(value);
  }

  @Test
  void testSimpleNumber() {
    List<Token> tokens = tokenise("3");
    assertTrue(tokenEquals(tokens.get(0), TokenType.NUMBER, "3"));
  }

  @Test
  void testIllegalCharThrows() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> tokenise("#"));
    assertTrue(exception.getMessage().contains("Unexpected character: #"));
  }
}
