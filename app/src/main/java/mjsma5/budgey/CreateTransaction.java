package mjsma5.budgey;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import java.util.StringTokenizer;


public class CreateTransaction extends AppCompatActivity implements View.OnClickListener {

    private Button btnDate;
    private TextView txtResult;
    private Button btnMethod;
    private EditText txtNote;
    private RadioButton rbExpense;
    private RadioButton rbIncome;
    private RadioButton rbTaxable;
    private Button btnCategory;

    // Calculator
    private Button btnClose;
    private Button btnClear;
    private Button btnPercentage;
    private Button btnOpen;

    private Button btn7;
    private Button btn8;
    private Button btn9;
    private Button btnDiv;

    private Button btn4;
    private Button btn5;
    private Button btn6;
    private Button btnMult;

    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btnMin;

    private Button btnEquals;
    private Button btnDecimal;
    private Button btnSum;
    private Button btn0;

    private String prev;
    private String func;
    private String tmp;
    private Boolean decimal;
    private String preview;

    private String result;
    private int parenthesis;
    private boolean reset;
    private boolean operatorLast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_transaction);

        // Instantiate UI components
        findViewById(R.id.btnOpen).setOnClickListener(this);
        findViewById(R.id.btnClose).setOnClickListener(this);
        findViewById(R.id.btnClear).setOnClickListener(this);
        findViewById(R.id.btnPercentage).setOnClickListener(this);
        findViewById(R.id.btnSum).setOnClickListener(this);
        findViewById(R.id.btnMin).setOnClickListener(this);
        findViewById(R.id.btnMult).setOnClickListener(this);
        findViewById(R.id.btnDiv).setOnClickListener(this);

        findViewById(R.id.btn0).setOnClickListener(this);
        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
        findViewById(R.id.btn3).setOnClickListener(this);
        findViewById(R.id.btn4).setOnClickListener(this);
        findViewById(R.id.btn5).setOnClickListener(this);
        findViewById(R.id.btn6).setOnClickListener(this);
        findViewById(R.id.btn7).setOnClickListener(this);
        findViewById(R.id.btn8).setOnClickListener(this);
        findViewById(R.id.btn9).setOnClickListener(this);

        findViewById(R.id.btnMin).setOnClickListener(this);
        findViewById(R.id.btnEquals).setOnClickListener(this);
        findViewById(R.id.btnDecimal).setOnClickListener(this);
        findViewById(R.id.btnSum).setOnClickListener(this);

        // calculator additional requirements
        txtResult = (TextView) findViewById(R.id.txtResult);
        result = "";
        parenthesis = 0;
        reset = false;
        decimal = false;
        operatorLast = false;

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                if (!result.isEmpty()) {
                    switch (result.substring(result.length() - 1)) {
                        case ")":
                            parenthesis += 1;

                        case "(":
                            parenthesis -= 1;

                        case ".":
                            decimal = false;
                    }
                    result = result.substring(0, result.length() - 1);
                }
            case R.id.btnClear:
                result = "";
                reset = false;
                parenthesis = 0;
                operatorLast = false;
                decimal = false;
                break;
            case R.id.btnPercentage:
                opUpdate("%");
                break;
            case R.id.btnOpen:
                parenthesis += 1;
                opUpdate("(");
                break;
            case R.id.btnClose:
                if (!(parenthesis == 0)) {
                    opUpdate(")");
                    parenthesis -= 1;
                }
                // error display "incorrect function"
                break;
            case R.id.btnDiv:
                opUpdate("/");
                break;
            case R.id.btnMult:
                opUpdate("*");
                break;
            case R.id.btnSum:
                opUpdate("+");
                break;
            case R.id.btnMin:
                opUpdate("-");
                break;
            case R.id.btn1:
                numUpdate("1");
                break;
            case R.id.btn2:
                numUpdate("2");
                break;
            case R.id.btn3:
                numUpdate("3");
                break;
            case R.id.btn4:
                numUpdate("4");
                break;
            case R.id.btn5:
                numUpdate("5");
                break;
            case R.id.btn6:
                numUpdate("6");
                break;
            case R.id.btn7:
                numUpdate("7");
                break;
            case R.id.btn8:
                numUpdate("8");
                break;
            case R.id.btn9:
                numUpdate("9");
                break;
            case R.id.btn0:
                numUpdate("0");
                break;
            case R.id.btnDecimal:
                if (!decimal && !operatorLast) {
                    decimal = true;
                    result += ".";
                    Log.d("INFO", "DECIMAL INPUT");
                } else {
                    Log.d("INPUT ERROR", "DECIMAL ERROR");
                }
                break;

            case R.id.btnEquals:
                if (operatorLast) {
                    result = result.substring(0, result.length() - 1);
                    // if the last token entered was an operator, remove it, then evaluate.
                }
                if (parenthesis == 0) {
                    Log.d("EVALUATION_INPUT", result);
                    result = evaluate(result);
                    Log.d("EVALUATION_OUTPUT", result);
                    reset = true;
                } else {
                Log.d("INPUT ERROR", "PARENTHESIS");
                }
                break;
        }
        txtResult.setText("$" + result);
    }

    public String evaluate(String infix) {
        // Converts infix string to postfix using Dijkstra's ShuntingYard
        String postfix = ShuntingYard.postfix(infix);
        // Evaluates postfix using the stack method.
        String value = postfixEvaluation(postfix);
        if (value.equals("ERROR")) {
            result = "";
            txtResult.setText(result);
            Log.d("INPUT ERROR", "EVALUATION ERROR");
            return "error"; // insert error reporting
        } else {
            return value;
        }
    }

    public void numUpdate(String input) {
        // Update module for all numbers
        if (reset) {
            result = input;
            txtResult.setText(result);
            reset = false;
        } else {
            result += input;
        }
        operatorLast = false;
    }

    public void opUpdate(String input) {
        reset = false;
        if (operatorLast) {
            result = result.substring(0, result.length() -3);
        }
        result += " " + input + " ";
        operatorLast = true;
    }

    public String postfixEvaluation(String postfix) {
        // Use a stack to track all the numbers and temporary results
        Stack<Double> s = new Stack<Double>();

        // Convert expression to char array
        char[] chars = postfix.toCharArray();

        // Cache the length of expression
        int N = chars.length;

        for (int i = 0; i < N; i++) {
            char ch = chars[i];

            if (isOperator(ch)) {
                // Operator,pop out two numbers from stack and perform operation
                switch (ch) {
                    case '+': s.push(s.pop() + s.pop());     break;
                    case '*': s.push(s.pop() * s.pop());     break;
                    case '-': s.push(-s.pop() + s.pop());    break;
                    case '/': s.push(1 / s.pop() * s.pop()); break;
                }
            } else if(Character.isDigit(ch)) {
                // if Number, push to the stack
                s.push(0.0);
                while (Character.isDigit(chars[i]))
                    s.push(10.0 * s.pop() + (chars[i++] - '0'));
            }
        }

        // The final result should be located in the bottom of stack
        // Otherwise return 0.0
        if (!s.isEmpty())
            return String.valueOf(s.pop());
        else
            return String.valueOf("ERROR");
    }

    /**
     * Check if the character is an operator
     */
    private boolean isOperator(char ch) {
        return ch == '*' || ch == '/' || ch == '+' || ch == '-';
    }
}
