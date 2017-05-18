package mjsma5.budgey;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

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

    ArrayList<Transaction> transactions;
    DatabaseReference userRef;
    private static PieChart pChart;
    private static ArrayList<Integer> colours;

    public static List<PieEntry> entries;

    //ArrayList<Integer> colours;
    Float hue;

    String uID;
    public static FirebaseDatabase database;
    DatabaseReference transRef;
    DatabaseReference catRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        // Establish connection to Firebase User account
        /*
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uID = user.getUid();
        userRef = database.getReference("users/").child(uID);
        */

        entries = FirebaseServices.entries;

        pChart = (PieChart) findViewById(R.id.pChart);

        hue = (float) 0;
        colours = new ArrayList<>();
        transactions = new ArrayList<>();

        findViewById(R.id.btnSignIn).setOnClickListener(this);
        findViewById(R.id.btnCreate).setOnClickListener(this);

        colours.add(Color.BLUE);
        colours.add(Color.RED);
        colours.add(Color.GREEN);
        colours.add(Color.YELLOW);
        colours.add(Color.CYAN);
        colours.add(Color.MAGENTA);
        colours.add(Color.WHITE);
        colours.add(Color.GRAY);

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


    public static void updateChart() {
        PieDataSet set = new PieDataSet(entries, "Spending");
        //set.setColors(colours);

        set.setSliceSpace(3f);
        set.setSelectionShift(8f);
        set.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        set.setValueLinePart1OffsetPercentage(80.f);
        set.setValueLinePart1Length(0.4f);
        set.setValueLinePart2Length(0.2f);
        set.setColors(ColorTemplate.VORDIPLOM_COLORS);
        set.setValueTextSize(14);
        set.setColors(colours);


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

    

}
