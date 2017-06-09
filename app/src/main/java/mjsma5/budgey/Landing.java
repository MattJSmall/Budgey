package mjsma5.budgey;

import android.accounts.Account;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.PublicKey;
import java.text.DecimalFormat;
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

    private Context context;

    private ImageView arrowLeft;
    private ImageView arrowRight;

    public static Double balance;
    public static Button btnBalance;
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

    private ViewGroup.LayoutParams listParams;

    private int height;

    private MyValueFormatter formatter;

    public AlertDialog.Builder deleteMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        // Toolbar


        context = this;

        // Checking if this is a new user (no transactions created) and will force user to create a transaction.
        userRef = GoogleSignInActivity.userRef;
        userRef.child("transactions").addListenerForSingleValueEvent(new ValueEventListener() {
            // Create default categories for new user
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    createTransaction();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
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

        arrowLeft = (ImageView) findViewById(R.id.imgLeftArrow);
        arrowRight = (ImageView) findViewById(R.id.imgRightArrow);

        balanceBar = (LinearLayout) findViewById(R.id.linBalanceBar);

        findViewById(R.id.imgRightArrow).setOnClickListener(this);
        findViewById(R.id.imgLeftArrow).setOnClickListener(this);

        colours.add(Color.parseColor("#ffcdd2"));
        colours.add(Color.parseColor("#f8bbd0"));
        colours.add(Color.parseColor("#e1bee7"));
        colours.add(Color.parseColor("#d1c4e9"));
        colours.add(Color.parseColor("#c5cae9"));
        colours.add(Color.parseColor("#bbdefb"));
        colours.add(Color.parseColor("#b3e5fc"));
        colours.add(Color.parseColor("#b2ebf2"));
        colours.add(Color.parseColor("#b2dfdb"));
        colours.add(Color.parseColor("#c8e6c9"));
        colours.add(Color.parseColor("#dcedc8"));

        /* TODO
        colours.add(Color.parseColor("#"));
        colours.add(Color.parseColor("#"));
        colours.add(Color.parseColor("#"));
        colours.add(Color.parseColor("#"));
        colours.add(Color.parseColor("#"));
        colours.add(Color.parseColor("#"));
        colours.add(Color.parseColor("#"));
        */
        listView = (ExpandableListView) findViewById(R.id.lvTransactions);
        initiateChart();
        instanceList();

        // List instance sizing
        listParams = listView.getLayoutParams();
        listParams.height = ((int) (height * .30));
        listView.setLayoutParams(listParams);

        formatter = new MyValueFormatter();

        deleteMenu = new AlertDialog.Builder(this);
        deleteMenu.setTitle("Select a Category to Delete");
    }

    public void instanceList() {
        // List Instance
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listHash);
        listView.setAdapter(listAdapter);
    }

    // Appbar Menu Start
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_categories:
                manageCategories();
                break;
            // action with ID action_settings was selected
            case R.id.action_account:
                Intent intent = new Intent(this, UserAccount.class);
                startActivity(intent);
                break;
            case R.id.action_about:
                Intent aboutIntent = new Intent(this, About.class);
                startActivity(aboutIntent);
                break;
            default:
                break;
        }
        return true;
    }

    /* App bar menu END */
    private static void updateListView() {
        listDataHeader.clear();
        listHash.clear();
        Log.d("EXP_LIST_VIEW_headers: ", categories.getList().toString());
        ArrayList<Category> tmpList = categories.getList();
        for (int i = 0; i < tmpList.size() - 1; i++) {
            Category c = tmpList.get(i);
            if (!listDataHeader.contains(c.getValue())) {
                if (c.getTransactions().size() != 0) {
                    if (c.getValueSum() != 0) {
                        listDataHeader.add(c.getValue());
                        Log.d("HEADER", c.getValue());
                        listHash.remove(c.getValue());
                        listHash.put(c.getValue(), c.getTransactions());
                    }
                }
            }
        }
        listDataHeader.add("Salary");
        listHash.put("Salary", FirebaseServices.salary.get(0).getTransactions());
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
        updateBalance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgLeftArrow:
                translateList(2);
                break;
            case R.id.imgRightArrow:
                translateList(2);
                break;
            case R.id.btnBalance:
                translateList(2);
                break;
            case R.id.btnPos:
                createTransaction();
                break;
        }
    }

    private void translateList(Integer direction) {
        /*
         * @Param: direction values; 0:up, 1:down, 2:toggle
         */
        switch (direction) {
            case 0:
                if (down) {
                    translateUp();
                }
                break;
            case 1:
                if (!down) {
                    translateDown();
                }
                break;
            case 2:
                if (down) {
                    translateUp();
                } else {
                    translateDown();
                }
                break;
        }
    }

    private void translateDown() {
        arrowLeft.animate().rotation(0).start();
        arrowRight.animate().rotation(-0).start();

        listView.animate().translationY(0f);
        balanceBar.animate().translationY(0f);

        listParams = listView.getLayoutParams();
        listParams.height = ((int) (height * .30));
        listView.setLayoutParams(listParams);
        down = !down;
    }

    private void translateUp() {
        arrowLeft.animate().rotation(-180).start();
        arrowRight.animate().rotation(180).start();

        listParams = listView.getLayoutParams();
        listParams.height = (int) (height * 0.75);
        listView.setLayoutParams(listParams);

        balanceBar.animate().translationY((int) (height * -.45));
        listView.animate().translationY((int) (height * -.45));
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
        data.setValueFormatter(formatter);
        data.setValueTextColor(Color.BLACK);
        // data.setValueFormatter(new MyValueFormatter());
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
            btnBalance.setTextColor(Color.parseColor("#64dd17"));
        } else {
            btnBalance.setTextColor(Color.parseColor("#d50000"));
        }
    }
    // Swipe Gestures

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        TAG = "GESTURE";
        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                Log.d(TAG, "Action was DOWN");
                translateList(1);
                return true;
            case (MotionEvent.ACTION_MOVE):
                Log.d(TAG, "Action was MOVE");
                return true;
            case (MotionEvent.ACTION_UP):
                Log.d(TAG, "Action was UP");
                translateList(0);
                return true;
            case (MotionEvent.ACTION_CANCEL):
                Log.d(TAG, "Action was CANCEL");
                return true;
            case (MotionEvent.ACTION_OUTSIDE):
                Log.d(TAG, "Movement occurred outside bounds " +
                        "of current screen element");
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    private void createTransaction() {
        Intent posIntent = new Intent(this, CreateTransaction.class);
        posIntent.putExtra("category", "null");
        startActivity(posIntent);
        Log.d("REACHED", "intent reached");
    }

    /***************************************************************/

    private void manageCategories () {
        final String[] menuItems = categories.getAll();
        deleteMenu.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        deleteMenu.setItems(menuItems,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, final int index) {
                        AlertDialog.Builder deleteAlert = new AlertDialog.Builder(context);
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        FirebaseServices.deleteCategory(categories.get(index).getKey());
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        break;
                                }
                            }
                        };

                        deleteAlert.setTitle("WARNING!");
                        deleteAlert.setMessage("Are you sure you want to delete this category? " +
                                "This will remove all related transactions! ")
                                .setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();
                    }
                });
        deleteMenu.show();
    }

}

class MyValueFormatter implements IValueFormatter {

    private DecimalFormat mFormat;

    public MyValueFormatter() {
        mFormat = new DecimalFormat("###,###,##0.00");
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {

        if(value > 0) {
            return mFormat.format(value);
        } else {
            return "";
        }
    }
}

// set creator

