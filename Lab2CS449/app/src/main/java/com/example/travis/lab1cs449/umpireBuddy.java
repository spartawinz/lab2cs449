package com.example.travis.lab1cs449;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;


// tts doesn't work it just crashes
public class umpireBuddy extends AppCompatActivity {
    /*private int MY_DATA_CHECK_CODE = 0;
    private TextToSpeech myTTS;*/

    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_umpire_buddy);
        // initializes the value of outs keeping cache.
        SharedPreferences pref = this.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        TextView outHandler = (TextView)findViewById(R.id.out_number);
        outHandler.setText(String.valueOf(getInt("outs")));

        /*Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent,MY_DATA_CHECK_CODE);*/


        Button Strike = (Button)findViewById(R.id.Strike);
        Strike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementNum(1);
            }
        });
        Button Ball = (Button)findViewById(R.id.Ball);
        Ball.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementNum(2);
            }
        });
        registerForContextMenu(Ball);
        registerForContextMenu(Strike);
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action_bar,menu);
        return true;
    }
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu,v,menuInfo);
        if(v.getId()==R.id.Strike)
        {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_context_strike,menu);
        }
        if(v.getId()==R.id.Ball)
        {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_context_ball,menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.incrementBall:
                incrementNum(2);
                return true;
            case R.id.decrementBall:
               decrementNum(2);
                return true;
            case R.id.incrementStrike:
                incrementNum(1);
                return true;
            case R.id.decrementStrike:
                decrementNum(1);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                Intent intent = new Intent(umpireBuddy.this, About.class);
                startActivity(intent);
                return true;
            case R.id.reset:
                resetValues();
                return true;
            case R.id.settings:
                getFragmentManager().beginTransaction().replace(android.R.id.content,new Settings()).addToBackStack("Settings").commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // getters and setters of mutating the data
    public int getStrikes()
    {
        TextView handler = (TextView) findViewById(R.id.StrikeNumber);
        return Integer.parseInt(handler.getText().toString());
    }
    public int getBalls()
    {
        TextView handler = (TextView) findViewById(R.id.BallNumber);
        return Integer.parseInt(handler.getText().toString());
    }
    // business logic for data, these mutate the data and throw a popup if the number is reached.
    // see popup for definition.
    public void incrementNum(int item)
    {
        TextView strikeHandler = (TextView)findViewById(R.id.StrikeNumber);
        TextView ballHandler = (TextView) findViewById(R.id.BallNumber);
        int newNumber;
        switch (item)
        {
            case 1:
                newNumber = getStrikes()+1;
                strikeHandler.setText(Integer.toString(newNumber));
                if(getStrikes() > 2)
                {
                    Button strikeBttnHandler = (Button)findViewById(R.id.Strike);
                    strikeBttnHandler.setEnabled(false);
                    popup(1);
                    //myTTS.speak("Out",TextToSpeech.QUEUE_ADD,null,"out");
                    saveInt("outs",getInt("outs")+1);
                    TextView outHandler = (TextView) findViewById(R.id.out_number);
                    outHandler.setText(Integer.toString(getInt("outs")));
                    resetValues();
                }
                break;
            case 2:
                newNumber = getBalls()+1;
                ballHandler.setText(Integer.toString(newNumber));
                if(getBalls() > 3)
                {
                    Button ballBttnHandler = (Button)findViewById(R.id.Ball);
                    ballBttnHandler.setEnabled(false);
                    popup(2);
                    //myTTS.speak("Walk",TextToSpeech.QUEUE_ADD,null,"walk");
                    resetValues();
                }
                break;
            default:
                new Exception("invalid entry.");
        }
    }
    public void decrementNum(int item)
    {
        TextView strikeHandler = (TextView)findViewById(R.id.StrikeNumber);
        TextView ballHandler = (TextView)findViewById(R.id.BallNumber);
        int newNumber;
        switch (item)
        {
            case 1:
                if(getStrikes() != 0)
                {
                    newNumber = getStrikes()-1;
                    strikeHandler.setText(Integer.toString(newNumber));
                }
                break;
            case 2:
                if(getBalls() != 0)
                {
                    newNumber = getBalls()-1;
                    ballHandler.setText(Integer.toString(newNumber));
                }
                break;
            default:
                new Exception("invalid entry.");
        }
    }
    protected void resetValues()
    {
        TextView ballHandler =(TextView) findViewById(R.id.BallNumber);
        ballHandler.setText(String.valueOf(0));
        TextView strikeHandler = (TextView) findViewById(R.id.StrikeNumber);
        strikeHandler.setText(String.valueOf(0));
    }
    // sets the framework for the popup when the strikes or balls go past the bounds of the game.
    protected void popup(int number)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(umpireBuddy.this);
        AlertDialog alert;
        switch (number)
        {
            case 1:
                builder.setMessage(R.string.dialog_out);
                builder.setCancelable(true);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Button strikeBttnHandler = (Button)findViewById(R.id.Strike);
                        strikeBttnHandler.setEnabled(true);
                    }
                });
                alert = builder.create();
                alert.show();
                break;
            case 2:
                builder.setMessage(R.string.dialog_walk);
                builder.setCancelable(true);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Button ballBttnHandler = (Button)findViewById(R.id.Ball);
                        ballBttnHandler.setEnabled(true);
                    }
                });
                alert = builder.create();
                alert.show();
                break;
            default:
                new Exception("Invalid entry.");
        }
    }

    private void saveInt(String key, int value)
    {
        SharedPreferences pref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key,value);
        editor.commit();
    }
    private int getInt(String key)
    {
        SharedPreferences pref = getPreferences(MODE_PRIVATE);
        return pref.getInt(key,0);
    }
}

