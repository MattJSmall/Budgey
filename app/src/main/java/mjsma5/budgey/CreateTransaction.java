package mjsma5.budgey;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import java.io.ObjectStreamException;
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

    // NEED TO ADD PREVIEW FUNCTION

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_transaction);

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

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnClose:
                curr += ")";
                updateUI(')');
                break;
            case R.id.btnClear:
                txtResult.setText("$0.00");
                curr = "";
                prev = "";
                break;
            case R.id.btnPercentage:
                // percentage calculations
                if (Objects.equals(func, "x")) {
                    tmp = String.valueOf((Integer.parseInt(prev) / 100) * Integer.parseInt(curr));
                } else if (Objects.equals(func, "/")) {
                    tmp = String.valueOf(Integer.parseInt(prev) / ((Integer.parseInt(prev) / 100)
                            * Integer.parseInt(curr)));
                } else if (Objects.equals(func, "-")) {
                    tmp = String.valueOf(Integer.parseInt(prev) - ((Integer.parseInt(prev) / 100)
                            * Integer.parseInt(curr)));
                } else if (Objects.equals(func, "/")) {
                    tmp = String.valueOf(Integer.parseInt(prev) + ((Integer.parseInt(prev) / 100)
                            * Integer.parseInt(curr)));
                } else {
                    tmp =  String.valueOf(1 / Integer.parseInt(prev));
                }
                prev = tmp;
                curr = "";
                txtResult.setText(tmp += "%");
                break;
            case R.id.btnOpen:
                curr += "(";
                break;
            case R.id.btn7:
                if (Objects.equals(func, "") && !Objects.equals(prev, "")) {
                    // no function and prev contains numbers, replace
                    prev = "";
                    curr = "7";
                } else {
                    curr += "7";
                }

                break;
            case R.id.btn8:
                if (Objects.equals(func, "") && !Objects.equals(prev, "")) {
                    // no function and prev contains numbers, replace
                    prev = "";
                    curr = "8";
                } else {
                    curr += "8";
                }
                break;
            case R.id.btn9:
                if (Objects.equals(func, "") && !Objects.equals(prev, "")) {
                    // no function and prev contains numbers, replace
                    prev = "";
                    curr = "9";
                } else {
                    curr += "9";
                }
                break;
            case R.id.btnDiv:
                if (Objects.equals(prev, "") && !Objects.equals(curr, "")) {     // Replaces with / if no number has been entered
                    func = "/";
                    prev = curr;
                    curr = "";
                } else if (!Objects.equals(curr, "")) {
                    prev = curr;
                    func = "/";
                    curr = "";
                } else if (!Objects.equals(func, "")) {
                    tmp = String.valueOf(Integer.parseInt(prev) / Integer.parseInt(curr));
                    curr = "";
                    prev = tmp;
                }
                break;
            case R.id.btn4:
                if (Objects.equals(func, "") && !Objects.equals(prev, "")) {
                    // no function and prev contains numbers, replace
                    prev = "";
                    curr = "4";
                } else {
                    curr += "4";
                }
                break;
            case R.id.btn5:
                if (Objects.equals(func, "") && !Objects.equals(prev, "")) {
                    // no function and prev contains numbers, replace
                    prev = "";
                    curr = "5";
                } else {
                    curr += "5";
                }
                break;
            case R.id.btn6:
                if (Objects.equals(func, "") && !Objects.equals(prev, "")) {
                    // no function and prev contains numbers, replace
                    prev = "";
                    curr = "6";
                } else {
                    curr += "6";
                }
                break;
            case R.id.btnMult:
                if (Objects.equals(prev, "") && !Objects.equals(curr, "")) {     // Replaces with / if no number has been entered
                    func = "x";
                    prev = curr;
                    curr = "";
                } else if (!Objects.equals(curr, "")) {
                    prev = curr;
                    func = "x";
                    curr = "";
                }
                break;
            case R.id.btn1:
                if (Objects.equals(func, "") && !Objects.equals(prev, "")) {
                    // no function and prev contains numbers, replace
                    prev = "";
                    curr = "1";
                } else {
                    curr += "1";
                }
                break;
            case R.id.btn2:
                if (Objects.equals(func, "") && !Objects.equals(prev, "")) {
                    // no function and prev contains numbers, replace
                    prev = "";
                    curr = "2";
                } else {
                    curr += "2";
                }
                break;
            case R.id.btn3:
                if (Objects.equals(func, "") && !Objects.equals(prev, "")) {
                    // no function and prev contains numbers, replace
                    prev = "";
                    curr = "3";
                } else {
                    curr += "3";
                }
                break;
            case R.id.btnMin:
                if (Objects.equals(prev, "") && !Objects.equals(curr, "")) {     // Replaces with / if no number has been entered
                    func = "/";
                    prev = curr;
                    curr = "";
                } else if (!Objects.equals(curr, "")) {
                    prev = curr;
                    func = "/";
                    curr = "";
                }
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
                updateUI('.');
                break;
            case R.id.btnEquals:
                updateUI('=');
                break;
            case R.id.btnSum:
                if (Objects.equals(prev, "") && !Objects.equals(curr, "")) {     // Replaces with / if no number has been entered
                    func = "/";
                    prev = curr;
                    curr = "";
                } else if (!Objects.equals(curr, "")) {
                    prev = curr;
                    func = "/";
                    curr = "";
                }
                break;
        }
    }
    
    private void updateUI(Character value) {
        if (value == 'c') {

        } 
        else if (value == '(') {
            
        }
        
    }



}
