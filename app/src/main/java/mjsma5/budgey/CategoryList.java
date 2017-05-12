package mjsma5.budgey;

import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matts on 11/05/2017.
 */

public class CategoryList {
    private ArrayList<Category> categories = new ArrayList<>();

    public void addItem(String key, String value) {
        categories.add(new Category(key, value));
        String testing;
        for (int i = 0; i < categories.size(); i++) {
            testing = categories.get(i).getValue();
            Log.d("FIREBASE", "currList value:");
        }
    }

    public Category getItem(String key) {
        for (int i = 0; i < categories.size() - 1; i++) {
            if (categories.get(i).getKey().equals(key)) {
                return categories.get(i);
            }
        }
        return new Category("error", "error");
    }
    public void delItem(String key) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getKey().equals(key)) {
                categories.remove(i);
                Log.d("FIREBASE", "LOCAL CATEGORY DELETE SUCCESFUL");
            }
        }
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

}
