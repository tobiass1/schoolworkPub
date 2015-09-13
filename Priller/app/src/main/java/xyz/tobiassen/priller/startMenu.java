package xyz.tobiassen.priller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


/**
 * Main class of the Application.
 * This calls the DisplayMessageActivity and MapsActivity on button pushes.
 */
public class startMenu extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "xyz.tobiassen.priller.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start_menu, menu);
        return true;
    }

    /**
     * Function creates intent, and starts the mapsActivity
     * @param v
     */
    public void enterMap(View v) {
        final Intent i = new Intent(this, MapsActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Creates an intent, and puts the text the user inputed into editText element
     * into the intent using the putExtra. starts the activity trough startActivity()
     * @param view
     */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    /**
     * Dummy intent for training on creating intents and starting the activities.
     * @param f
     */
    public void launchSecondActivity(View f){
        final Intent i = new Intent(this, secondActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

    }
}




