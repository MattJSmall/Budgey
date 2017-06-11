package mjsma5.budgey;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Matts on 11/05/2017.
 */

class Category {
    /* Purpose: Stores Information about a specific category */
    private String value;
    private ArrayList<Transaction> ownedTransactions;
    private Double valueSum;
    private String id;

    Category(String _value, Double _valueSum) {
        value = _value;
        valueSum = _valueSum;
        ownedTransactions = new ArrayList<>();
    }

    void addTransaction(Transaction t) {
        ownedTransactions.add(t);
    }

    void addValueSum(Double input) {
        valueSum += input;
    }

    void delTransaction(Transaction t) {
        ownedTransactions.remove(ownedTransactions.indexOf(t));
        valueSum -= Double.valueOf(t.getAmount());
    }
    boolean isEmpty() {
        return ownedTransactions.isEmpty();
    }

    Double getValueSum() {
        return valueSum;
    }

    String getValue() {
        return value;
    }

    ArrayList<String> getTransactions() {

        ArrayList<Transaction> transactions = FirebaseServices.transactions;
        ArrayList<String> tempList = new ArrayList<>();
        for (Transaction t : ownedTransactions) {
            Log.d("LIST_CHILD_DETAILS", t.getNote());
            Integer index = transactions.indexOf(t);
            tempList.add(index.toString());
        }
        return tempList;
    }
}
