package mjsma5.budgey;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Button;

import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Comment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    public static Double balance = 0d;
    public static CategoryList categories = new CategoryList();
    public static ArrayList<Transaction> transactions = new ArrayList<>();

    public static List<PieEntry> entries = new ArrayList<>();

    public static DatabaseReference transRef = GoogleSignInActivity.transRef;

    public FirebaseServices() {
        super("FirebaseServices");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        transRef.addChildEventListener(transactionsChildEventListener);
        catRef.addChildEventListener(categoryListener);
        Log.d("FIREBASE", "START");
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    public ChildEventListener categoryListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            entries.add(new PieEntry(0f, dataSnapshot.getValue().toString()));
            if (!categories.contains(dataSnapshot.getValue().toString())) {
                categories.addItem(dataSnapshot.getKey(), dataSnapshot.getValue().toString(), 0d);
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        }
        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
        }
        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };


    // [Entry, x: 0.0 y: 237.0, Entry, x: 0.0 y: 263.0, Entry, x: 0.0 y: 104.0, Entry, x: 0.0 y: 64.0, Entry, x: 0.0 y: 52.0]
    // Transaction event listener
    public ChildEventListener transactionsChildEventListener = new ChildEventListener() {
        String TAG = "FIREBASE";
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
            Log.d(TAG, "onTransactionChildAdded:" + dataSnapshot.getValue());
            Transaction t = dataSnapshot.getValue(Transaction.class);
            t.setId(dataSnapshot.getKey());
            transactions.add(t); // add to Transaction List

            // Chart update
            String cat = t.getCategory();
            Integer index = categories.indexOf(cat);
            if (!cat.equals("Salary")) {
                balance -= Double.valueOf(t.getAmount());
                if (categories.contains(cat)) {
                    entries.set(index, new PieEntry(Float.valueOf(t.getAmount()) + entries.get(index).getValue(), cat));
                    categories.addValueSum(index, Double.valueOf(t.getAmount()));
                } else {
                    entries.add(new PieEntry(Float.valueOf(t.getAmount()), cat));
                    categories.addItem(dataSnapshot.getKey(), cat, Double.valueOf(t.getAmount()));
                }
                Log.d(TAG, "onEntryChildAdded:" + dataSnapshot.getValue());
            } else {
                if (categories.contains(cat)) {
                    categories.addValueSum(index, Double.valueOf(t.getAmount()));
                } else {
                    categories.addItem(dataSnapshot.getKey(), cat, Double.valueOf(t.getAmount()));
                }
                balance += Double.valueOf(t.getAmount());

            }
            categories.addTransaction(categories.indexOf(cat), t);
            Log.d("UPDATE", "balance: " + balance);
            Landing.update();
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
            Integer index = categories.indexOf(cat);
            // mew value must first remove past t value
            Float newValue = Float.valueOf(newTransaction.getAmount())
                    - Float.valueOf(transactions.get(pastIndex).getAmount())
                    + entries.get(index).getValue();
            entries.set(index, new PieEntry(newValue, cat));

            // Transaction List update
            transactions.set(pastIndex, newTransaction);
            Landing.update();

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
            Integer index = categories.indexOf(cat);
            // mew value must first remove past t value
            Float newValue = entries.get(index).getValue()
                    - Float.valueOf(transactions.get(pastIndex).getAmount());
            entries.set(index, new PieEntry(newValue, cat));
            categories.transRemoved(index, Double.valueOf(transactions.get(pastIndex).getAmount()));
            transactions.remove(pastIndex);
            Landing.update();
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

    private final IBinder mBinder = new LocalBinder();
    // Random number generator
    private final Random mGenerator = new Random();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        FirebaseServices getService() {
            // Return this instance of LocalService so clients can call public methods
            return FirebaseServices.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /** method for clients */
    public int getRandomNumber() {
        return mGenerator.nextInt(100);
    }
}
