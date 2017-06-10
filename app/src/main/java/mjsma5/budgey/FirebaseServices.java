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
import android.widget.Toast;

import com.github.mikephil.charting.data.Entry;
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
    public static CategoryList salary = new CategoryList();

    // Colour variables
    public static ArrayList<Color> colours = new ArrayList<>();
    private Integer numColours = 0;

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
        salary.addItem("Salary", 0d);
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    private ChildEventListener categoryListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            categories.addIndCategory(dataSnapshot.getValue().toString());
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            categories.removeCategory(dataSnapshot.getValue().toString());
            Log.d("FIREBASE", "category child removed: " + dataSnapshot.getValue());
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
    private ChildEventListener transactionsChildEventListener = new ChildEventListener() {
        String TAG = "FIREBASE";
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
            Log.d(TAG, "onTransactionChildAdded:" + dataSnapshot.getValue());
            Transaction t = dataSnapshot.getValue(Transaction.class);
            t.setId(dataSnapshot.getKey());
            transactions.add(t); // add to Transaction List
            // Chart update
            String cat = t.getCategory();

            if (cat.equals("Salary")) {
                balance += Double.valueOf(t.getAmount());
                salary.addValueSum(0, Double.valueOf(t.getAmount()));
                salary.addTransaction(0, t);
            } else {
                balance -= Double.valueOf(t.getAmount());
                if (categories.isUsed(cat)) {
                    Integer index = -1;
                    Log.d("CATEGORY", cat);
                    for (int i = 0; i < entries.size(); i++) {
                        PieEntry entry = entries.get(i);
                        Log.d("ENTRY", entry.getLabel());
                        if (entries.get(i).getLabel().equals(cat)) {
                            index = i;
                            Log.d("ENTRY_INDEX", String.valueOf(i));
                            break;
                        }
                        Log.d("ENTRY_INDEX", String.valueOf(index));
                    }
                    if (index != -1) {
                        entries.set(index, new PieEntry(Float.valueOf(t.getAmount()) + entries.get(index).getValue(), cat));
                        categories.addValueSum(index, Double.valueOf(t.getAmount()));
                        Log.d("CHART", "entry_successful");
                    } else {
                        Log.d("CHART", "entry_failed");
                    }
                } else {
                    categories.addItem(cat, Double.valueOf(t.getAmount()));
                    entries.add(new PieEntry(Float.valueOf(t.getAmount()), cat));
                }
                categories.addTransaction(categories.indexOf(cat), t);
            }
            Log.d("UPDATE", "balance: " + balance);
            Landing.update();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
            Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

            Transaction t = dataSnapshot.getValue(Transaction.class);
            t.setId(dataSnapshot.getKey());
            int transIndex = -1;
            for (int i = 0; i < transactions.size(); i++) {
                if (transactions.get(i).getID().equals(t.getID())) {
                    transIndex = i;
                    break;
                }
            }

            // Chart update
            String cat = t.getCategory();
            if (cat.equals("Salary")) {
                salary.transRemoved(0, transIndex);
            } else {
                int index = categories.indexOf(cat);
                categories.transRemoved(index, transIndex);
                for (int i = 0; i < entries.size(); i++) {
                    if (entries.get(i).getLabel().equals(cat)) {
                        index = i;
                        break;
                    }
                }
                Float entryValue = entries.get(index).getValue() - Float.valueOf(t.getAmount());
                if (entryValue == 0) {
                    entries.remove(index);
                } else {
                    entries.set(index, new PieEntry(entryValue, cat));
                }
            }
            transactions.remove(transIndex);
            Log.d("UPDATE", "balance: " + balance);
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

    public static void deleteTransaction(String id) {
        database.getReference("users/" + uID + "/transactions").child(id).removeValue();
        Log.d("FIREBASE", "transaction removed " + id);
    }

    public static void deleteCategory(String id) {
        database.getReference("users/" + uID + "/categories").child(id).removeValue();
        Log.d("FIREBASE", "category removed " + id);
    }

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
