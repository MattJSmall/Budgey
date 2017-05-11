package mjsma5.budgey;

/**
 * Created by Matts on 11/05/2017.
 */

public class Category {
    private static String key;
    private static String value;

    public Category(String key, String value) {
        Category.key = key;
        Category.value = value;
    }

    public void setValue(String value) {
        Category.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }
}
