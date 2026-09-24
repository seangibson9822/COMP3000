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