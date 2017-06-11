package mjsma5.budgey;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.Log;

import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class FirebaseServices extends IntentService {
    /* @Purpose: This class handles all interaction between the application and Firebase.
     *           It runs constantly in the background as a listener, ensuring all updates are
     *           Synchronised to the local storage.
     */

    private static String uID = GoogleSignInActivity.uID;
    private static FirebaseDatabase database = GoogleSignInActivity.database;

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
        /* Purpose: Firebase Even Listener attached to a list of categories.
         *          this listener ensures any updates to the category list are mirrored locally
         */

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            /* When a database child is added, add the same category to the categoryList class*/
            if (!dataSnapshot.getValue().equals("Salary")) {
                categories.addIndCategory(dataSnapshot.getKey(), dataSnapshot.getValue().toString());
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            /* When a database child is removed, remove the same category to the categoryList class*/
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

    // Transaction event listener
    private ChildEventListener transactionsChildEventListener = new ChildEventListener() {
        String TAG = "FIREBASE";
        /* Purpose: Firebase Even Listener attached to a list of Transaction objects.
         *          This listener ensures any updates to the transaction list are mirrored locally.
         *          transactions also update the CategoryList Class and their resective Categories.
         */

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
            /* When a transaction is added, check what type fo transaciton and update list respectively*/
            Log.d(TAG, "onTransactionChildAdded:" + dataSnapshot.getValue());
            Transaction t = dataSnapshot.getValue(Transaction.class);
            t.setId(dataSnapshot.getKey());
            transactions.add(t); // add to Transaction List
            // Chart update
            String cat = t.getCategory();

            if (cat.equals("Salary")) {
                // If transaction is an Income, add to balance and add to salary list
                balance += Double.valueOf(t.getAmount());
                salary.addValueSum(0, Double.valueOf(t.getAmount()));
                salary.addTransaction(0, t);
            } else {
                balance -= Double.valueOf(t.getAmount());
                if (categories.isUsed(cat)) {
                    // If it has been used before update entries list for the PieChart, update category
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
                    // If category hasn't been used, create a new Category class for the category list
                    // and add a new entry.
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
            // remove Item from transaction list

            Transaction t = dataSnapshot.getValue(Transaction.class);
            t.setId(dataSnapshot.getKey());
            int transIndex = -1;
            for (int i = 0; i < transactions.size(); i++) {
                // find transaction index
                if (transactions.get(i).getID().equals(t.getID())) {
                    transIndex = i;
                    break;
                }
            }

            // Chart update
            String cat = t.getCategory();
            if (cat.equals("Salary")) {
                balance -= Double.valueOf(t.getAmount());
                salary.transRemoved(0, transIndex);
                // remove transaction from category list
            } else {
                balance += Double.valueOf(t.getAmount());
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
        /* Removes a transaction from the database */
        database.getReference("users/" + uID + "/transactions").child(id).removeValue();
        Log.d("FIREBASE", "transaction removed " + id);
    }

    public static void deleteCategory(String id) {
        /* Removes a Category from the database */
        database.getReference("users/" + uID + "/categories").child(id).removeValue();
        Log.d("FIREBASE", "category removed " + id);
    }
}
