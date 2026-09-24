# A1 Rubric Explanation

This document is part of your first submission.  Complete it and include it in your submission zip, alongside your parser, example programs.

## How it works

The rubric explanation is the set of questions below.  The first set, the basic questions, is graded directly and is worth 10\% of your marks for this submission.  Answer them accurately to earn those marks.

The remaining sections ask one question for each of the other rubric items.  These are not graded directly, but your answers help your marker award you the marks for each rubric item, so write your answers below each question text in markdown format and point your marker to where the evidence lives in your submission.

## Basic questions (10)

1. Which chapter of the book did you use as the starting point for your solution?

### Your answer

Chapter 4 of *Crafting Interpreters* was used as the starting point of my solution. I then worked through Chapter 5 on AST representation and chapter 6 prior to adapting them for my river language

2. What is the "working folder", and what command(s) compile your parser?

### Your answer

The working foler is the root folder of the project, which contains the 'src', 'Examples', 'Workshops', and 'docs' folders. in addition to files such as teh logbook, rubric and readme

From this folder, I compile the parser and supporting Java files with:

javac -d out src/com/craftinginterpreters/lox/*.java

This compiles the Java source files into the *out* folder


3. What literal in your language represents a river that gets 10L/s of flow on the first day after 1mm of rainfall?

### Your answer

hydro{peak:10, day:1, decay:0/45}


4. What symbol in your language shows two rivers combine, and is it a "unary", "binary", or "literal"?

### Your answer

the symbol '&' is used

for example - two rivers combining at a confluence:
A & B

it is a binary operator as it operates on two river expressios, one of the left, and one on the right

5. Does your language include statements, or is it an expression language?

### Your answer

It is currently an *expression language*

A complete river system is written as a single expression using 'hydro' literals, the '&' operator for confluences, the '>>' operator for downstream flow, and parenthesis for grouping

e.g.,
(A & B) >> C

6. In your language, how long does it take all the water to work through a river system after 1 day of rain?

### Your answer

There is no one exact number of days for all the water to wrok through a river system in my language. Because my language models the flow using exponential decay. the flow continues to decrease at the specified 'decay' rate until it becomes negligible.

The effective time depends on the river's parameters and the amount of rainfall.

For example, 'hydro{peak:10, day:1, decay:0.45} 'produces 10L/s on day 1, then 4.5L/s, 2.025L/s, 0.911L/s, and so on. The effective time for the water to work through the system therefore depends on the decay rate and on what level of additional flow is considered negligible.

The intent behind this was for a more realistic rain effect, as varying rain levels and periods of rain will generate different rates of flow in a real system. In order to determine a set number of days for all the water to make its way through the river system, we would need to establish a minimal flow  - e.g., regular flow without rain, and calculate the length of time the decay would take until baseline is reached.

## Log-book submissions (10)

Which file in the zip are your log-book entries and when did you make them?  Your teacher needs to have seen them during the semester.

### Your answer

My log-book entries are contained in:

Logbook.md

The logbook contains my work from weeks 1-6, with the date and time completion recorded in each entry.

As mentioned prior in the first log, and team aggreement, due to work and family constraints, I am the sole member of my team, and am unable to attend in person classes, thus I have been unable to keep my TA updated.

## Grammar given in the document in Nystrom's notation (20)

Provide the grammar for your language, and how does each of your example programs parse according to it?

### Your answer

The grammar for my language in Nystrom's notation is:


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


a *hydro* literal represents an individual river response. the *&* operator combines two river expressions at a confluence, while *>>* represents the flow continuing downstream into another river seciton.

As conflucence is parsed inside route, & has a higher precedence than >> thus:

Examples/simple_system.lox` has the structure:


A >> B


Each *hydro* literal is parsed as a *primary*. The *route* rule then joins the two river expressions using *>>*.

*Examples/confluence_system.lox* has the structure:


(A & B) >> C


A and B are first parsed as a confluence using the *&* operator. The parentheses group this result, which is then parsed as the left side of a route into C.

*Examples/complex_system.lox* has the structure:


(((A & B) >> C) & D) >> E


A and B first form a confluence. Their combined flow is then routed into C. That result forms another confluence with D, before the complete result is routed into the final downstream river E.


## Three example programs (20)

Provide your three example programs here and identify which files in your zip contain them.
### Your answer

My three example programs are stored in the *Examples* folder.

#### Example 1 — *Examples/simple_system.lox*


hydro{peak:5, day:1, decay:0.50}
>>
hydro{peak:2, day:2, decay:0.60}


This represents one upstream river flowing into a downstream river section.

#### Example 2 — *Examples/confluence_system.lox*


(
    hydro{peak:4, day:1, decay:0.40}
    &
    hydro{peak:3, day:2, decay:0.55}
)
>>
hydro{peak:2, day:1, decay:0.35}


This represents two upstream rivers combining at a confluence before flowing into a downstream river section.

#### Example 3 — *Examples/complex_system.lox*


(
    (
        (
            hydro{peak:6, day:1, decay:0.55}
            &
            hydro{peak:4, day:2, decay:0.60}
        )
        >>
        hydro{peak:3, day:2, decay:0.50}
    )
    &
    hydro{peak:5, day:3, decay:0.70}
)
>>
hydro{peak:2, day:1, decay:0.40}


This represents a larger river system where two headwater rivers first combine and flow into another river section. That result then combines with another tributary before flowing into the final downstream river.


## Parser written in Java based on Lox codebase (20)

Which chapter of the book is your parser based on?  What did you add beyond the Chapter 6 code, and where is that explained?

### Your answer

My parser is based on the Chapter 6 expression parser from *Crafting Interpreters*. I kept the same parsing structure and adapted it to support the grammar of my river language.

Beyone the Chapter 6 Lox parser, I added support for my cusom 'hydro' literal:

hydro{peak:10, day:1, decay:0.45}

This requried additional scanner tokens for *hydro, peak, day, decay* and *:*, as well as a new *hydro* expression type in *Expr.java* to store the tree values.

I also added two operators for representing river systems:

*&*

for combining rivers at a confluence, and:

*>>*

for representing flow continuinig downstream.


to support these operators andtheir precedence, I added the *route()* and *confluence()* parser mehtods. *confluence()* is passed before *route()*, which gives *&* a higher precedence than *>>*.

I also extended *AstPrinter.java* to print my custom *Hydro* expressions so I could verify the structure produced by the parser.

As an additional change, my *parse()* method checks that the parser has reached the end of the input after parsing an expression. This prevents extra unexpected tokens after an otherwise valid river expression from being ignored.

These additions and reasoning behind them are documented in *Logbook.md* and *docs/grammar.md*. teh implementation can be found in:

src/com/craftinginterpreters/lox/Scanner.java
src/com/craftinginterpreters/lox/TokenType.java
src/com/craftinginterpreters/lox/Expr.java
src/com/craftinginterpreters/lox/Parser.java
src/com/craftinginterpreters/lox/AstPrinter.java

## Uniqueness and Creativity (20)

What did you do beyond the in-class work?  Point your marker to where it lives in your submission.

### Your answer

My submission goes beyond the in-class examples by using my own syntax and river modelling approach.

I created a custom river-response literal:

hydro{peak:10, day:1, decay:0.45}

rather than using the literal formats shown in the workshop examples. The named values make it clear what each part represents: *peak* describes the peak flow response, *day* describes when that peak occurs, and *decay* describes how quickly the flow decreases afterwards.

I also use two separate operators to represent different parts of a river system:

*&* 
represents two rivers combining at a confluence, while:

*>>*
represents flow continuing downstream into another river section

I gave *&* a higher precendence than *>>*, so an expression such as:

A & B >> C

is naturally parsed as:
(A & B) >> C

This allows the structure of the river system to be shown directly in the expression without requiring parentheses in every case.

I also extedned the Chapter 6-style parser with my own *Hydro* AST node, custom scanner tokens, *route()* and *confluence()* grammar levels, and end-of-input check so that unexpected tokens after a valid expression are rejected.

The main evidence for these additions can be found in:

docs/grammar.md
Logbook.md
Examples/
src/com/craftinginterpreters/lox/Parser.java
src/com/craftinginterpreters/lox/Expr.java
src/com/craftinginterpreters/lox/Scanner.java
src/com/craftinginterpreters/lox/TokenType.java