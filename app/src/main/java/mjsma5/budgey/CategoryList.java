package mjsma5.budgey;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Matts on 11/05/2017.
 */

class CategoryList implements Parcelable {

    private ArrayList<Category> categories = new ArrayList<>();

    private ArrayList<String> indCategories = new ArrayList<>();
    private ArrayList<String> usedCategories = new ArrayList<>();
    private ArrayList<String> indCategoryIDs = new ArrayList<>();

    private CategoryList(Parcel in) {
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

    private void addUsed(String categoryName) {
        usedCategories.add(categoryName);
    }


    CategoryList() {
    }

    Category get(int index) {
         return categories.get(index);
    }

    void removeCategory(String category) {
        /* Remove category data including all transactions related to category */
        Category c = getItem(category);
        if (c.getValue().equals("error")) {
            Log.d("CATEGORY_REMOVAL", "FAILED: " + category);
        } else {
            for (String t : c.getTransactions()) {
                FirebaseServices.deleteTransaction(FirebaseServices.transactions.get(Integer.valueOf(t)).getID());
            }
        }
        indCategoryIDs.remove(indCategories.indexOf(category));
        indCategories.remove(indCategories.indexOf(category)); // remove from individual cat list
    }

    private Category getItem(String category) {
        for (Category c: categories) {
            if (c.getValue().equals(category)) {
                return c;
            }
        }
        return new Category("error", 0d);
    }


    void addItem(String category, Double valueSum) {
        categories.add(new Category(category, valueSum));
        addUsed(category);
    }

    void addIndCategory(String id, String category) {
        if (!indCategories.contains(category)) {
            indCategories.add(category);
            indCategoryIDs.add(id);
        }
    }

    void addTransaction(Integer index, Transaction t) {
        categories.get(index).addTransaction(t);
    }

    ArrayList<Category> getList() {
        return categories;
    }


    void addValueSum(Integer catIndex, Double val) {
        categories.get(catIndex).addValueSum(val); }

    void transRemoved(int catIndex, Integer transIndex) {
        Transaction t = FirebaseServices.transactions.get(transIndex);
        categories.get(catIndex).delTransaction(t);
        // If no more expense transactions are contained within this category, remove category object
        if (categories.get(catIndex).isEmpty() &&
                (!categories.get(catIndex).getValue().equals("Salary"))) {
            categories.remove(catIndex);
            usedCategories.remove(catIndex);
        }
    }

    int indSize() {
        return indCategories.size() + 1;
    }
    
    String[] getAll() {
        String[] items = indCategories.toArray(new String[indCategories.size() + 1]);
        items[indCategories.size()] = "Create new Category";
        return items;
    }

    Integer indexOf(String cat) {
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

    boolean isUsed(String cat) {
        return usedCategories.contains(cat);
    }

    boolean isCreated(String cat) {
        return indCategories.contains(cat);
    }

    private String getAllUsedCategories() {
        String out = "";
        for (String s: usedCategories) {
            out += " " + s;
        }
        return out;
    }

    Double getCategoryValueSum(String headerTitle) {
        Log.d("CATEGORIES", getAllUsedCategories());
        Log.d("CAT_RETRIEVAL", headerTitle);
        return categories.get(usedCategories.indexOf(headerTitle)).getValueSum();
    }

    String getID(int index) {
        return indCategoryIDs.get(index);
    }
}
