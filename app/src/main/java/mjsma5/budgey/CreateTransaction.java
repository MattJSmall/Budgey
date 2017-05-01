package mjsma5.budgey;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
    private String s;

    // NEED TO ADD PREVIEW FUNCTION

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_transaction);


        // Instantiate UI components
        findViewById(R.id.btnClose).setOnClickListener(this);
        findViewById(R.id.btnClear).setOnClickListener(this);
        findViewById(R.id.btnPercentage).setOnClickListener(this);
        findViewById(R.id.btnOpen).setOnClickListener(this);
        findViewById(R.id.btn7).setOnClickListener(this);
        findViewById(R.id.btn8).setOnClickListener(this);
        findViewById(R.id.btn9).setOnClickListener(this);
        findViewById(R.id.btnDiv).setOnClickListener(this);
        findViewById(R.id.btn4).setOnClickListener(this);
        findViewById(R.id.btn5).setOnClickListener(this);
        findViewById(R.id.btn6).setOnClickListener(this);
        findViewById(R.id.btnMin).setOnClickListener(this);
        findViewById(R.id.btnEquals).setOnClickListener(this);
        findViewById(R.id.btnDecimal).setOnClickListener(this);
        findViewById(R.id.btnSum).setOnClickListener(this);
        findViewById(R.id.btn0).setOnClickListener(this);

        txtResult = (TextView) findViewById(R.id.txtResult);
        infix = new Stack<String>();
        s = "";
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnClear:
                
                break;
            case R.id.btnPercentage:
                infix.push("%");
                break;
            case R.id.btnOpen:
                infix.push("(");
                break;
            case R.id.btnClose:
                infix.push(")");
                break;
            case R.id.btnDiv:
                infix.push("/");
                break;
            case R.id.btnMult:
                infix.push("*");
                break;
            case R.id.btnSum:
                infix.push("+");
                break;
            case R.id.btnMin:
                infix.push("-");
                break;
            case R.id.btn1:
                infix.push("1");
                break;
            case R.id.btn2:
                infix.push("2");
                break;
            case R.id.btn3:
                infix.push("3");
                break;
            case R.id.btn4:
                infix.push("4");
                break;
            case R.id.btn5:
                infix.push("5");
                break;
            case R.id.btn6:
                infix.push("6");
                break;
            case R.id.btn7:
                infix.push("7");
                break;
            case R.id.btn8:
                infix.push("8");
                break;
            case R.id.btn9:
                infix.push("9");
                break;
            case R.id.btn0:
                infix.push("0");
                break;
            case R.id.btnDecimal:
                if (decimal) {
                    infix.push(".");
                } else {
                    // error notification
                }
            case R.id.btnEquals:
                txtResult.setText(String.valueOf(evaluate()));
                break;
        }
        while (!infix.isEmpty()) {
            s += infix.pop();
        }
        txtResult.setText(s);
    }

    public double evaluate() {
        // Dijkstra's Algorithm
        Stack<String> ops = new Stack<String>();
        Stack<Double> vals = new Stack<Double>();

        while (!infix.isEmpty()) {
            String s = infix.pop();
            if (s.equals("(")) ;
            else if (s.equals("+")) ops.push(s);
            else if (s.equals("-")) ops.push(s);
            else if (s.equals("*")) ops.push(s);
            else if (s.equals("/")) ops.push(s);
            else if (s.equals("sqrt")) ops.push(s);
            else if (s.equals(")")) {
                String op = ops.pop();
                double v = vals.pop();
                if (op.equals("+")) v = vals.pop() + v;
                else if (op.equals("-")) v = vals.pop() - v;
                else if (op.equals("*")) v = vals.pop() * v;
                else if (op.equals("/")) v = vals.pop() / v;
                vals.push(v);
            } else vals.push(Double.parseDouble(s));
        }
        return vals.pop();
    }
}

