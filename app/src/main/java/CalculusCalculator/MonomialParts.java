package CalculusCalculator;

import java.util.Map;

public record MonomialParts(double coefficient, Map<String, Double> variables) {
  // e.g. 3x ^ 2 y -> coefficient = 3, variables = x, y
  // helps with collecting multiplicative like tmers
}