package mjsma5.budgey;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Matts on 22/05/2017.
 */

public class UpdateReceiver extends BroadcastReceiver {
    private Double balance;

    private UpdateReceiver() {}


    @Override
    public void onReceive(Context context, Intent intent) {
        // balance = intent.getDoubleExtra("balance", );
    }
}
