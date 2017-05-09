package mjsma5.budgey;

import android.content.DialogInterface;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Stack;


public class CreateTransaction extends AppCompatActivity implements View.OnClickListener {

    private Button btnDateFinish;
    private Button btnDate;
    private TextView txtResult;

    /*
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
    */

    private Boolean decimal;
    // private String preview;
    private ImageButton btnMethod;

    private String result;
    private int parenthesis;
    private boolean reset;
    private boolean operatorLast;

    private Transaction transaction;

    private DatePicker datePicker;
    private Calendar date;
    private TextView txtNote;

    public AlertDialog.Builder methodMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_transaction);

        // Transaction details
        findViewById(R.id.rbTaxDeductable).setOnClickListener(this);
        findViewById(R.id.btnChooseCategory).setOnClickListener(this);
        btnMethod = (ImageButton) findViewById(R.id.btnMethod);
        findViewById(R.id.btnMethod).setOnClickListener(this);
        findViewById(R.id.btnDate).setOnClickListener(this);
        txtNote = (TextView) findViewById(R.id.txtNote);


        findViewById(R.id.btnFinish).setOnClickListener(this);

        // Calculator UI components
        ImageButton btnBackSpace = (ImageButton) findViewById(R.id.btnBack);
        findViewById(R.id.btnBack).setOnClickListener(this);
        btnBackSpace.setImageResource(R.drawable.backspace); // backspace visualisation

        findViewById(R.id.btnOpen).setOnClickListener(this);
        findViewById(R.id.btnClose).setOnClickListener(this);
        findViewById(R.id.btnClear).setOnClickListener(this);
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

        datePicker = (DatePicker) findViewById(R.id.datePicker);

        // Calculator additional requirements
        txtResult = (TextView) findViewById(R.id.txtResult);
        result = "";
        parenthesis = 0;
        reset = false;
        decimal = false;
        operatorLast = false;

        // Date Picker
        btnDate = (Button) findViewById(R.id.btnDate);
        findViewById(R.id.btnDateFinish).setOnClickListener(this);
        btnDateFinish = (Button) findViewById(R.id.btnDateFinish);
        date = Calendar.getInstance();
        updateDate(date);

        btnDateFinish.setVisibility(View.GONE);
        datePicker.setVisibility(View.GONE);

        // Transaction initialisation
        transaction = new Transaction("ID", 0.00, "NULL", date, "", "cash", false, false);
        // (String nID, Double nAmount, String nCategory, String nDate, String nNote,
        // String nMethod, Boolean nTaxable, Boolean nType) {
        setMethodImage();
        methodMenu = new AlertDialog.Builder(this);
        methodMenu.setTitle("Select a Transaction Method");
        methodMenu.setItems(new CharSequence[] {"Cash", "Paypal", "Debit", "Credit"},
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // The 'which' argument contains the index position
                    // of the selected item
                    switch (which) {
                        case 0:
                            transaction.setMethod("cash");
                            break;
                        case 1:
                            transaction.setMethod("paypal");
                            break;
                        case 2:
                            transaction.setMethod("debit");
                            break;
                        case 3:
                            transaction.setMethod("credit");
                            break;
                    }
                }
        });

    }

    private void updateDate(Calendar c) {
        int day = c.get(Calendar.DAY_OF_WEEK);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);

        StringBuilder currDate = new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year);
        btnDate.setText(currDate);
    }

    public void onRadioButtonClicked(View view) {
        boolean taxableChecked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.rbTaxDeductable:
                taxableChecked = !taxableChecked;
                break;
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnFinish:
                transaction.setNote(txtNote.getText().toString());
                transaction.updateDatabase();
                /*
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                // DatabaseReference myRef = database.getReference('message');
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference tranRef = database.getReference("users/" + currentUser.getUid());
                tranRef.push()
                test.setValue("Hello, World!");
                */
                //intent return to details
                break;
            case R.id.btnChooseCategory:
                // intent open list
                break;
            case R.id.btnMethod:
                methodMenu.show();
                setMethodImage();
                break;
            case R.id.btnDate:
                btnDateFinish.setVisibility(View.VISIBLE);
                datePicker.setVisibility(View.VISIBLE);

                // intent start datePicker
                break;
            case R.id.btnDateFinish:
                date.set (datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                updateDate(date);
                datePicker.setVisibility(View.GONE);
                btnDateFinish.setVisibility(View.GONE);
                break;

            // Calculator buttons
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

    public void setMethodImage() {
        switch (transaction.getMethod()) {
            case "cash":
                btnMethod.setImageResource(R.drawable.cash);
                break;
            case "credit":
                btnMethod.setImageResource(R.drawable.visa);
                break;
            case "debit":
                btnMethod.setImageResource(R.drawable.card);
                break;
            case "paypal":
                btnMethod.setImageResource(R.drawable.paypal);
        }
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
