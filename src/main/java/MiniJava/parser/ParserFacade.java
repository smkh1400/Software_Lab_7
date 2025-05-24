package MiniJava.parser;

import MiniJava.Log.Log;
import MiniJava.codeGenerator.CodeGenerator;
import MiniJava.errorHandler.ErrorHandler;
import MiniJava.scanner.lexicalAnalyzer;
import MiniJava.scanner.token.Token;

import java.util.ArrayList;
import java.util.Stack;

public class ParserFacade {

    private Stack<Integer> parsStack;
    private CodeGenerator cg;
    private ArrayList<Rule> rules;
    private ParseTable parseTable;

    public ParserFacade(ParseTable parseTable, ArrayList<Rule> rules) {
        parsStack = new Stack<>();
        parsStack.push(0);
        cg = new CodeGenerator();
        this.parseTable = parseTable;
        this.rules = rules;
    }

    public void startParse(java.util.Scanner sc) {
        lexicalAnalyzer lexicalAnalyzer = new lexicalAnalyzer(sc);
        Token lookAhead = lexicalAnalyzer.getNextToken();

        boolean finish = false;
        Action currentAction;
        while (!finish) {
            try {
                Log.print(lookAhead.toString() + "\t" + parsStack.peek());
                currentAction = parseTable.getActionTable(parsStack.peek(), lookAhead);
                Log.print(currentAction.toString());

                switch (currentAction.action) {
                    case shift:
                        parsStack.push(currentAction.number);
                        lookAhead = lexicalAnalyzer.getNextToken();

                        break;
                    case reduce:
                        Rule rule = rules.get(currentAction.number);
                        for (int i = 0; i < rule.RHS.size(); i++) {
                            parsStack.pop();
                        }

                        Log.print(parsStack.peek() + "\t" + rule.LHS);
                        parsStack.push(parseTable.getGotoTable(parsStack.peek(), rule.LHS));
                        Log.print(parsStack.peek() + "");
                        try {
                            cg.semanticFunction(rule.semanticAction, lookAhead);
                        } catch (Exception e) {
                            Log.print("Code Genetator Error");
                        }
                        break;
                    case accept:
                        finish = true;
                        break;
                }
                Log.print("");
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
        if (!ErrorHandler.hasError)
            cg.printMemory();
    }
}
