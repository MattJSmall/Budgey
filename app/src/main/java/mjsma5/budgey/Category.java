package mjsma5.budgey;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Matts on 11/05/2017.
 */

class Category {
    /* Purpose: Stores Information about a specific category */
    private String label;
    private ArrayList<String> ownedTransactions;
    private Double valueSum;
    private String key;


    Category(String _label, Double _valueSum) {
        label = _label;
        valueSum = _valueSum;
        ownedTransactions = new ArrayList<>();
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setValueSum(Double valueSum) {
        this.valueSum = valueSum;
    }

    public void setKey(String key) {
        this.key = key;
    }

    void addTransaction(Transaction t) {
        ownedTransactions.add(t.getID());
        valueSum += Double.valueOf(t.getAmount());
    }

    void addValueSum(Double input) {
        valueSum += input;
    }

    void delTransaction(Transaction t) {
        ownedTransactions.remove(ownedTransactions.indexOf(t.getID()));
        valueSum -= Double.valueOf(t.getAmount());
    }
    boolean isEmpty() {
        return ownedTransactions.isEmpty();
    }

    Double getValueSum() {
        return valueSum;
    }

    String getLabel() {
        return label;
    }

    ArrayList<String> getTransactions() {
        return ownedTransactions;
    }

    public String getKey() {
        return key;
    }
}
