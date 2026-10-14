package com.craftinginterpreters.lox;

// Walks through the parsed expression tree and calculates
// the resulting river flow for a particular day.
class Interpreter implements Expr.Visitor<Double> {

    // Rainfall is supplied when the interpreter is created.
    // This keeps rainfall separate from the river description itself.
    private final double rainfallMm;

    // For Week 7 we evaluate one day at a time.
    // Multi-day evaluation will be added later.
    private final int currentDay;

    Interpreter(double rainfallMm, int currentDay) {
        this.rainfallMm = rainfallMm;
        this.currentDay = currentDay;
    }

    // Start evaluation from the root of the AST.
    // The final value represents the output flow of the river system.
    void interpret(Expr expression) {
        double result = evaluate(expression);

        System.out.printf(
                "Day %d output flow: %.3f L/s%n",
                currentDay,
                result
        );
    }

    // Calls the correct visitor method for the expression type.
    // This is what makes the interpreter a tree-walking evaluator.
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

        // On the peak day, the complete peak flow is produced.
        if (currentDay == peakDay) {
            return peakFlow;
        }

        // Work out how many days have passed since the peak.
        int daysAfterPeak = currentDay - (int) peakDay;

        // After the peak, each day's flow is the configured
        // decay proportion of the previous day's flow.
        return peakFlow * Math.pow(decay, daysAfterPeak);
    }

    @Override
    public Double visitBinaryExpr(Expr.Binary expr) {
        // Evaluate both sides of the binary expression first.
        double left = evaluate(expr.left);
        double right = evaluate(expr.right);

        switch (expr.operator.type) {

            // A & B represents two rivers meeting at a confluence.
            // Their flows are combined.
            case AMPERSAND:
                return left + right;

            // A >> B represents A flowing downstream into B.
            // The upstream flow and B's own contribution are combined.
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
        // Parentheses do not change the value,
        // so evaluate the expression contained inside them.
        return evaluate(expr.expression);
    }

    @Override
    public Double visitLiteralExpr(Expr.Literal expr) {
        // Standard numeric literals are retained from the Lox AST.
        if (expr.value instanceof Double) {
            return (Double) expr.value;
        }

        return 0.0;
    }

    @Override
    public Double visitUnaryExpr(Expr.Unary expr) {
        // Unary expressions are retained from the original Lox AST.
        double right = evaluate(expr.right);

        if (expr.operator.type == TokenType.MINUS) {
            return -right;
        }

        return right;
    }
}