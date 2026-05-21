# Integration & Differentiation Calculator
This project aims to differentiate and integrate given expressions.

This mainly works as a pure differentiator and integrator, with some basic simplifications.

## Limitations
### Simplification
Due to the AST representation of the expression, the `Simplifier` simplifies only nearby terms. It **cannot**:
- Collect like terms across the entire expression,
- Common factors are not cancelled across numerators and denominators,
- Output is usually "unsimplified" for complex expressions, and would require basic algebra simplification.