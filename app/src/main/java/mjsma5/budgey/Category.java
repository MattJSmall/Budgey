package mjsma5.budgey;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Matts on 11/05/2017.
 */

public class Category {
    private String key;
    private String value;
    private ArrayList<Transaction> ownedTransactions;
    private Double valueSum;

    public Category(String k, String v, Double s) {
        key = k;
        value = v;
        valueSum = s;
        ownedTransactions = new ArrayList<>();
    }

    public void addTransaction(Transaction t) {
        ownedTransactions.add(t);
    }

    public Transaction getTransaction(Integer index) {
        return ownedTransactions.get(index);
    }

    public void addValueSum(Double input) {
        valueSum += input;
    }

    public void delTransaction(Transaction t) {
        ownedTransactions.remove(t);
        valueSum -= Double.valueOf(t.getAmount());
    }

    public Double getValueSum() {
        return valueSum;
    }

    public void setValue(String v) {
        value = v;
    }

    public String getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }

    public ArrayList<String> getTransactions() {

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
