package MiniJava.codeGenerator;

import MiniJava.scanner.token.Token;

public interface SemanticAction {
    void execute(CodeGenerator codeGenerator, Token token);
}
