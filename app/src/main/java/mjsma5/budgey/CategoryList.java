package mjsma5.budgey;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Matts on 11/05/2017.
 */

class CategoryList {

    private ArrayList<String> usedCategories = new ArrayList<>();
    private HashMap<String, Category> categories = new HashMap<>();

    private void addUsed(String categoryName) {
        usedCategories.add(categoryName);
    }


    CategoryList() {
    }

    void removeCategory(String categoryLabel) {
        /* Remove category data including all transactions related to category */
        if (usedCategories.contains(categoryLabel)) {
            usedCategories.remove(usedCategories.indexOf(categoryLabel));
        }
        for (String transactionID : categories.get(categoryLabel).getTransactions()) {
            FirebaseServices.deleteTransaction(transactionID);
        }
        categories.remove(categoryLabel);
    }


    void newCategory(Category category) {
        /* add new category         */
        categories.put(category.getLabel(), category);
    }


    void addTransaction(String categoryLabel, Transaction t) {
        categories.get(categoryLabel).addTransaction(t);
    }

    Set<String> getCategoryLabels() { return categories.keySet(); }


    void addValueSum(String categoryLabel, Double val) {
        categories.get(categoryLabel).addValueSum(val); }

    void transRemoved(String transactionID) {
        Transaction t = FirebaseServices.transactions.get(transactionID);
        categories.get(t.getCategory()).delTransaction(t);
        // If no more expense transactions are contained within this category, remove category object
        if (categories.get(t.getCategory()).isEmpty()) {
            usedCategories.remove(usedCategories.indexOf(t.getCategory()));
        }
    }

    int size() {
        return categories.size();
    }

    Integer indexOf(String cat) {
        return usedCategories.indexOf(cat);
    }

    boolean isUsed(String cat) {
        return usedCategories.contains(cat);
    }

    private String getAllUsedCategories() {
        String out = "";
        for (String s: usedCategories) {
            out += " " + s;
        }
        return out;
    }

    String getKey(String label) {
        return categories.get(label).getKey();
    }

    ArrayList<Category> getCategories () {
        ArrayList<Category> catList = new ArrayList<>();
        for (String key: categories.keySet()) {
            catList.add(categories.get(key));
        }
        return catList;
    }

    Double getCategoryValueSum(String categoryLabel) {
        return categories.get(categoryLabel).getValueSum();
    }

    public boolean contains(String s) {
        return categories.containsKey(s);
    }
}
