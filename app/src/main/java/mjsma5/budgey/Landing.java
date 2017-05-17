package mjsma5.budgey;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Landing extends AppCompatActivity implements View.OnClickListener {

    String uID;
    ArrayList<Transaction> transactions;
    DatabaseReference userRef;
    PieChart pChart;

    List<PieEntry> entries;
    List<String> categories;
    //ArrayList<Integer> colours;
    Integer red;
    Integer green;
    Integer blue;
    ArrayList<Integer> colours;
    Float hue;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        // Establish connection to Firebase User account
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uID = user.getUid();
        userRef = database.getReference("users/").child(uID);

        pChart = (PieChart) findViewById(R.id.pChart);
        DatabaseReference transRef = database.getReference("users/" + uID + "/transactions");
        transRef.addChildEventListener(childEventListener);

        hue = (float) 0;
        colours = new ArrayList<>();

        entries = new ArrayList<>();
        categories = new ArrayList<>();
        transactions = new ArrayList<>();

        findViewById(R.id.btnSignIn).setOnClickListener(this);
        findViewById(R.id.btnCreate).setOnClickListener(this);
        /*
        colours.add(Color.BLUE);
        colours.add(Color.RED);
        colours.add(Color.GREEN);
        colours.add(Color.YELLOW);
        colours.add(Color.CYAN);
        colours.add(Color.MAGENTA);
        colours.add(Color.WHITE);
        colours.add(Color.GRAY);
        */

        // Initialise Colour scheme
        //colours = new ArrayList<>();

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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSignIn:
                Intent sIntent = new Intent(this, Login.class);
                startActivity(sIntent);
                Log.d("REACHED", "intent reached");
                break;
            case R.id.btnCreate:
                Intent cIntent = new Intent(this, CreateTransaction.class);
                startActivity(cIntent);
                Log.d("REACHED", "intent reached");
                break;
        }
    }


    public void updateChart() {
        PieDataSet set = new PieDataSet(entries, "Spending");
        //set.setColors(colours);

        set.setSliceSpace(3f);
        set.setSelectionShift(5f);
        set.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        set.setValueLinePart1OffsetPercentage(80.f);
        set.setValueLinePart1Length(0.7f);
        set.setValueLinePart2Length(0.4f);
        set.setColors(ColorTemplate.VORDIPLOM_COLORS);
        set.setValueTextColor(Color.GRAY);
        set.setValueTextSize(14);


        set.setValueTextColor(Color.BLACK);
        set.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        
        PieData data = new PieData(set);
        data.setValueTextColor(Color.BLACK);
        
        pChart.setData(data);

        pChart.setEntryLabelColor(Color.BLACK);
        pChart.setEntryLabelTextSize(18f);

        // undo all highlights
        pChart.highlightValues(null);

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
