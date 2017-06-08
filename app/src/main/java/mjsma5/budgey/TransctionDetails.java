package mjsma5.budgey;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class TransctionDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transction_details);

        Intent intent = getIntent();
        String category = intent.getStringExtra("Category");

        Toast t = new Toast(this);
        t.setText(category);

    }
}
