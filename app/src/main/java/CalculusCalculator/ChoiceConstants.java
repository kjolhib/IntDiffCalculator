package CalculusCalculator;

public enum ChoiceConstants {
  DIFFERENTIATION(1),
  INTEGRATION(2),
  EXIT(3);

  private final int value;

  ChoiceConstants(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static ChoiceConstants fromInt(int value) {
    for (ChoiceConstants c : values()) {
      if (c.value == value) {
        return c;
      }
    }
    throw new IllegalArgumentException("Invalid choice: " + value);
  }
}