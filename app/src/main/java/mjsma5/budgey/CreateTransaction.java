package mjsma5.budgey;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;


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

    private Stack<String> infix;
    private String result;
    private int parenthesis;
    private boolean reset;

    // NEED TO ADD PREVIEW FUNCTION

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
        findViewById(R.id.btn0).setOnClickListener(this);

        // calculator additional requirements
        txtResult = (TextView) findViewById(R.id.txtResult);
        result = "";
        parenthesis = 0;
        reset = false;

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                if (!result.isEmpty()) {
                    if (result.substring(result.length() - 1).equals(")")) {
                        parenthesis += 1;
                    } else if (result.substring(result.length() - 1).equals("(")) {
                        parenthesis -= 1;
                    } else if (result.substring(result.length() - 1).equals(".")) {
                        decimal = true;
                    }
                    result = result.substring(0, result.length()-1);
                } else {
                    txtResult.setText("0.00");
                }
            case R.id.btnClear:
                result = "0.00";
                txtResult.setText(result);
                break;
            case R.id.btnPercentage:
                result += "%";
                break;
            case R.id.btnOpen:
                parenthesis += 1;
                result += "(";
                break;
            case R.id.btnClose:
                if (!(parenthesis == 0)) {
                    result += ")";
                    parenthesis -= 1;
                } else {
                    // error display "incorrect function"
                }
                break;
            case R.id.btnDiv:
                result += "/";
                break;
            case R.id.btnMult:
                result += "*";
                break;
            case R.id.btnSum:
                result += "+";
                break;
            case R.id.btnMin:
                result += "-";
                break;
            case R.id.btn1:
                result += "1";
                break;
            case R.id.btn2:
                result += "2";
                break;
            case R.id.btn3:
                result += "3";
                break;
            case R.id.btn4:
                result += "4";
                break;
            case R.id.btn5:
                result += "5";
                break;
            case R.id.btn6:
                result += "6";
                break;
            case R.id.btn7:
                result += "7";
                break;
            case R.id.btn8:
                result += "8";
                break;
            case R.id.btn9:
                result += "9";
                break;
            case R.id.btn0:
                result += "0";
                break;
            case R.id.btnDecimal:
                if (decimal) {
                    decimal =  false;
                    result += ".";

                } // else error
            case R.id.btnEquals:
                result = evaluate(result);
                Log.d("EVALUATION_OUTPUT", result);
                txtResult.setText(result);
                break;
        }
        txtResult.setText("$" + result);
    }

    public String evaluate(String infix) {
        // Converts infix string to postfix using Dijkstra's ShuntingYard
        String postfix = ShuntingYard.postfix(infix);
        // Evaluates postfix using the stack method.
        String value = postfixEvaluate(postfix);
        if (value.equals("ERROR")) {
            result = "$0.00";
            txtResult.setText(result);
            return "error"; // insert error reporting
        } else {
            return value;
        }


    }


    /*
     * Evaluate postfix expression
     *
     * @param postfix The postfix expression
    */
    public static String postfixEvaluate(String postfix) {
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
        // Otherwise return
        if (!s.isEmpty())
            return String.valueOf(s.pop());
        else
            return "ERROR";
    }

    /**
     * Check if the character is an operator
     */
    private static boolean isOperator(char ch) {
        return ch == '*' || ch == '/' || ch == '+' || ch == '-';
    }

    
}

