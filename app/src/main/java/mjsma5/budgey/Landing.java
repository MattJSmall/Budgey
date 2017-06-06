package mjsma5.budgey;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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
    public ExpandableListView listView;
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

    private Animation slideUp;
    private Animation slideDown;

    private ViewGroup.LayoutParams downListParams;
    private ViewGroup.LayoutParams upListParams;

    private int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        // Toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        // Checking if this is a new user (no transactions created) and will force user to create a transaction.
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

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        height = size.y;

        findViewById(R.id.btnBalance).setOnClickListener(this);
        findViewById(R.id.btnPos).setOnClickListener(this);
        findViewById(R.id.btnNeg).setOnClickListener(this);

        arrowLeft = (ImageView) findViewById(R.id.imgLeftArrow);
        arrowRight = (ImageView) findViewById(R.id.imgRightArrow);

        balanceBar = (LinearLayout) findViewById(R.id.linBalanceBar);

        findViewById(R.id.imgRightArrow).setOnClickListener(this);
        findViewById(R.id.imgLeftArrow).setOnClickListener(this);

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

        // Animations

        //listLayout = (ConstraintLayout) findViewById(R.id.conListLayout);
    }

    private static void updateListView() {
        listDataHeader.clear();
        listHash.clear();
        Log.d("EXP_LIST_VIEW_headers: ", categories.getList().toString());
        ArrayList<Category> tmpList = categories.getList();
        for (int i = 0; i < tmpList.size()-1 ; i++) {
            Category c = tmpList.get(i);
            if (!listDataHeader.contains(c.getValue())) {
                listDataHeader.add(c.getValue());
                Log.d("HEADER", c.getValue());
            }
            listHash.remove(c.getValue());
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
        updateListView();
        btnBalance.setText("Balance: " + String.valueOf(balance));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgLeftArrow:
                translateList();
                break;
            case R.id.imgRightArrow:
                translateList();
                break;
            case R.id.btnBalance:
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

    private void translateList() {
        if (down) {
            arrowLeft.animate().rotation(-180).start();
            arrowRight.animate().rotation(180).start();

            downListParams = listView.getLayoutParams();
            downListParams.height = (int) (height * 0.9);
            listView.setLayoutParams(downListParams);

            balanceBar.animate().translationY((int) (height * -.45));
            listView.animate().translationY((int) (height * -.45));


        } else {
            arrowLeft.animate().rotation(0).start();
            arrowRight.animate().rotation(-0).start();

            listView.animate().translationY(0f);
            balanceBar.animate().translationY(0f);

            downListParams = listView.getLayoutParams();
            downListParams.height = ((int) (height * .55));
            listView.setLayoutParams(downListParams);
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


    /***************************************************************/

}
