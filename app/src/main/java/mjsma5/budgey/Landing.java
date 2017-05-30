package mjsma5.budgey;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    public boolean down;

    private ImageView arrowLeft;
    private ImageView arrowRight;

    public static Double balance;
    public static Button btnBalance;
    ArrayList<Transaction> transactions;
    private static PieChart pChart;
    private static ArrayList<Integer> colours;

    public static List<PieEntry> entries;
    //ArrayList<Integer> colours;
    Float hue;

    private String TAG;

    public static PieData data;

    private static CategoryList categories;

    private LinearLayout balanceBar;
    private static DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        userRef = GoogleSignInActivity.userRef;
        userRef.child("transactions").addListenerForSingleValueEvent(new ValueEventListener() {
            // Create default categories for new user
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    createNegativeTransaction();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        categories = FirebaseServices.categories;
        entries = FirebaseServices.entries;
        pChart = (PieChart) findViewById(R.id.pChart);
        setChartStyling();
        hue = (float) 0;
        colours = new ArrayList<>();
        btnBalance = (Button) findViewById(R.id.btnBalance);
        down = true;

        findViewById(R.id.btnBalance).setOnClickListener(this);
        findViewById(R.id.btnPos).setOnClickListener(this);
        findViewById(R.id.btnNeg).setOnClickListener(this);

        arrowLeft = (ImageView) findViewById(R.id.imgLeftArrow);
        arrowRight = (ImageView) findViewById(R.id.imgRightArrow);

        balanceBar = (LinearLayout) findViewById(R.id.linBalanceBar);
        findViewById(R.id.linBalanceBar).setOnClickListener(this);

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
            case R.id.linBalanceBar:
                translateList();
                break;
            case R.id.btnPos:
                createTransaction();
                break;
            case R.id.btnNeg:
                createNegativeTransaction();
                break;
        }
    }

    private void translateList() { // dir either 1 or -1 for direction of rotation
        RotateAnimation rotateClock = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateClock.setDuration(700);
        RotateAnimation rotateAntiClock = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAntiClock.setDuration(700);
        if (down) {
            arrowRight.setAnimation(rotateAntiClock);
            arrowLeft.setAnimation(rotateClock);
        } else {
            arrowRight.setAnimation(rotateClock);
            arrowLeft.setAnimation(rotateAntiClock);
        }
        down = !down;
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

    private void createTransaction() {
        Intent posIntent = new Intent(this, CreateTransaction.class);
        posIntent.putExtra("type", true);
        posIntent.putExtra("category", "Salary");
        startActivity(posIntent);
        Log.d("REACHED", "intent reached");
    };

    private void createNegativeTransaction() {
        Intent negIntent = new Intent(this, CreateTransaction.class);
        negIntent.putExtra("type", false);
        negIntent.putExtra("category", "NULL");
        startActivity(negIntent);
        Log.d("REACHED", "intent reached");
    }
    /*

     Animation animation = AnimationUtils.loadAnimation(getContext(), (position > lastPosition) ? R.anim.up_from_bottom);
     ////item/////.startAnimation(animation);
     lastPosition = position;
     */

}
