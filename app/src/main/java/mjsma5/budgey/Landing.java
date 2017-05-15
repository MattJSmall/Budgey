package mjsma5.budgey;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

public class Landing extends AppCompatActivity {

    String uID;
    ArrayList<Transaction> transactions;
    DatabaseReference userRef;
    PieChart pChart;

    List<PieEntry> entries;
    List<String> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        entries = new ArrayList<>();
        categories = new ArrayList<>();
        transactions = new ArrayList<>();

        // Establish connection to Firebase User account
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uID = user.getUid();
        userRef = database.getReference("users/").child(uID);

        pChart = (PieChart) findViewById(R.id.pChart);
        DatabaseReference transRef = database.getReference("users/" + uID + "/transactions");
        transRef.addChildEventListener(childEventListener);

        // Create Chart
        /*
        for (int i = 0; i < transactions.size(); i++) {
            Transaction t = transactions.get(i);
            String cat = t.getCategory();
            if (categories.contains(cat)) {
                Integer index = categories.indexOf(cat);
                entries.set(index, new PieEntry(Float.valueOf(t.getAmount() + entries.get(index).getValue()), cat));
            } else {
                categories.add(t.getCategory());
                entries.add(new PieEntry(Float.valueOf(t.getAmount()), cat));
            }
        }
        PieDataSet set = new PieDataSet(entries, "Spending");
        PieData data = new PieData(set);
        pChart.setData(data);
        pChart.invalidate();
        */

    }

    public void updateChart() {
        PieDataSet set = new PieDataSet(entries, "Spending");
        PieData data = new PieData(set);
        pChart.setData(data);
        pChart.invalidate();
    }

    public void setChartStyling() {
        pChart.setBackgroundColor(16777215);
    }



    public ChildEventListener childEventListener = new ChildEventListener() {
        String TAG = "FIREBASE";
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
            Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
            Transaction t = dataSnapshot.getValue(Transaction.class);
            t.setId(dataSnapshot.getKey());
            transactions.add(t); // add to Transaction List

            // Chart update
            String cat = t.getCategory();
            if (categories.contains(cat)) {
                Integer index = categories.indexOf(cat);
                entries.set(index, new PieEntry(Float.valueOf(t.getAmount()) + entries.get(index).getValue(), cat));
            } else {
                categories.add(t.getCategory());
                entries.add(new PieEntry(Float.valueOf(t.getAmount()), cat));
            }
            updateChart();

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
            updateChart();

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

            transactions.remove(pastIndex);
            updateChart();
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
