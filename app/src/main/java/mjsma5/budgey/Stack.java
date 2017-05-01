package mjsma5.budgey;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matts on 1/05/2017.
 */

public class Stack {
    private List<String> array;
    private String tmp;

    public Stack() {
        array = new ArrayList<String>();
    }

    public void push(String item) {
        array.add(item);
    }

    public String pop() {
        tmp = array.get(array.size() - 1);
        array.remove(array.size() - 1);
        return tmp;
    }

    public String peek() {
        return array.get(array.size() - 1);
    }

    public void back() {
        array.remove(array.size() - 1);
    }

    public void clearE() {
        do {
            try {
                Integer.parseInt(array.get(array.size() - 1));
                array.remove(array.size() - 1);
            } catch (Exception e) {
                break;
            }
        } while (true);
    }

    public void clearAll() {
        array = new ArrayList<String>();
    }

    public int size() {
        return array.size();
    }

}
