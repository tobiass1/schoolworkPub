package xyz.tobiassen.priller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This class was made for training trough the Android Developers Tutorial.
 * Source: https://developer.android.com/training/index.html
 */

public class DisplayMessageActivity extends AppCompatActivity {


    /**
     * Function to display a message that the user entered in the startMenu activity.
     * Made solely for training purposes.
     * Gets the extrastring from the intent trough getStringExtra();
     * sets it to textView
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String message = intent.getStringExtra(startMenu.EXTRA_MESSAGE);
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(message);
        setContentView(textView);
    }

    /**
     * Creates
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action_bar, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Handles what happens when menuButtons are pushed
     * Only openSettings and Search is dummy functions, only containing a Toast to see if
     * works.
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_search:
                openSearch();
                return true;
            case R.id.action_settings:
                openSettings();
                return true;
            default:
                super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Dummy with Toast
     */
    private void openSearch() {
        Toast.makeText(this, "Search button pressed", Toast.LENGTH_SHORT).show();
    }
    /**
     * Dummy with Toast
     */
    private void openSettings() {
        Toast.makeText(this, "Settings button pressed", Toast.LENGTH_SHORT).show();
    }

    /**
     * When push back button, the current animation occurs.
     */
    @Override
    protected void onPause() {
        super.onPause();

        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

}
