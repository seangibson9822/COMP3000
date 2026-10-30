package com.craftinginterpreters.lox;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {

    static boolean hadError = false;

    public static void main(String[] args)
        throws IOException {

        // The interpreter requires:
        //
        // 1. A source file.
        // 2. Rainfall in millimetres.
        //
        // A third optional argument controls
        // how many days should be simulated.
        if (args.length < 2 || args.length > 3) {

            System.out.println(
                "Usage: jlox <file> <rainfall-mm> [days]"
            );

            System.exit(64);
        }

        double rainfallMm;
        int days = 1;

        try {

            // Rainfall is supplied externally rather
            // than being hard-coded into the program.
            rainfallMm =
                Double.parseDouble(args[1]);

            // Existing commands still simulate one day.
            //
            // A third argument enables multi-day
            // simulation when required.
            if (args.length == 3) {
                days =
                    Integer.parseInt(args[2]);
            }

        } catch (NumberFormatException error) {

            System.out.println(
                "Rainfall must be a number and days must be an integer."
            );

            System.exit(64);
            return;
        }

        // A simulation must contain at least one day.
        if (days < 1) {

            System.out.println(
                "Days must be at least 1."
            );

            System.exit(64);
        }

        runFile(
            args[0],
            rainfallMm,
            days
        );
    }

    private static void runFile(
        String path,
        double rainfallMm,
        int days
    ) throws IOException {

        byte[] bytes =
            Files.readAllBytes(
                Paths.get(path)
            );

        run(
            new String(
                bytes,
                StandardCharsets.UTF_8
            ),
            rainfallMm,
            days
        );

        if (hadError) {
            System.exit(65);
        }
    }

    private static void run(
        String source,
        double rainfallMm,
        int days
    ) {

        // Scan the source program once.
        Scanner scanner =
            new Scanner(source);

        List<Token> tokens =
            scanner.scanTokens();

        // Parse the program once into its AST.
        Parser parser =
            new Parser(tokens);

        List<Stmt> statements =
            parser.parseProgram();

        if (hadError) {
            return;
        }

        // Evaluate the same river system once
        // for every day in the simulation.
        //
        // A fresh interpreter is used for each day
        // so variable values are recalculated using
        // that day's flow values.
        for (
            int day = 1;
            day <= days;
            day++
        ) {

            Interpreter interpreter =
                new Interpreter(
                    rainfallMm,
                    day
                );

            interpreter.interpret(
                statements
            );
        }
    }

    static void error(
        int line,
        String message
    ) {

        report(
            line,
            "",
            message
        );
    }

    private static void report(
        int line,
        String where,
        String message
    ) {

        System.err.println(
            "[line "
            + line
            + "] Error"
            + where
            + ": "
            + message
        );

        hadError = true;
    }
}