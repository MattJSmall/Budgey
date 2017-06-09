package mjsma5.budgey;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Matts on 23/05/2017.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context _context;
    private List<String> _listDataHeader;
    private HashMap<String,List<String>> _listHashMap;
    // private static CategoryList categories;
    private static ArrayList<Transaction> transactions;
    //private static CategoryList categories;


    public ExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<String>> listHashMap) {
        // categories = FirebaseServices.categories;
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listHashMap = listHashMap;
        transactions = FirebaseServices.transactions;
        //categories = FirebaseServices.categories;

    }

    /* [Swipe action to delete START} */


    @Override
    public int getGroupCount() {
        return _listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return _listHashMap.get(_listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return _listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this._listHashMap.get(this._listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, null);
        }
        TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
        TextView lblCatCost = (TextView) convertView.findViewById(R.id.txtCatBalance);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        // Update Category balance text
        Double catBalance;
        if (headerTitle.equals("Salary")) {
            catBalance = FirebaseServices.salary.get(0).getValueSum();
            lblCatCost.setTextColor(ContextCompat.getColor(_context, R.color.positive));
        } else {
            catBalance = FirebaseServices.categories.getCategory(headerTitle).getValueSum();
            lblCatCost.setTextColor(ContextCompat.getColor(_context, R.color.negative));
        }
        lblCatCost.setText(String.valueOf(catBalance));
        Log.d("LISTVIEW", "Balance for " + headerTitle + " = " + catBalance);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate( R.layout.list_item, null);
        }
        TextView txtPrice = (TextView) convertView.findViewById(R.id.lblPrice);
        TextView txtNote = (TextView) convertView.findViewById(R.id.lblNote);
        TextView txtDate = (TextView) convertView.findViewById(R.id.lblDate);
        Button btnDelete = (Button) convertView.findViewById(R.id.btnDelete);

        final Transaction t = transactions.get(Integer.valueOf(childText));

        txtPrice.setText("$" + t.getAmount());
        txtNote.setText(t.getNote());
        Calendar c = Calendar.getInstance();
        String[] date = t.getDate().split(" ");
        c.set(Integer.valueOf(date[2]), Integer.valueOf(date[1]), Integer.valueOf(date[0])) ;
        txtDate.setText(new SimpleDateFormat("EE d, MMMM").format(c.getTime()));

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseServices.deleteTransaction(t.getID());
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
