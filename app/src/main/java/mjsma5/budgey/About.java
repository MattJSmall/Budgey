package mjsma5.budgey;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView txtDescription = (TextView) findViewById(R.id.txtDescription);

        txtDescription.setText(
                "Application Name: Budgey \n" +
                "Creator: Matt Small \n\n" +
                "MP Android Charts:\n" +
                "Copyright 2017 Matthew Small\n" +
                        "\n" +
                        "Licensed under the Apache License, Version 2.0 (the \"License\");" +
                        "you may not use this file except in compliance with the License." +
                        "You may obtain a copy of the License at\n" +
                        "\n" +
                        "    http://www.apache.org/licenses/LICENSE-2.0\n" +
                        "\n" +
                        "Unless required by applicable law or agreed to in writing, software" +
                        "distributed under the License is distributed on an \"AS IS\" BASIS," +
                        "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied." +
                        "See the License for the specific language governing permissions and" +
                        "limitations under the License." +
                "\n\n" +
                "Exp4j (Mathematical Evaluation Library)\n" +
                "Copyright 2017 Matthew Small\n" +
                        "\n" +
                        "Licensed under the Apache License, Version 2.0 (the \"License\");" +
                        "you may not use this file except in compliance with the License." +
                        "You may obtain a copy of the License at\n" +
                        "\n" +
                        "    http://www.apache.org/licenses/LICENSE-2.0\n" +
                        "\n" +
                        "Unless required by applicable law or agreed to in writing, software" +
                        "distributed under the License is distributed on an \"AS IS\" BASIS," +
                        "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied." +
                        "See the License for the specific language governing permissions and" +
                        "limitations under the License."
        );
    }
}
