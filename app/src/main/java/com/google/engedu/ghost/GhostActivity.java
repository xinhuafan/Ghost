package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import static android.view.KeyEvent.KEYCODE_A;
import static android.view.KeyEvent.KEYCODE_Z;


public class GhostActivity extends AppCompatActivity {
    static final String STATE_FRAGMENT = "fragment";
    static final String STATE_STATUS = "userTurn";
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();
    private String fragment = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("words.txt");
             dictionary = new FastDictionary(inputStream);
          //  dictionary = new SimpleDictionary(inputStream);
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }

        onStart(null);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(STATE_FRAGMENT, fragment);
        savedInstanceState.putBoolean(STATE_STATUS, userTurn);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        fragment = savedInstanceState.getString(STATE_FRAGMENT);
        userTurn = savedInstanceState.getBoolean(STATE_STATUS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
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

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode >= KEYCODE_A && keyCode <= KEYCODE_Z) {

            // +68 means to convert keycode to ascii
            String s = Character.toString((char)(keyCode + 68));
            fragment += s;
            TextView gLabel = (TextView) findViewById(R.id.ghostText);
            TextView sLabel = (TextView) findViewById(R.id.gameStatus);
            gLabel.setText(fragment);
            sLabel.setText(COMPUTER_TURN);
            computerTurn();
            return false;
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        fragment = "";
        userTurn = random.nextBoolean();
        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        TextView label = (TextView) findViewById(R.id.gameStatus);
        if (userTurn) {
            label.setText(USER_TURN);
        } else {
            userTurn = false;
            label.setText(COMPUTER_TURN);
            computerTurn();
        }

        return true;
    }

    public boolean onChallenge(View view) {
        TextView sLabel = (TextView) findViewById(R.id.gameStatus);
        TextView gLabel = (TextView) findViewById(R.id.ghostText);
       // String candidate = dictionary.getAnyWordStartingWith(fragment);
        String candidate = dictionary.getGoodWordStartingWith(fragment);
        if ((fragment.length() >= 4 && dictionary.isWord(fragment)) || candidate == null) {
            sLabel.setText("User Wins!");
        } else {
            sLabel.setText("Computer Wins!");
            gLabel.setText(candidate);
        }

        return true;
    }

    private void computerTurn() {
        TextView sLabel = (TextView) findViewById(R.id.gameStatus);
        TextView gLabel = (TextView) findViewById(R.id.ghostText);
        // String candidate = dictionary.getAnyWordStartingWith(fragment);
        String candidate = dictionary.getGoodWordStartingWith(fragment);
        if ((fragment.length() >= 4 && dictionary.isWord(fragment)) || candidate == null) {
            sLabel.setText("Computer wins!");
        } else {
            char next = candidate.charAt(fragment.length());
            boolean isBluff = random.nextBoolean();
            if (isBluff) {
                while (true) {
                    int index = random.nextInt(26) + 97;
                    if (next == (char)index) {
                        continue;
                    }

                    next = (char)index;
                    break;
                }
            }

            fragment += Character.toString(next);
            gLabel.setText(fragment);
            userTurn = true;
            sLabel.setText(USER_TURN);
        }
    }
}
