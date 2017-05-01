package mjsma5.budgey;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.Objects;


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
    private String curr;
    private String func;
    private String tmp;
    private Boolean decimal;
    private String preview;

    private Stack stack;
    private boolean test;

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

        stack = new Stack();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnClose:
                evaluate(")");
                break;
            case R.id.btnClear:
                evaluate("c");
                break;
            case R.id.btnPercentage:
                evaluate("%");
                break;
            case R.id.btnOpen:
                evaluate("(");
                break;
            case R.id.btn7:
                evaluate("7");
                break;
            case R.id.btn8:
                evaluate("8");
                break;
            case R.id.btn9:
                evaluate("9");
                break;
            case R.id.btnDiv:
                evaluate("/");
                break;
            case R.id.btn4:
                evaluate("4");
                break;
            case R.id.btn5:
                evaluate("5");
                break;
            case R.id.btn6:
                evaluate("6");
                break;
            case R.id.btnMult:
                evaluate("*");
                break;
            case R.id.btn1:
                evaluate("1");
                break;
            case R.id.btn2:
                evaluate("2");
                break;
            case R.id.btn3:
                evaluate("3");
                break;
            case R.id.btnMin:
                evaluate("-");
                break;
            case R.id.btn0:
                if (Objects.equals(func, "") && !Objects.equals(prev, "")) {
                    // no function and prev contains numbers, replace
                    prev = "";
                    curr = "4";
                } else {
                    curr += "4";
                }
                break;
            case R.id.btnDecimal:
                evaluate(".");
            case R.id.btnEquals:
                evaluate("=");
                break;
            case R.id.btnSum:
                evaluate("+");
                break;
        }
    }
    
    private void updateUI(Character value) {

    }

    private void evaluate(String item) {
        int i = stack.size();
        int r = 0;
        while (i != 0) {
            curr = stack.pop();

        }

    }



}
