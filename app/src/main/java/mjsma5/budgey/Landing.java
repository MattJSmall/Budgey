package mjsma5.budgey;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Landing extends AppCompatActivity implements View.OnClickListener {

    // ListView Items
    private ExpandableListView listView;
    private static ExpandableListAdapter listAdapter;
    private static List<String> listDataHeader;
    private static HashMap<String, List<String>> listHash;


    public static Double balance;
    public static Button btnBalance;
    ArrayList<Transaction> transactions;
    DatabaseReference userRef;
    private static PieChart pChart;
    private static ArrayList<Integer> colours;

    public static List<PieEntry> entries;
    //ArrayList<Integer> colours;
    Float hue;

    private String TAG;

    public static PieData data;

    private static CategoryList categories;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        categories = FirebaseServices.categories;
        entries = FirebaseServices.entries;
        pChart = (PieChart) findViewById(R.id.pChart);
        setChartStyling();
        hue = (float) 0;
        colours = new ArrayList<>();
        btnBalance = (Button) findViewById(R.id.btnBalance);

        findViewById(R.id.btnBalance).setOnClickListener(this);
        findViewById(R.id.btnPos).setOnClickListener(this);
        findViewById(R.id.btnNeg).setOnClickListener(this);

        colours.add(Color.BLUE);
        colours.add(Color.RED);
        colours.add(Color.GREEN);
        colours.add(Color.YELLOW);
        colours.add(Color.CYAN);
        colours.add(Color.MAGENTA);
        colours.add(Color.WHITE);
        colours.add(Color.GRAY);
        initiateChart();

        // List Instance
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();
        listView = (ExpandableListView) findViewById(R.id.lvTransactions);
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listHash);
        listView.setAdapter(listAdapter);
    }

    private static void updateListView() {
        Log.d("EXP_LIST_VIEW_headers: ", categories.getList().toString());
        ArrayList<Category> tmpList = categories.getList();
        for (int i = 0; i < tmpList.size()-1 ; i++) {
            Category c = tmpList.get(i);
            if (!listDataHeader.contains(c.getValue())) {
                listDataHeader.add(c.getValue());
            }
            listHash.put(c.getValue(), c.getTransactions());
        }
        listAdapter.notifyDataSetChanged();
    }

    public static void update() {
        updateBalance();
        updateChart();
        updateListView();
        Log.d("UPDATE", "success");
    }

    @Override
    protected void onStart() {
        super.onStart();
        pChart.notifyDataSetChanged();
        pChart.invalidate();
        btnBalance.setText("Balance: " + String.valueOf(balance));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnBalance:
                break;
            case R.id.btnPos:
                Intent posIntent = new Intent(this, CreateTransaction.class);
                posIntent.putExtra("type", true);
                posIntent.putExtra("category", "Salary");
                startActivity(posIntent);
                Log.d("REACHED", "intent reached");
                break;
            case R.id.btnNeg:
                Intent negIntent = new Intent(this, CreateTransaction.class);
                negIntent.putExtra("type", false);
                negIntent.putExtra("category", "NULL");
                startActivity(negIntent);
                Log.d("REACHED", "intent reached");
                break;
        }
    }

    public void initiateChart() {
        pChart.clear();
        PieDataSet set = new PieDataSet(entries, "Spending");

        set.setSliceSpace(3f);
        set.setSelectionShift(8f);

        set.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        set.setValueLinePart1OffsetPercentage(60.f);
        set.setValueLinePart1Length(0.4f);
        set.setValueLinePart2Length(0.2f);

        set.setValueTextSize(14);
        set.setColors(colours);
        set.setValueTextColor(Color.BLACK);

        data = new PieData(set);
        data.setValueTextColor(Color.BLACK);
        pChart.setData(data);
    }

    public static void updateChart() {
        data.notifyDataChanged();
        pChart.notifyDataSetChanged();
        pChart.invalidate();
        Log.d("CHART", "Chart Created   " + entries);
    }


    public void setChartStyling() {
        // Styling options and initialisation for PieChart
        pChart.getLegend().setEnabled(false);
        pChart.setEntryLabelColor(Color.BLACK);
        pChart.setEntryLabelTextSize(18f);
        pChart.setExtraOffsets(20f, 20f, 20f, 20f);
        pChart.setBackgroundColor(16777215);
        pChart.setNoDataText("Loading...");
        Description des = pChart.getDescription();
        des.setEnabled(false);
    }

    public static void updateBalance() {
        balance = FirebaseServices.balance;
        btnBalance.setText("Balance: " + String.valueOf(balance));
        if (balance >= 0) {
            btnBalance.setBackgroundColor(Color.GREEN);
        } else {
            btnBalance.setBackgroundColor(Color.RED);
        }
    }
    // Swipe Gestures

    @Override
    public boolean onTouchEvent(MotionEvent event){

        int action = MotionEventCompat.getActionMasked(event);
        TAG = "GESTURE";
        switch(action) {
            case (MotionEvent.ACTION_DOWN) :
                Log.d(TAG,"Action was DOWN");
                return true;
            case (MotionEvent.ACTION_MOVE) :
                Log.d(TAG,"Action was MOVE");
                return true;
            case (MotionEvent.ACTION_UP) :
                Log.d(TAG,"Action was UP");
                return true;
            case (MotionEvent.ACTION_CANCEL) :
                Log.d(TAG,"Action was CANCEL");
                return true;
            case (MotionEvent.ACTION_OUTSIDE) :
                Log.d(TAG,"Movement occurred outside bounds " +
                        "of current screen element");
                return true;
            default :
                return super.onTouchEvent(event);
        }
    }
    /*

     Animation animation = AnimationUtils.loadAnimation(getContext(), (position > lastPosition) ? R.anim.up_from_bottom);
     ////item/////.startAnimation(animation);
     lastPosition = position;
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listHash = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Top 250");
        listDataHeader.add("Now Showing");
        listDataHeader.add("Coming Soon..");

        // Adding child data
        List<String> top250 = new ArrayList<String>();
        top250.add("The Shawshank Redemption");
        top250.add("The Godfather");
        top250.add("The Godfather: Part II");
        top250.add("Pulp Fiction");
        top250.add("The Good, the Bad and the Ugly");
        top250.add("The Dark Knight");
        top250.add("12 Angry Men");

        List<String> nowShowing = new ArrayList<String>();
        nowShowing.add("The Conjuring");
        nowShowing.add("Despicable Me 2");
        nowShowing.add("Turbo");
        nowShowing.add("Grown Ups 2");
        nowShowing.add("Red 2");
        nowShowing.add("The Wolverine");

        List<String> comingSoon = new ArrayList<String>();
        comingSoon.add("2 Guns");
        comingSoon.add("The Smurfs 2");
        comingSoon.add("The Spectacular Now");
        comingSoon.add("The Canyons");
        comingSoon.add("Europa Report");

        listHash.put(listDataHeader.get(0), top250); // Header, Child data
        listHash.put(listDataHeader.get(1), nowShowing);
        listHash.put(listDataHeader.get(2), comingSoon);
    }

    

}
