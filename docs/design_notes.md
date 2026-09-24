# Language Design Notes

## Water-Flow Literal

### Selected Syntax


hydro{runoff:0.65, peak:2, decay:0.45}


### Parameters

- runoff: proportion of rainfall that becomes river runoff
- peak: day of maximum catchment response
- decay: recession factor after the peak

### Intended Behaviour

The language will model the catchment response over a 10-day period.

For day *d*:


before/at peak: response rises toward the peak
after peak:     response decreases using the decay factor


The response curve will later be normalised so that the available runoff is distributed across the 10-day period.

### Proposed Constraints


0 <= runoff <= 1
1 <= peak <= 10
0 <= decay < 1


These are semantic constraints and do not necessarily need to be enforced by the Submission One parser.

### Alternatives Considered

#### Positional


hydro(0.65, 2, 0.45)


Easy to parse but difficult to read without remembering the parameter order.

#### Symbolic


H{65% ^2 ~0.45}


Compact, but less readable and requires more specialised scanner behaviour.

#### Named Parameters


hydro{runoff:0.65, peak:2, decay:0.45}


Selected because the syntax exposes the meaning of each hydrological parameter directly.




## Week 5 – Combining Rivers

### Confluence Operator

I decided to use *&* to represent two river flows meeting at a confluence.

For example:


hydro{peak:4, day:1, decay:0.40}
&
hydro{peak:2, day:2, decay:0.50}


The *&* operator combines the flow coming from both sides.

For each day:

combined flow = left flow + right flow


I chose *&* instead of *+* because it reads naturally as two rivers joining together and avoids making the river structure look like normal arithmetic.

*&* is a binary operator because it operates on a flow on its left and a flow on its right.

### Downstream Operator

I decided to use *>>* to represent water flowing downstream into another river section.

For example:

A >> B


means that the output from *A* flows into *B*.

The flow leaving *B* is then:


upstream flow + B's own catchment flow


Unlike *&*, direction matters for this operator.

A >> B

does not mean the same thing as:


B >> A


I chose *>>* because it visually shows the direction the water is travelling.

### Combining the Operators

A river system can then be written as:

A & B >> C


This is interpreted as:

(A & B) >> C

The two upstream rivers combine first, and their combined flow then moves downstream into *C*.

Because of this, *&* has a higher precedence than *>>*.

The precedence order is:


Highest:  hydro literals and parentheses
          &
Lowest:   >>


Parentheses can still be used when the structure needs to be made explicit.