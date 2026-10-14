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