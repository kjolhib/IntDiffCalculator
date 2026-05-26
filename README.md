# Integration & Differentiation Calculator
This project aims to differentiate and integrate given expressions.

This mainly works as a pure differentiator and integrator, with some basic simplifications.

## Semantic Notes
The semantic structure and more details of how this calculator parses the inputted expression can be found in the `src/resources` folder, in the `grammar.md` file.

## Limitations
### Simplification
Due to the AST representation of the expression, the `Simplifier` simplifies only nearby terms. 
It **cannoc**:
- Cancel common factors across numerators and denominators.

Known Quirks:
- Negative exponents:
  Currently, negative exponents are displayed as $x \^ -3$, instead of unambiguous $x^(-3)$.
- Negative denominator:
  Similarly to negative exponents, the calculator displays $x \/ -3$ instead of $x \/ (-3)$ or $-(x \/ 3)$.
### Integration
Due to the complexity of by parts and u-sub, I currently have no plans to extend this past simple chain rule and standard integration rules.

As a result, if an expression is recognised (or not recognised at all) as a potential u-sub or by parts, the calculator will throw an `UnsupportedOperationException` detailing any info. But usually this info would just be along the lines "this expression cannot be integrated, as it requires by parts, u-sub, or is overall not solvable".

Specific cases: (more may be added)
- Any integrals that result in absolute values, ie. $\int{\tan (x)}$ would return  $-\ln(\cos(x))$ rather than the strictly correct $-\ln\|\cos(x)\|\quad \forall x \in [-\frac{\pi}{2}, \frac{\pi}{2}]$. This is elaborated further in the below section.
### Absolute Values
This is a fundamental function in maths, and in code I believe it requires some sort of domain implementation.

Actually evaluating absolute values is complex when dealing with domains, which I believe is outside the scope of a simple differentiation/integration calculator (though it is heavily interweaved). I may allow the pretty printer to symbolically represent the absolute values, but not actually implement them.
