package mjsma5.budgey;

import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matts on 11/05/2017.
 */

public class CategoryList {
    private List<Category> categories = new ArrayList<>();

    public void addItem(String key, String value) {
        categories.add(new Category(key, value));
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
        for (int i = 0; i < categories.size() - 1; i++) {
            if (categories.get(i).getKey().equals(key)) {
                categories.remove(i);
            }
        }
        Log.d("SYSTEM", "DELETE FAILED");
    }
    
    public String[] getAll() {
        String[] items = new String[categories.size()];
        for (int i = 0; i < categories.size(); i++) {
            items[i] = categories.get(i).getValue();
        }
        return items;
    }

}
