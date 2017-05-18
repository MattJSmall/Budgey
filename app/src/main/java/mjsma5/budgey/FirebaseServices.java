package mjsma5.budgey;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class FirebaseServices extends IntentService {
    private static String uID = GoogleSignInActivity.uID;
    private static FirebaseDatabase  database = GoogleSignInActivity.database;
    private static DatabaseReference catRef = database.getReference("users/" + uID + "/categories");

    public static CategoryList categories = new CategoryList();
    public static ArrayList<Transaction> transactions = new ArrayList<>();
    private ArrayList<String> curr_categories = new ArrayList<>();

    public static List<PieEntry> entries = new ArrayList<>();

    public static DatabaseReference transRef = GoogleSignInActivity.transRef;

    public FirebaseServices() {
        super("FirebaseServices");
    }




    @Override
    protected void onHandleIntent(Intent intent) {
        catRef.addChildEventListener(categoryChildEventListener);
        transRef.addChildEventListener(transactionsChildEventListener);
    }

    // Category Event Listener
    String TAG = "FIREBASE";
    public ChildEventListener categoryChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
            Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey() + " value:" + dataSnapshot.getValue());
            categories.addItem(dataSnapshot.getKey(), dataSnapshot.getValue().toString());
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
            Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
            categories.delItem(dataSnapshot.getKey());
            // ~ delete transactions to be added
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
            // A comment has changed position, use the key to determine if we are
            // displaying this comment and if so move it.
            Comment movedComment = dataSnapshot.getValue(Comment.class);
            String commentKey = dataSnapshot.getKey();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w(TAG, "category:onCancelled", databaseError.toException());
        }
    };

    public ChildEventListener transactionsChildEventListener = new ChildEventListener() {
        String TAG = "FIREBASE";
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
            Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
            Transaction t = dataSnapshot.getValue(Transaction.class);
            t.setId(dataSnapshot.getKey());
            transactions.add(t); // add to Transaction List

            // Chart update
            String cat = t.getCategory();
            if (curr_categories.contains(cat)) {
                Integer index = curr_categories.indexOf(cat);
                entries.set(index, new PieEntry(Float.valueOf(t.getAmount()) + entries.get(index).getValue(), cat));
            } else {
                curr_categories.add(t.getCategory());
                entries.add(new PieEntry(Float.valueOf(t.getAmount()), cat));
            }
            Landing.updateChart();
            /*
            float[] f = new float[3];
            f[0] = hue;
            f[1] = (float) 1;
            f[2] = (float) 1;
            colours.add(Color.HSVToColor(f));
            hue += (float) 0.09;
            */
        }


        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
            Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
            Transaction newTransaction = dataSnapshot.getValue(Transaction.class);
            String transactionKey = dataSnapshot.getKey();
            Integer pastIndex = -1;

            // Find index of past item;
            for (int i = 0; i < transactions.size(); i++) {
                if (transactionKey.equals(transactions.get(i).getID())) {
                    pastIndex = i;
                    break;
                }
            }

            // Chart Update
            String cat = newTransaction.getCategory();
            Integer index = curr_categories.indexOf(cat);
            // mew value must first remove past t value
            Float newValue = Float.valueOf(newTransaction.getAmount())
                    - Float.valueOf(transactions.get(pastIndex).getAmount())
                    + entries.get(index).getValue();
            entries.set(index, new PieEntry(newValue, cat));

            // Transaction List update
            transactions.set(pastIndex, newTransaction);
            Landing.updateChart();

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
            String transactionKey = dataSnapshot.getKey();
            Integer pastIndex = -1;
            for (int i = 0; i < transactions.size(); i++) {
                if (transactionKey.equals(transactions.get(i).getID())) {
                    pastIndex = i;
                    break;
                }
            }
            String cat = transactions.get(pastIndex).getCategory();
            Integer index = curr_categories.indexOf(cat);
            // mew value must first remove past t value
            Float newValue = entries.get(index).getValue()
                    - Float.valueOf(transactions.get(pastIndex).getAmount());
            entries.set(index, new PieEntry(newValue, cat));

            transactions.remove(pastIndex);
            Landing.updateChart();
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w(TAG, "category:onCancelled", databaseError.toException());
        }
    };

}
