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

    public void addItem(String key, String value, Double valueSum) {
        categories.add(new Category(key, value, valueSum));
        indCategories.add(value);
    }
    public void addTransaction(Integer index, Transaction t) {
        categories.get(index).addTransaction(t);
    }

    public ArrayList<Category> getList() {
        return categories;
    }

    public Category getItem(String key) {
        for (int i = 0; i < categories.size() - 1; i++) {
            if (categories.get(i).getKey().equals(key)) {
                return categories.get(i);
            }
        }
        return new Category("error", "error", 0d);
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

    public void transRemoved(Integer i, Double val) { categories.get(i).delValueSum(val);}

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
