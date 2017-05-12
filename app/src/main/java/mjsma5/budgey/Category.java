package mjsma5.budgey;

/**
 * Created by Matts on 11/05/2017.
 */

public class Category {
    private String key;
    private String value;

    public Category(String k, String v) {
        key = k;
        value = v;
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
}
