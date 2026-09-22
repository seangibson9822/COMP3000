# COMP3000 Project Logbook

## Week 1 – Team Contract

**Activity:** 
- Team Contract
- Repo set up

**Completed:** 22 September 2026 10:54pm

### Work Completed

The Week 1 activity was to create a team contract outlining the team name, team members, expectations, exclusion conditions, and any additional practices that would help the team work effectively.

As I am completing the COMP3000 project individually, I adapted the activity to suit a single-member team.

I selected the team name **Solo Levelling** and identified myself as the sole member. As the only member, I am responsible for all aspects of the project, including language design, implementation, testing, documentation, and submission.

The team contract also established several working practices for the project:

- Break the project into milestones corresponding to the COMP3000 workshop sequence.
- Ensure each milestone produces a tangible result.
- Document important design decisions rather than making decisions only through code.
- Compare alternative designs before selecting an implementation.
- Keep code changes incremental so that problems can be isolated and fixed without unnecessarily rewriting functioning components.
- Maintain accountability through regular logbook entries, Git commits, documented design decisions, working examples and tests, and comparison against the assignment rubric.

Because the team contains only one member, exclusion of another member is not applicable. The contract still records failure to participate, failure to complete agreed work, or preventing progress as conditions that would represent a breakdown in the working agreement.

### Outcome

The team contract was completed and added to the project repository as:

`docs/team_contract.md`

A Git repository was also established for the assignment so that development can be tracked incrementally.

The Week 1 work was committed to the repository after completion.

### Reflection

Although the activity was primarily designed for group projects, adapting it for an individual project was still useful because it established how I intend to manage the assignment. This is required due to personal life circumstances such as family and professional commitments, which render it impossible for me to attend in-person classes, and form a team with my peers.

The most important outcome was deciding to document the reasoning behind language-design decisions rather than only recording the final implementation. This should make it easier to trace how the language develops and justify design choices in the final assignment documentation.

### Next Steps

Work through the Week 2 material on programming languages and the compiler/interpreter pipeline before beginning implementation of the Lox-based language.


## Week 2 – Introduction to Programming Languages

**Completed:** 23 September 2026 12:38am

### Work Completed

This week focused on **little languages**, regular expressions, and the basic compiler/interpreter pipeline.

For the practical exercise, I used **Regexr** with an excerpt from the *Bee Movie* script. I tested several regular expressions:

```regex
Breakfast is ready!
```
Matched an exact phrase.

```regex
Yellow|black
```
Matched either `Yellow` or `black`.

```regex
([A-Za-z])\1
```
Matched repeated letters such as `zz`, `ll`, `ee`, and `oo`.

I also enabled the **global (`g`)** and **multiline (`m`)** flags and used:

```regex
^([A-Z ]+):
```

This identified character names including `BARRY BENSON:`, `JANET BENSON:`, and `BARRY:`.

### Learning Outcomes

The Regexr exercise demonstrated how a small domain-specific language can use compact syntax to represent more complex rules.

I also learned the basic language-processing pipeline:

**Source characters → Scanner → Tokens → Parser → Syntax Tree**

The scanner converts characters into tokens, while the parser uses those tokens to construct a tree representing the program's grammar.

### Reflection

The repeated-character expression showed how a general rule can replace several individual searches. This is relevant to the river-language assignment because good language syntax should express domain concepts clearly without unnecessary repetition.k.

### Next Steps

Week 3 workshop