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
    private ArrayList<String> usedCategories = new ArrayList<>();

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

    public void addUsed(String categoryName) {
        usedCategories.add(categoryName);
    }


    public CategoryList() {
    }

    public Category get(int index) {
         return categories.get(index);
    }

    public void removeCategory(String category) {
        /* Remove category data including all transactions related to category
         *
         */
        indCategories.remove(indCategories.indexOf(category)); // remove from individual cat list
        if (usedCategories.contains(category)) { // removed from used cat list if it has been used
            usedCategories.remove(usedCategories.indexOf(category));
        }
        Category c = getItem(category);
        if (c.getValue().equals("error")) {
            Log.d("CATEGORY_REMOVAL", "FAILED: " + category);
        } else {
            for (String t : c.getTransactions()) {
                FirebaseServices.deleteTransaction(FirebaseServices.transactions.get(Integer.valueOf(t)).getID());
            }
        }
    }

    private Category getItem(String category) {
        for (Category c: categories) {
            if (c.getValue().equals(category)) {
                return c;
            }
        }
        return new Category("error", 0d);
    }


    public void addItem(String category, Double valueSum) {
        categories.add(new Category(category, valueSum));
        addUsed(category);
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

    public Category getCategory(String category) {
        // Retrun category based on key ID
        for (int i = 0; i < categories.size() - 1; i++) {
            if (categories.get(i).getValue().equals(category)) {
                return categories.get(i);
            }
        }
        return new Category("error", 0d);
    }

    public void addValueSum(Integer catIndex, Double val) {
        categories.get(catIndex).addValueSum(val); }

    public void transRemoved(Integer catIndex, Integer transIndex) {
        Transaction t = FirebaseServices.transactions.get(transIndex);
        categories.get(catIndex).delTransaction(t);
        Landing.update();
        if (categories.get(catIndex).getTransactions().isEmpty()) {
            categories.remove(catIndex);
            usedCategories.remove(usedCategories.indexOf(t.getCategory()));
            Landing.updateChart();
        }
    }

    public int size() {
        return categories.size() + 1;
    }
    public int indSize() {
        return indCategories.size() + 1;
    }

    public boolean contains(String item) {
        return indCategories.contains(item);
    }
    
    public String[] getAll() {
        String[] items = indCategories.toArray(new String[indCategories.size() + 1]);
        items[indCategories.size()] = "Create new Category";
        return items;
    }



    public boolean isEmpty() {
        return categories.isEmpty();
    }

    public Integer indexOf(String cat) {
        return usedCategories.indexOf(cat);
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

    public boolean isUsed(String cat) {
        return usedCategories.contains(cat);
    }

    public boolean isCreated(String cat) {
        return indCategories.contains(cat);
    }
}
