package mjsma5.budgey;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import mjsma5.budgey.Login;

/**
 * Created by Matts on 27/04/2017.
 */

public class Transaction {
    private String id;             // Unique transaction ID
    private Double amount;       // Transaction's Cost
    private String category;    // Transaction's user chosen category
    private String date;          // Date transaction entered, default current day
    private String note;        // Extra user entered details
    private String method;      // Payment used (eg, Paypal, Visa ...)
    private boolean taxable;    // Boolean if transaction flagged for taxable
    private boolean type;       // true: positive, false: negative

    public Transaction(String nID, Double nAmount, String nCategory, String nDate, String nNote,
                 String nMethod, Boolean nTaxable, Boolean nType) {
        if (nID == null) {
            // set unique id
        } else {
            id = UUID.randomUUID().toString();
        }
        amount = nAmount;
        category = nCategory;
        date = nDate;
        note = nNote;
        method = nMethod;
        taxable = nTaxable;
        type = nType;
    }

    // Methods to set
    private void setId(String item) { id = item; };
    private void setAmount(Double item) { amount = item; }
    private void setCategory(String item) { category = item; }
    private void setDate(String item) { date = item; }
    private void setNote(String item) { note = item; }
    private void setMethod(String item) { method = item; }
    private void setTaxable(Boolean item) { taxable = item; }
    private void setType(Boolean item) { type = item; }

    // Method to update databsase
    void updateDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference userRef = database.getReference("users/").child(id);

        HashMap<String, Object> result = new HashMap<>();
        result.put("amount", amount);
        result.put("category", category);
        result.put("date", date);
        result.put("note", note);
        result.put("method", method);
        result.put("taxable", taxable);
        result.put("type", type);
        userRef.setValue(result);
    }
}

/* access
    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
    Transaction transaction = new Transaction("a", 12.12, "b", currentDateTimeString, "c", "d", true, true);
    transaction.updateDatabase();
 */
