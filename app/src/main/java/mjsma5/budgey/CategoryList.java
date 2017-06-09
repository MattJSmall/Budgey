package mjsma5.budgey;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matts on 11/05/2017.
 */

public class CategoryList implements Parcelable {

    private ArrayList<Category> categories = new ArrayList<>();
    private ArrayList<String> indCategories = new ArrayList<>();
    //private static ArrayList<Transaction> transactions = FirebaseServices.transactions;

    protected CategoryList(Parcel in) {
        indCategories = in.createStringArrayList();
    }

    public static final Creator<CategoryList> CREATOR = new Creator<CategoryList>() {
        @Override
        public CategoryList createFromParcel(Parcel in) {
            return new CategoryList(in);
        }

        @Override
        public CategoryList[] newArray(int size) {
            return new CategoryList[size];
        }
    };

    public CategoryList() {
    }

    public Category get(int index) {
         return categories.get(index);
    }

    public void removeCategory(String category) {
        indCategories.remove(indCategories.indexOf(category));
        Category c = getItem(category);
        for (String t: c.getTransactions()) {
            FirebaseServices.deleteTransaction(FirebaseServices.transactions.get(Integer.valueOf(t)).getID());
        }
    }


    public void addItem(String key, String value, Double valueSum) {
        categories.add(new Category(key, value, valueSum));
        indCategories.add(value);
    }

    public void addIndCategory(String category) {
        if (!indCategories.contains(category)) {
            indCategories.add(category);
        }
    }

    public void addTransaction(Integer index, Transaction t) {
        categories.get(index).addTransaction(t);
    }

    public ArrayList<Category> getList() {
        return categories;
    }

    public Category getItem(String key) {
        // Retrun category based on key ID
        for (int i = 0; i < categories.size() - 1; i++) {
            if (categories.get(i).getKey().equals(key)) {
                return categories.get(i);
            }
        }
        return new Category("error", "error", 0d);
    }

    public Category getCategory(String category) {
        // Retrun category based on key ID
        for (int i = 0; i < categories.size() - 1; i++) {
            if (categories.get(i).getValue().equals(category)) {
                return categories.get(i);
            }
        }
        return new Category("error", "error", 0d);
    }


    private Integer getIndex(String key) {
        for (int i = 0; i < categories.size() - 1; i++) {
            if (categories.get(i).getKey().equals(key)) {
                return i;
            }
        }
        return -1;
    }

    public void delItem(String key) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getKey().equals(key)) {
                categories.remove(i);
                Log.d("FIREBASE", "LOCAL CATEGORY DELETE SUCCESFUL");
            }
        }
    }

    public void addValueSum(Integer i, Double val) {
        categories.get(i).addValueSum(val); }

    public void transRemoved(Integer catIndex, String transKey) {
        ArrayList<String> transList = categories.get(catIndex).getTransactions();
        Transaction t = new Transaction();
        for (int i = 0; i < transList.size(); i++) {
            if (transKey.equals(transList.get(i))) {
                t = categories.get(catIndex).getTransaction(i);
            }
        }
        categories.get(catIndex).delTransaction(t);
    }

    public int size() {
        return categories.size() + 1;
    }

    public boolean contains(String item) {
        return indCategories.contains(item);
    }
    
    public String[] getAll() {
        String[] items = new String[categories.size() + 1];
        for (int i = 0; i < categories.size(); i++) {
            items[i] = categories.get(i).getValue();
            Log.d("RETRIEVAL", categories.get(i).getValue());
        }
        items[categories.size()] = "Create New Category";
        return items;
    }

    public boolean isEmpty() {
        return categories.isEmpty();
    }

    public Integer indexOf(String cat) {
        return indCategories.indexOf(cat);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(categories);
        dest.writeList(indCategories);
    }
}
