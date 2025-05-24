package MiniJava.codeGenerator;

import MiniJava.errorHandler.ErrorHandler;

import java.util.Stack;

public class IntegerOperationCodeGenerator {

    private Memory memory;
    private Stack<Address> ss;

    public IntegerOperationCodeGenerator(Memory memory, Stack<Address> ss) {
        this.memory = memory;
        this.ss = ss;
    }

    public void operate(Operation operation, String operationName) {
        memory.modifyLastTempIndex();
        Address temp = new Address(memory.getTemp(), varType.Int);
        Address s2 = ss.pop();
        Address s1 = ss.pop();

        if (s1.varType != varType.Int || s2.varType != varType.Int) {
            ErrorHandler.printError("In "+ operationName +" two operands must be integer");
        }
        memory.add3AddressCode(operation, s1, s2, temp);
        ss.push(temp);
    }
}
