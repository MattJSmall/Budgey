package mjsma5.budgey;

/**
 * Created by Matts on 2/05/2017.
 */

import java.util.Stack;

class PostfixEvaluation {
    /*
     * Evaluate postfix expression
     *
     * @param postfix The postfix expression
    */

    public static Double evaluate(String postfix) {
        // Use a stack to track all the numbers and temporary results
        Stack<Double> s = new Stack<Double>();

        // Convert expression to char array
        char[] chars = postfix.toCharArray();

        // Cache the length of expression
        int N = chars.length;

        for (int i = 0; i < N; i++) {
            char ch = chars[i];

            if (isOperator(ch)) {
                // Operator, simply pop out two numbers from stack and perfom operation
                // Notice the order of operands
                switch (ch) {
                    case '+': s.push(s.pop() + s.pop());     break;
                    case '*': s.push(s.pop() * s.pop());     break;
                    case '-': s.push(-s.pop() + s.pop());    break;
                    case '/': s.push(1 / s.pop() * s.pop()); break;
                }
            } else if(Character.isDigit(ch)) {
                // Number, push to the stack
                s.push(0.0);
                while (Character.isDigit(chars[i]))
                    s.push(10.0 * s.pop() + (chars[i++] - '0'));
            }
        }

        // The final result should be located in the bottom of stack
        // Otherwise return 0.0
        if (!s.isEmpty())
            return s.pop();
        else
            return 0.0;
    }

    /**
     * Check if the character is an operator
     */
    private static boolean isOperator(char ch) {
        return ch == '*' || ch == '/' || ch == '+' || ch == '-';
    }
}