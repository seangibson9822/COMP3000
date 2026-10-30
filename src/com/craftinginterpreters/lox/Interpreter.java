package com.craftinginterpreters.lox;

import java.util.List;

// Walks through the parsed syntax tree and calculates
// the resulting river flow for a particular day.
class Interpreter implements Expr.Visitor<Double>, Stmt.Visitor<Void> {

    // Rainfall is supplied when the interpreter is created.
    // This keeps rainfall separate from the river description itself.
    private final double rainfallMm;

    // For now the evaluator works on one selected day.
    // Multi-day evaluation will be added later.
    private final int currentDay;

    // Stores variables declared by the river program.
    private final Environment environment = new Environment();

    Interpreter(double rainfallMm, int currentDay) {
        this.rainfallMm = rainfallMm;
        this.currentDay = currentDay;
    }

    // Execute every statement in the program in order.
    void interpret(List<Stmt> statements) {

        for (Stmt statement : statements) {

            // A null statement can occur after a parser error.
            // Lox normally stops before interpretation when hadError is true,
            // but this protects the interpreter as well.
            if (statement != null) {
                execute(statement);
            }
        }
    }

    // Execute one statement using the statement visitor.
    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    // Evaluate one expression using the expression visitor.
    private double evaluate(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public Double visitHydroExpr(Expr.Hydro expr) {

        // Read the values stored in the hydro AST node.
        double peak = (double) expr.peak;
        double peakDay = (double) expr.day;
        double decay = (double) expr.decay;
        double area = (double) expr.area;

        // Before the configured peak day, this river contributes no flow.
        if (currentDay < peakDay) {
            return 0.0;
        }

        // Rainfall and catchment area scale the peak response.
        double peakFlow = peak * rainfallMm * area;

        // On the peak day, return the complete peak response.
        if (currentDay == peakDay) {
            return peakFlow;
        }

        // Determine how many days have passed since the peak.
        int daysAfterPeak = currentDay - (int) peakDay;

        // Each following day keeps the configured proportion
        // of the previous day's flow.
        return peakFlow * Math.pow(decay, daysAfterPeak);
    }

    @Override
    public Double visitBinaryExpr(Expr.Binary expr) {

        // Evaluate both river expressions first.
        double left = evaluate(expr.left);
        double right = evaluate(expr.right);

        switch (expr.operator.type) {

            // A & B represents two rivers meeting at a confluence.
            case AMPERSAND:
                return left + right;

            // A >> B represents upstream flow entering B.
            case RIGHT_SHIFT:
                return left + right;

            default:
                throw new RuntimeException(
                    "Unknown river operator: " + expr.operator.lexeme
                );
        }
    }

    @Override
    public Double visitGroupingExpr(Expr.Grouping expr) {

        // Parentheses only control grouping.
        return evaluate(expr.expression);
    }

    @Override
    public Double visitLiteralExpr(Expr.Literal expr) {

        if (expr.value instanceof Double) {
            return (Double) expr.value;
        }

        return 0.0;
    }

    @Override
    public Double visitUnaryExpr(Expr.Unary expr) {

        double right = evaluate(expr.right);

        if (expr.operator.type == TokenType.MINUS) {
            return -right;
        }

        return right;
    }

    @Override
    public Double visitVariableExpr(Expr.Variable expr) {

        // Look up the value that was stored when the variable
        // was originally declared.
        return environment.get(expr.name);
    }

    @Override
public Double visitDamExpr(Expr.Dam expr) {

    // First calculate the amount of water entering the dam.
    // This can be a hydro expression, variable, confluence,
    // or another river expression.
    double inflow = evaluate(expr.inflow);

    // Week 11 dams store their management behaviour
    // as an ordered list of release rules.
    if (expr.rules == null) {
        throw new RuntimeException(
            "Dam does not contain any management rules."
        );
    }

    // Check each rule in the same order that
    // the user wrote it in the source program.
    for (Expr.DamRule rule : expr.rules) {

        // The release value represents the proportion
        // of incoming water which leaves the dam.
        double release =
            (double) rule.release;

        // A release fraction must stay between 0 and 1.
        //
        // 0   = release no water
        // 0.5 = release half
        // 1   = release all water
        if (release < 0 || release > 1) {
            throw new RuntimeException(
                "Dam release fraction must be between 0 and 1."
            );
        }

        // "otherwise" is the fallback rule.
        // If none of the earlier conditions matched,
        // use this release fraction.
        if (rule.otherwise) {
            return inflow * release;
        }

        // Work out which value this rule should inspect.
        double sourceValue;

        switch (rule.source.type) {

            // Use today's rainfall supplied to the interpreter.
            case RAIN:
                sourceValue = rainfallMm;
                break;

            // Use the amount of water currently entering the dam.
            case INFLOW:
                sourceValue = inflow;
                break;

            default:
                throw new RuntimeException(
                    "Unknown dam rule source: "
                    + rule.source.lexeme
                );
        }

        double threshold =
            (double) rule.threshold;

        // Test the comparison written in the rule.
        boolean matches;

        switch (rule.operator.type) {

            case GREATER:
                matches =
                    sourceValue > threshold;
                break;

            case GREATER_EQUAL:
                matches =
                    sourceValue >= threshold;
                break;

            case LESS:
                matches =
                    sourceValue < threshold;
                break;

            case LESS_EQUAL:
                matches =
                    sourceValue <= threshold;
                break;

            default:
                throw new RuntimeException(
                    "Unknown dam comparison operator: "
                    + rule.operator.lexeme
                );
        }

        // The first matching rule determines
        // how much water leaves the dam.
        if (matches) {
            return inflow * release;
        }
    }

    // The parser requires an otherwise rule,
    // so reaching here indicates an invalid dam AST.
    throw new RuntimeException(
        "Dam has no matching release rule."
    );
}

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {

        // Evaluate the expression on the right-hand side first.
        double value = evaluate(stmt.initializer);

        // Store the resulting flow under the variable's name.
        environment.define(
            stmt.name.lexeme,
            value
        );

        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {

        // Evaluate the expression being printed.
        double value = evaluate(stmt.expression);

        // Display the flow value for the current simulation day.
        System.out.printf(
            "Day %d flow: %.3f L/s%n",
            currentDay,
            value
        );

        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {

        // Evaluate the expression even though its result
        // is not explicitly printed.
        evaluate(stmt.expression);

        return null;
    }
}