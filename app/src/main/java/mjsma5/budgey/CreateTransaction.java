package mjsma5.budgey;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;


public class CreateTransaction extends AppCompatActivity implements View.OnClickListener {

    private Button btnDate;
    private TextView txtResult;

    private Boolean decimal;
    // private String preview;
    private ImageButton btnMethod;
    private Button btnCategory;

    private String input;
    private String result;
    private int parenthesis;
    private boolean reset;
    private boolean operatorLast;

    private Transaction transaction;

    private Calendar date;
    private TextView txtNote;

    public AlertDialog.Builder methodMenu;
    public AlertDialog.Builder categoryMenu;
    public AlertDialog.Builder createCategoryDialog;
    public ArrayList<String> cat;
    public static CategoryList categories;
    private String[] menuItems;
    private Context context;

    private static FirebaseDatabase database;
    private static String uID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_transaction);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        context = this;

        Toast welcome = Toast.makeText(this, "Enter your transaction details", Toast.LENGTH_SHORT);

        welcome.show();
        uID = GoogleSignInActivity.uID;
        categories = FirebaseServices.categories;
        database = GoogleSignInActivity.database;

        // Transaction details
        btnCategory = (Button) findViewById(R.id.btnCategory);
        findViewById(R.id.btnCategory).setOnClickListener(this);
        btnMethod = (ImageButton) findViewById(R.id.btnMethod);
        findViewById(R.id.btnMethod).setOnClickListener(this);
        findViewById(R.id.btnDate).setOnClickListener(this);
        txtNote = (TextView) findViewById(R.id.txtNote);

        btnCategory.setVisibility(View.VISIBLE);

        // Calculator UI components
        findViewById(R.id.btnBack).setOnClickListener(this);

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

        // Calculator additional requirements
        txtResult = (TextView) findViewById(R.id.txtResult);
        result = "";
        parenthesis = 0;
        reset = false;
        decimal = false;
        operatorLast = false;

        result = "";

        // Date Picker
        btnDate = (Button) findViewById(R.id.btnDate);
        date = Calendar.getInstance();
        setDate();


        // Transaction initialisation
        Intent intent = getIntent();
        transaction = new Transaction("0 0 0", false, "0.00", "cash", "null", "Note", false);
        // String nDate, Boolean nTaxable,String nAmount, String nMethod, String nCategory, String nNote, Boolean nType
        setMethodImage();
        methodMenu = new AlertDialog.Builder(this);
        methodMenu.setTitle("Select a Transaction Method");
        methodMenu.setItems(new CharSequence[] {"Cash", "PayPal", "Debit", "Credit"},
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
                    setMethodImage();
                }
        });

        // Alert Dialogs
        categoryMenu = new AlertDialog.Builder(this);
        categoryMenu.setTitle("Select a Category");
    }

    private void instanceCalculator() {
        parenthesis = 0;
        reset = false;
        decimal = false;
        operatorLast = false;
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

    public AlertDialog.Builder reinstanceCreateCategory() {
        AlertDialog.Builder createCategory = new AlertDialog.Builder(this);
        createCategory.setTitle("Please enter name of category");
        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        createCategory.setView(input);
        createCategory.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (categories.isCreated(input.getText().toString())) {
                    Toast.makeText(context, "Parenthesis Mismatch", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseReference curr_category = database.getReference("users/" + uID + "/categories");
                    String key = curr_category.push().getKey();
                    curr_category.child(key).setValue(input.getText().toString());
                    transaction.setCategory(input.getText().toString());
                    Log.d("Category_Added ", input.getText().toString());
                    btnCategory.setText(input.getText());
                }
            }
        });
        createCategory.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        return createCategory;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCategory:
                if (transaction.getCategory().equals("null") || txtResult.getText().toString().equals("0.00")) {
                    createCategoryDialog = reinstanceCreateCategory();
                    final String[] menuItems = categories.getAll();
                    categoryMenu.setItems(menuItems,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // The 'which' argument contains the index position
                                    // of the selected item
                                    if (which == categories.indSize() - 1) {
                                        createCategoryDialog.show();
                                    } else {
                                        transaction.setCategory(menuItems[which]);
                                    }
                                    btnCategory.setText(transaction.getCategory());
                                    pass();
                                }
                            });
                    categoryMenu.show();
                } else {
                    pass();
                }

                break;
            case R.id.btnMethod:
                methodMenu.show();
                break;
            case R.id.btnDate:
                showDialog(R.id.datePicker);
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
                break;
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
                if (!decimal) {
                    decimal = true;
                    result += ".";
                    Log.d("INFO", "DECIMAL INPUT");
                } else {
                    Log.d("INPUT ERROR", "DECIMAL ERROR");
                }
                operatorLast = false;
                break;

            case R.id.btnEquals:
                if (operatorLast) {
                    result = result.substring(0, result.length() - 1);
                    // if the last token entered was an operator, remove it, then evaluate.
                    operatorLast = !operatorLast;
                }
                if (parenthesis == 0) {
                    Log.d("EVALUATION_INPUT", result);
                    result = evaluate(result);
                    Log.d("EVALUATION_OUTPUT", result);
                    reset = true;
                } else {
                    Toast.makeText(this, "Parenthesis Mismatch", Toast.LENGTH_SHORT).show();
                    Log.d("INPUT ERROR", "PARENTHESIS");
                }
                break;
        }
        txtResult.setText("$" + result);
    }

    private void pass() {
        /*  Exit activity and return to landing class
         *  @Params: form must be complete
         */
        if (result.equals("")) {
            Toast valueError = Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT);
            valueError.show();
        } else if (transaction.getCategory().equals("NULL")) {
            Toast categoryError = Toast.makeText(this, "Please enter a Category", Toast.LENGTH_SHORT);
            categoryError.show();
        } else {
            if (transaction.getCategory().equals("Salary")) {
                transaction.setType(true);
            } else {
                transaction.setType(false);
            }
            transaction.setNote(txtNote.getText().toString());
            transaction.setAmount(result);
            transaction.setDate(String.valueOf(date.get(Calendar.DAY_OF_MONTH)) + " " + date.get(Calendar.MONTH) + " " + date.get(Calendar.YEAR));
            transaction.updateDatabase();
            Intent home = new Intent(this, Landing.class);
            startActivity(home);
        }
    }


    public String evaluate(String s) {
        Expression e = new ExpressionBuilder(s).build();
        Double r = e.evaluate();
        return String.valueOf(r);
    }
    /*
    public String evaluate(String infix) {
        // Converts infix string to postfix using Dijkstra's ShuntingYard
        String postfix = ShuntingYard.postfix(infix);
        Log.d("EVALUATION_POSTFIX", postfix);
        // Evaluates postfix using the stack method.
        String value = postfixEvaluation(postfix);
        Log.d("EVALUATION_DONE", value);
        if (value.equals("ERROR")) {
            result = "";
            txtResult.setText(result);
            Log.d("INPUT ERROR", "EVALUATION ERROR");
            return "error"; // ~ insert error reporting
        } else {
            return value;
        }
    }
    */

    public void numUpdate(String input) {
        // Update module for all numbers
        if (reset) {
            result = input;
            txtResult.setText(result);
            reset = !reset;
        } else {
            result += input;
        }
        operatorLast = false;
    }

    public void opUpdate(String input) {
        if (reset) {
            instanceCalculator();
        }
        if (operatorLast) {
            result = result.substring(0, result.length() -3);
        }
        result += " " + input + " ";
        operatorLast = true;
        decimal = false;
    }



    //date functions
    private void setDate() {
        String format = new SimpleDateFormat("EEEE d, MMMM yyyy").format(date.getTime());
        btnDate.setText(format);
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    date.set(year, month, dayOfMonth);
                    setDate();
                }
            };

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case R.id.datePicker:
                return new DatePickerDialog(this,
                        myDateListener,
                        date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
        }
        return null;
    }





    }
