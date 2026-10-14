package com.craftinginterpreters.lox;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {

    static boolean hadError = false;

    public static void main(String[] args) throws IOException {

        // Assignment 2 takes both a program file and rainfall amount.
        if (args.length != 2) {
            System.out.println("Usage: jlox <file> <rainfall_mm>");
            System.exit(64);
        }

        // Convert the rainfall argument from text into a number.
        double rainfallMm;

        try {
            rainfallMm = Double.parseDouble(args[1]);
        } catch (NumberFormatException error) {
            System.out.println("Rainfall must be a number.");
            System.exit(64);
            return;
        }

        // Run the river program using the supplied rainfall.
        runFile(args[0], rainfallMm);
    }

    private static void runFile(String path, double rainfallMm) throws IOException {

        byte[] bytes = Files.readAllBytes(Paths.get(path));

        // Pass the runtime rainfall value through to the evaluator.
        run(new String(bytes, StandardCharsets.UTF_8), rainfallMm);

        if (hadError) {
            System.exit(65);
        }
    }

    private static void run(String source, double rainfallMm) {

    // Convert the source code into tokens.
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();

    // Parse the tokens into a complete program containing statements.
    Parser parser = new Parser(tokens);
    List<Stmt> statements = parser.parseProgram();

    // Do not attempt evaluation if scanning or parsing failed.
    if (hadError) {
        return;
    }

    // For now we evaluate day 1.
    int currentDay = 1;

    // Execute each statement in the parsed program.
    Interpreter interpreter = new Interpreter(
        rainfallMm,
        currentDay
    );

    interpreter.interpret(statements);
}

    static void error(int line, String message) {

        report(line, "", message);
    }

    private static void report(
        int line,
        String where,
        String message
    ) {

        System.err.println(
            "[line " + line + "] Error"
            + where + ": " + message
        );

        hadError = true;
    }
}