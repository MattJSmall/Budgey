package mjsma5.budgey;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

/**
 * Created by Matts on 27/04/2017.
 */

public class Transaction {
    private String id;             // Unique transaction ID
    private String amount;       // Transaction's Cost
    private String category;    // Transaction's user chosen category
    private String date;          // Date transaction entered, default current day
    private String note;        // Extra user entered details
    private String method;      // Payment used (eg, Paypal, Visa ...)
    private boolean type;       // true: positive, false: negative

    public Transaction(){}

    private DatabaseReference transRef = GoogleSignInActivity.transRef;


    Transaction(String nDate, String nAmount, String nMethod,
                String nCategory, String nNote, Boolean nType) {
        amount = nAmount;
        category = nCategory;
        date = nDate;
        note = nNote;
        method = nMethod;
        type = nType;
    }

    // methods to get
    String getID() { return id; }
    String getCategory() {return category;}
    String getAmount() {return amount;}
    public Boolean gType() {return type; }
    String getDate() {
        return date;
    }

    // Methods to set
    public void setId(String item) { id = item; };
    void setAmount(String item) { amount = item; }
    void setCategory(String item) { category = item; }
    void setDate(String item) { date = item; }
    void setNote(String item) { note = item; }
    void setMethod(String item) { method = item; }
    void setType(Boolean item) { type = item; }

    // Method to update database
    void updateDatabase() {
        DatabaseReference currTrans;

        HashMap<String, Object> result = new HashMap<>();
        result.put("amount", amount);
        result.put("category", category);
        result.put("date", date);
        result.put("note", note);
        result.put("method", method);
        result.put("type", type);
        String key = transRef.push().getKey();
        currTrans = transRef.getRef().child("/" + key);
        currTrans.updateChildren(result);
        Log.d("FIREBASE_UPLOAD", "Transaction Uploaded");
    }

    String getMethod() {
        return method;
    }

    String getNote() {
        return note;
    }
}

