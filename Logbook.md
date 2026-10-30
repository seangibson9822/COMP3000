# COMP3000 Project Logbook

## Log 1 – Team Contract

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

docs/team_contract.md

A Git repository was also established for the assignment so that development can be tracked incrementally.

The Week 1 work was committed to the repository after completion.

### Reflection

Although the activity was primarily designed for group projects, adapting it for an individual project was still useful because it established how I intend to manage the assignment. This is required due to personal life circumstances such as family and professional commitments, which render it impossible for me to attend in-person classes, and form a team with my peers.

The most important outcome was deciding to document the reasoning behind language-design decisions rather than only recording the final implementation. This should make it easier to trace how the language develops and justify design choices in the final assignment documentation.

### Next Steps

Work through the Week 2 material on programming languages and the compiler/interpreter pipeline before beginning implementation of the Lox-based language.


## Log 2 – Introduction to Programming Languages

**Completed:** 22 September 2026 11:19pm

### Work Completed

This week focused on **little languages**, regular expressions, and the basic compiler/interpreter pipeline.

For the practical exercise, I used **Regexr** with an excerpt from the *Bee Movie* script. I tested several regular expressions:


Breakfast is ready!

Matched an exact phrase.


Yellow|black

Matched either *Yellow* or *black*


([A-Za-z])\1

Matched repeated letters such as *zz*, *ll*, *ee*, and *oo*.

I also enabled the **global (g)** and **multiline (m)** flags and used:


^([A-Z ]+):


This identified character names including *BARRY BENSON:*, *JANET BENSON:*, and *BARRY:*.

### Learning Outcomes

The Regexr exercise demonstrated how a small domain-specific language can use compact syntax to represent more complex rules.

I also learned the basic language-processing pipeline:

**Source characters → Scanner → Tokens → Parser → Syntax Tree**

The scanner converts characters into tokens, while the parser uses those tokens to construct a tree representing the program's grammar.

### Reflection

The repeated-character expression showed how a general rule can replace several individual searches. This is relevant to the river-language assignment because good language syntax should express domain concepts clearly without unnecessary repetition.k.

### Next Steps

Week 3 workshop

## Log 3 – Lox and Turtle Graphics

**Completed:** 23 September 2026 1:08pm

### Work Completed

This log focused on using **Lox to generate a program written in another language**. I used the supplied *lox.jar* implementation and created a *turtle.lox* file for the workshop 3 exercise.

I first confirmed that Lox was working by running:


print "fd 10";


which generated the Logo command:


fd 10


I then used a Lox loop to generate the instructions for a solid five-point star:


for (var i = 0; i < 5; i = i + 1) {
    print "fd 100";
    print "rt 144";
}

Finally, I used nested loops and the Logo *pd* and *pu* commands to generate a dotted version of the star. The inner loop alternated between drawing 10 units and moving 10 units without drawing:


for (var i = 0; i < 5; i = i + 1) {
    for (var j = 0; j < 100; j = j + 20) {
        print "pd";
        print "fd 10";
        print "pu";
        print "fd 10";
    }

    print "rt 144";
}

I copied the generated Logo instructions into the online Logo interpreter and confirmed that it successfully produced a dotted five-point star.

### What I Learned

The main idea from this workshop was that the output of one program can itself be another program. In this case:

**Lox program → generated Logo code → Logo interpreter → drawing**

This also helped reinforce the difference between the two languages. Commands such as *fd*, *rt*, *pd*, and *pu* belong to Logo, while Lox was being used to generate those commands.

### Reflection

The dotted-star exercise made the idea of code generation much clearer. Instead of manually writing a long Logo program, Lox loops were able to generate the repeated instructions automatically.

The final drawing was not visually identical to the workshop example in terms of dash spacing and turtle position, but the generated program still produced the dotted star.

### Next Steps

Move on to Week 4

## Log 4 – Water-Flow Literal and Scanner

**Completed:** 24 September 2026 1:42pm

### Work Completed

This log I started designing the water-flow literal for my river language.

The main thing I needed to decide was how a river should describe the way rainfall turns into flow over the following days. I considered a few different formats:

hydro(10, 1, 0.45)


This was simple, but it was not obvious what each number represented.

hydro{peak:10, day:1, decay:0.45}


This was slightly longer, but the meaning of each value was much clearer.


H{10 ^1 ~0.45}


This was more compact, but I thought it would be harder to understand and would add extra symbols to the language without much benefit.

I decided to use:


hydro{peak:10, day:1, decay:0.45}


The values represent:

- peak – the highest flow produced after rainfall
- day – the day when the flow reaches its peak
- decay – how much of the previous day's flow remains after the peak

The idea is that the daily flow can later be calculated using a formula instead of manually entering a value for every day.

For example, if:


peak = 10
decay = 0.45


then after the peak the flow would reduce like:


10 -> 4.5 -> 2.025 -> 0.911...


### Scanner Implementation

As I had not previously completed the Lox scanner from the textbook, I first created the basic scanner files:

- TokenType.java
- Token.java
- Scanner.java
- Lox.java

I first tested the normal Lox scanner using:


var river = 10 + 2.5;
print river;


The scanner correctly identified the keywords, identifiers, numbers and symbols.

I then added the new tokens needed for my river literal:

HYDRO
PEAK
DAY
DECAY
COLON


I tested the new syntax using:


hydro{peak:10, day:1, decay:0.45}


The scanner produced:


HYDRO hydro null
LEFT_BRACE { null
PEAK peak null
COLON : null
NUMBER 10 10.0
COMMA , null
DAY day null
COLON : null
NUMBER 1 1.0
COMMA , null
DECAY decay null
COLON : null
NUMBER 0.45 0.45
RIGHT_BRACE } null
EOF null


This confirmed that the scanner can now recognise all parts of the custom water-flow literal.

### Reflection

I chose the named parameter format because it is easy to tell what each value represents just by looking at it. Although it is slightly longer than some of the other designs, I think it is easier to understand.

I also liked the idea of using a mathematical model rather than manually entering the flow for every day. The *peak*, *day* and *decay* values can later be used to calculate how the flow changes over the 10-day period.

As I completed this workshop retrospectively, I was not able to compare my design with another team during class. Instead, I compared it with the different literal designs provided in the workshop material.

### Next Steps

Week 5 workshop


## Log 5 – Combining Rivers and Expression Trees

**Completed:** 24 September 2026 2:23pm

### Work Completed

This log I worked on how multiple rivers should be combined to describe a full river system.

I decided to use two different operators:

&

represents two rivers meeting at a confluence.

>>

represents water flowing downstream into another river section.

For example:

A & B >> C

is interpreted as:

(A & B) >> C


This means rivers A and B combine first, then their combined flow moves downstream into C.

I chose *&* instead of *+* because I thought it better represented two rivers joining together rather than normal arithmetic. I chose *>>* because it visually shows the direction that the water is moving.

I also decided that *&* should have higher precedence than *>>*. Parentheses can still be used when I want to make the river structure clearer.

### Expression Trees

I looked at how these expressions can be represented as trees.

For:


(A & B) >> C


the tree is:


        >>
       /  \
      &    C
     / \
    A   B


The tree would be evaluated from the bottom up. A and B would first calculate their own flow values. Their flows would then be combined by `&`. The result would then flow downstream into C, where C's own catchment flow would also be added.

This helped me see how the structure of the expression can represent the actual structure of the river system.

### Grammar

I created an initial grammar for the language:


expression  → route ;

route       → confluence ( ">>" confluence )* ;

confluence  → primary ( "&" primary )* ;

primary     → hydro
            | "(" expression ")" ;

hydro       → "hydro" "{"
              "peak" ":" NUMBER ","
              "day" ":" NUMBER ","
              "decay" ":" NUMBER
              "}" ;


This grammar gives *&* higher precedence than *>>* and also allows parentheses to control how rivers are grouped.

I also created several example river expressions ranging from a single catchment to multiple rivers combining and flowing downstream.

### Reflection

I originally considered using normal arithmetic operators such as *+*, but I preferred having separate symbols for rivers combining and rivers flowing downstream.

Using *&* and *>>* makes the expression show more of the river structure rather than making it look like a normal mathematical equation.

As I completed this workshop retrospectively, I used the examples and discussion in the supplied workshop material to compare different ways of representing river combinations.

### Next Steps

For Week 6, I will use this grammar to start building the actual parser in Java. This will require adding the new operators to the scanner, creating the AST expression types and then implementing the parsing rules.


## Log 6 – Parser Implementation 

**Completed:** 24 September 2026 5:30pm
### Work Completed

This log I implemented the parser for the river expressions I designed in log 5.

I first updated the scanner so it could recognise the two operators I added to the language:


&   -> AMPERSAND
>>  -> RIGHT_SHIFT


I then created the AST classes used by the parser. I added a custom *Hydro* expression which stores:


peak
day
decay


For example:


hydro{peak:10, day:1, decay:0.45}

is stored as one *Hydro* expression containing those three values.

The existing binary expression structure is used for both of my river operators because they both have a left expression, an operator and a right expression.

### Parser

I implemented the parser based on the grammar from Week 5:


expression  → route ;

route       → confluence ( ">>" confluence )* ;

confluence  → primary ( "&" primary )* ;

primary     → hydro
            | "(" expression ")" ;

hydro       → "hydro" "{"
              "peak" ":" NUMBER ","
              "day" ":" NUMBER ","
              "decay" ":" NUMBER
              "}" ;


Each grammar rule is represented by a corresponding method in *Parser.java*.

Having separate *route()* and *confluence()* methods also controls the operator precedence. *&* is parsed before *>>*, so:

A & B >> C

is interpreted as:

(A & B) >> C


### Testing

I created several tests to check different parts of the parser.

The main parser test:


hydro{peak:4, day:1, decay:0.40}
&
hydro{peak:2, day:2, decay:0.50}
>>
hydro{peak:3, day:1, decay:0.30}


produced:


(>> (& hydro{peak:4.0, day:1.0, decay:0.4} hydro{peak:2.0, day:2.0, decay:0.5}) hydro{peak:3.0, day:1.0, decay:0.3})


This showed that *&* was being evaluated before *>>*.

I also tested parentheses:


A & (B >> C)


which produced a different tree and confirmed that parentheses could override the normal precedence.

I tested multiple confluences:


A & B & C


which were parsed correctly as nested binary expressions.

Finally, I tested invalid syntax by removing the comma after the *day* value:


hydro{peak:4, day:1 decay:0.40}


The parser rejected it with:


[line 1] Error: Expect ',' after day value.


### Reflection

This log helped connect the grammar from log 5 to how the parser actually works. The grammar rules translate quite directly into Java methods, and the order of those methods controls how the operators are grouped.

Testing the printed AST also made it easier to confirm that the river structure was being parsed the way I intended rather than just checking that the code compiled.

### Next Steps

The parser now supports my custom *hydro* literal, river confluences using *&*, downstream flow using *>>*, and parentheses.

The next step will be to continue building the language toward the requirements for Submission One and create the final example programs and rubric explanation.


## Submission One Finalisation – *7:55pm 24 September 2026*


### Work completed

I completed the final version of my Submission One parser and tested it from a clean build by deleting the existing `out` folder and recompiling all Java source files.

I then ran all three final example programs:

- Examples/simple_system.lox
- Examples/confluence_system.lox
- Examples/complex_system.lox

All three programs parsed successfully and produced the expected AST structure.

I also completed *a1_rubric.md*, documenting the grammar, example programs, parser changes and the unique features of my language.

### Final language design

My river language uses the literal:


hydro{peak:10, day:1, decay:0.45}


where:

- *peak* represents the peak flow response per 1mm of rainfall.
- *day* represents the day on which that peak occurs.
- *decay* represents the fixed proportion of the previous day's flow that remains on each following day.

For example, a peak of 10 with a decay of 0.45 produces approximately:


10 -> 4.5 -> 2.025 -> 0.911 -> ...


The *&* operator represents two rivers combining at a confluence, while *>>* represents flow continuing downstream. *&* has a higher precedence than *>>*.

### Design clarification

While completing the rubric I considered how long it takes for water to completely work through the system. With exponential decay, the calculated flow approaches zero but never mathematically reaches exactly zero.

The current language therefore does not define an exact final day or a minimum flow threshold at which the remaining flow is treated as zero. This is an evaluation detail that would need to be defined when the language is extended with actual flow evaluation.

Submission One currently parses and represents these values but does not yet calculate the resulting daily flows.

### Final status

The parser now supports:

- *hydro* river-response literals
- confluences using *&*
- downstream routing using *>>*
- operator precedence
- parentheses and nested river systems
- syntax error reporting
- full-expression validation

The grammar, parser and three final example programs are now working together successfully.

## Log 7 – Evaluating Expressions
**Date completed:** 14 October 12:35pm 2026  


### Workshop focus

This week focused on extending the expression language from Assignment 1 with a tree-walking evaluator. Previously my language could scan and parse river expressions into an AST, but it did not calculate any actual river flow values.

### Design decisions

I decided to provide rainfall as a command-line argument rather than hard-coding it into the interpreter. This keeps the rainfall event separate from the river-system description and allows the same `.lox` program to be tested with different amounts of rainfall.

For example:

Powershell:
java -cp out com.craftinginterpreters.lox.Lox ".\Examples\area_test.lox" 2


runs the program using 2mm of rainfall.

I also extended the existing `hydro` literal with an optional catchment area:
hydro{peak:10, day:1, decay:0.45, area:2.5}

If `area` is not provided, it defaults to `1.0`, which means my Assignment 1 programs remain valid.

The peak flow is currently calculated as:
peak flow = peak × rainfall × area


### Implementation

I added `AREA` as a new token and updated the scanner and parser to recognise the optional `area` parameter. The `Hydro` AST node was also extended to store the catchment area.

I then created `Interpreter.java`, which implements `Expr.Visitor<Double>`. The interpreter walks the AST and evaluates each expression rather than simply printing its structure.

The evaluator currently supports:

- `hydro` expressions, which calculate a river's flow for the selected day.
- `&`, which combines the flow from two rivers at a confluence.
- `>>`, which combines upstream flow with the downstream river's own contribution.
- Parenthesised expressions.

For Week 7 I evaluate one day at a time, with `currentDay` currently set to day 1. Multi-day evaluation will be added later.

### Testing

I tested:
hydro{peak:10, day:1, decay:0.45, area:2.5}


With 1mm rainfall the interpreter produced:


Day 1 output flow: 25.000 L/s


With 2mm rainfall it produced:


Day 1 output flow: 50.000 L/s


I also retested my Assignment 1 examples. `simple_system.lox` produced `5.000 L/s` on day 1 and `confluence_system.lox` produced `6.000 L/s`, confirming that the existing `&`, `>>`, grouping and default area behaviour still work with the evaluator.

### Reflection

The main change this week was moving from representing a river system to actually giving the AST meaning through evaluation. Keeping rainfall as runtime input makes the river description reusable, while adding catchment area allows different rivers to respond differently to the same rainfall event.

The evaluator is still limited to a single selected day. The next stage will be extending this approach so that flows can be calculated and carried over across multiple days.


## Log 8 – Variables and Tree-Walking Evaluation
**Date completed:** 14 October  3:41pm 2026  


### Workshop focus
This workshop extended the river language from evaluating a single expression to supporting programs containing variables and statements. The main goal was to allow river expressions to be stored under variable names, reused in later expressions, and printed using an explicit `print(...)` statement.


### Parser changes
I extended the parser to support multiple statements through a new `parseProgram()` method.

The language now supports variable declarations such as:


var creek = hydro{peak:5, day:1, decay:0.5};

and print statements such as:

print(creek);


Identifiers can also now appear inside river expressions. For example:

var combined = creek & tributary;


The existing river operators `&` and `>>` continue to use the precedence rules developed for Assignment 1.

### Statement and variable representation
I added a `Stmt` AST hierarchy containing:

- `Stmt.Var` for variable declarations
- `Stmt.Print` for explicit output
- `Stmt.Expression` for normal expression statements

I also added `Expr.Variable` so that references to previously declared variables can be represented in the expression tree.

### Variable evaluation
I added an `Environment` class that maps variable names to their evaluated flow values.

When a variable is declared, the interpreter evaluates its river expression and stores the result. When the variable is later referenced, the interpreter retrieves the stored value from the environment.

The interpreter was extended to walk both expression and statement nodes.

### Testing
I created `variables_test.lox` containing multiple named river sections:


var creek =
    hydro{peak:5, day:1, decay:0.5};

var tributary =
    hydro{peak:3, day:1, decay:0.4};

var confluence =
    creek & tributary;

var output =
    confluence >>
    hydro{peak:2, day:1, decay:0.6};

print(output);


With rainfall of `1 mm`, the program produced:


Day 1 flow: 10.000 L/s


With rainfall of `2 mm`, it produced:

Day 1 flow: 20.000 L/s


This confirmed that variables are stored and retrieved correctly and that rainfall continues to affect the resulting river flow.

### Reflection
This change made the language much easier to read because complex river systems no longer need to be written as one large nested expression. Individual river sections can now be named and combined later.

It also changed the interpreter from evaluating one expression into executing a sequence of program statements. This provides a better foundation for the later Assignment 2 requirements, including multi-day evaluation and dams.


## Log 9 – Dam Syntax and Parsing
**Date completed:** 14 October 4:34pm 2026  

### Workshop focus
The Week 10 workshop introduced dams as the motivation for control-flow-style behaviour in the river language. Rather than implementing general-purpose loops or conditions, the task was to design syntax that could represent a human-controlled dam and extend the parser to recognise it.


### Dam language design
I designed a domain-specific dam expression rather than adding general-purpose `if` and `else` statements.

An example is:


var reservoir =
    dam(creek) {
        capacity:100,
        initial:40,
        low:30,
        high:80,
        rain:20,
        lowRelease:0,
        normalRelease:0.5,
        highRelease:1
    };


The expression inside `dam(...)` represents the incoming river flow.

The dam also stores configuration values for its capacity, initial water level, low and high water thresholds, rainfall threshold, and three release fractions.

The release fractions are intended to represent different operating conditions:

- `lowRelease` – release when the dam level is low
- `normalRelease` – release during normal conditions
- `highRelease` – release during high-water or heavy-rain conditions

The actual release algorithm is deliberately not implemented yet because Week 10 only requires the syntax and parser design.

### Implementation
I added dam-related keyword tokens to the scanner and created a new `Expr.Dam` AST node.

The parser was extended so `dam(...) { ... }` can appear anywhere a normal river expression can appear. The parsed node stores the incoming flow expression together with all dam configuration values.

`AstPrinter` and `Interpreter` were also updated with visitor methods for the new AST node. The interpreter method currently throws an intentional runtime exception because dam evaluation will be implemented later.

### Testing
I created `dam_test.lox` containing a river variable and a dam using that river as its inflow.

Running the program successfully passed scanning and parsing and reached the interpreter, which produced:

Dam evaluation has not been implemented yet.


This confirms that the dam syntax is recognised, parsed, stored as an `Expr.Dam`, and passed correctly to the interpreter.

### Reflection
Using a dedicated `dam` expression keeps the language focused on the river-modelling domain instead of adding general-purpose control flow that is not currently needed.

The parameters also provide a clear foundation for later dam evaluation because the dam can eventually base its release behaviour on stored water level, rainfall, and incoming river flow.


## Log 10 – Dam Semantics and Management Rules

**Date completed:** 30 October 2026  1:50pm


### Workshop focus

This week focused on adding behaviour to dams. Unlike a normal river connection, a dam is controlled by a management strategy which determines how much of the incoming water should be released.

The workshop required three versions of the same dam:

- always block half of the incoming water;
- block half unless rainfall is greater than 10 mm;
- block half unless inflow is greater than 10 L/s, where it instead blocks 75%.

### Dam rule syntax

I extended the Week 10 dam design into a rule-based structure.

A dam still takes an incoming river expression:

dam(creek) {
    ...
}


Inside the dam, release rules describe its management strategy.

For example:

dam(creek) {
    release 1 when rain > 10;
    release 0.5 otherwise;
}


`release` represents the proportion of incoming water that leaves the dam. This means:

- `release 1` releases all incoming water;
- `release 0.5` releases half;
- `release 0.25` releases 25% and therefore blocks 75%.

I used this form instead of adding general `if` and `else` statements because it keeps the syntax specific to the water-management language.

### Scanner changes

I added four new keywords:

when
otherwise
release
inflow

`rain` was already a keyword from the previous dam implementation.

The existing comparison tokens such as `>`, `>=`, `<` and `<=` can now be used inside dam rules.

### AST changes

I added a `DamRule` representation to `Expr.java`.

Each rule stores:

- the source being tested (`rain` or `inflow`);
- the comparison operator;
- the comparison threshold;
- the release fraction;
- whether the rule is the final `otherwise` rule.

The `Dam` expression now also stores a list of these rules in their original order.

### Parser changes

The parser now accepts dam rules with the following general structure:

release NUMBER when rain COMPARISON NUMBER;
release NUMBER when inflow COMPARISON NUMBER;
release NUMBER otherwise;

Every dam must contain at least one rule and must finish with an `otherwise` rule. This ensures that the dam always has a defined release behaviour.

Rules are stored in order so that the interpreter can later evaluate them from top to bottom.

### Example programs

I created three separate Week 11 examples.

#### `dam_half.lox`

dam(creek) {
    release 0.5 otherwise;
}


This represents a dam which always blocks half of its incoming water.

#### `dam_rain.lox`

dam(creek) {
    release 1 when rain > 10;
    release 0.5 otherwise;
}

This normally releases half of the incoming water, but releases all of it when rainfall is greater than 10 mm.

#### `dam_inflow.lox`

dam(creek) {
    release 0.25 when inflow > 10;
    release 0.5 otherwise;
}

This normally releases half of the incoming water. When inflow is greater than 10 L/s it releases only 25%, meaning that 75% is blocked.

### Testing

I first recompiled the interpreter:

Remove-Item -Recurse -Force out -ErrorAction SilentlyContinue
javac -d out src/com/craftinginterpreters/lox/*.java

I then reran the Week 9 variable test to check that the new parser changes had not broken the previous implementation:

Day 1 flow: 10.000 L/s

All three new dam programs also successfully passed scanning and parsing.

They currently reach:

Dam evaluation has not been implemented yet.


This is expected because `visitDamExpr()` is still deliberately acting as a placeholder. The dam-management syntax and AST representation are now complete, but evaluating the rules will be implemented as part of the final Assignment 2 work.

### Reflection

The new rule-based approach is more flexible than the fixed Week 10 dam parameters. Different management strategies can now be represented using the same syntax instead of requiring a new field for every possible behaviour.

It also gives a clear path toward the final Assignment 2 requirements, because the interpreter can later evaluate rules based on rainfall and incoming flow before determining the downstream output.

## Log 11 – Dam Evaluation

**Date completed:** 30 October 2026 2:30pm

### Dam evaluation

I extended the interpreter so the rule-based dam syntax created in Week 11 can now be executed.

The interpreter first evaluates the river expression entering the dam, then checks each management rule in the same order it appears in the source program.

For example:

```lox
dam(creek) {
    release 1 when rain > 10;
    release 0.5 otherwise;
}
```

The interpreter checks the current rainfall. If it is greater than 10 mm, all of the incoming water is released. Otherwise, half of the incoming flow is released.

The same rule system can inspect either `rain` or `inflow`.

### Release fractions

Release values represent the proportion of incoming water which leaves the dam:

```text
0    = no water released
0.5  = half released
1    = all water released
```

The interpreter also checks that release values stay between `0` and `1`. Values outside this range are treated as invalid dam behaviour.

### Testing

I tested the three dam programs created in Week 11.

`dam_half.lox` with rainfall `1`:

```text
Day 1 flow: 5.000 L/s
```

`dam_rain.lox` with rainfall `15`:

```text
Day 1 flow: 150.000 L/s
```

The same program with rainfall `5` used the fallback rule:

```text
Day 1 flow: 25.000 L/s
```

`dam_inflow.lox` with rainfall `2`:

```text
Day 1 flow: 5.000 L/s
```

I also reran the previous variable test:

```text
Day 1 flow: 10.000 L/s
```

This confirmed that adding dam evaluation did not break the existing river and variable evaluation.

### Reflection

The dam system is now able to make runtime decisions based on both rainfall and incoming flow. This gives the language behaviour closer to the original water-management goal, because the same dam can respond differently depending on current conditions.

The next major task is multi-day evaluation so flows can carry over from previous days instead of only evaluating one day at a time.