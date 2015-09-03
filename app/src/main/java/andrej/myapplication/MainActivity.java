package andrej.myapplication;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Locale;


public class MainActivity extends ActionBarActivity implements TextToSpeech.OnInitListener {
    DbHelper db;
    TextToSpeech engine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DbHelper(getApplicationContext());

        Intent intent = getIntent();
        updateBtns();

        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, 0);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            engine.setLanguage(new Locale("en_UK"));
        }
        else if (status == TextToSpeech.ERROR) {
            Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                engine = new TextToSpeech(this, this);
            }
            else {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    public void addBtn(View view) {
        Intent intent = new Intent(this, AddActivity.class);
        startActivity(intent);
    }

    //should load all btns from db
    public void updateBtns() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.btnsLayout);
        ll.removeAllViews();

        SQLiteDatabase readDb = db.getReadableDatabase();
        String selectQuery = "SELECT * FROM btns";
        Cursor c = readDb.rawQuery(selectQuery, new String[] {});
        while (c.moveToNext()) {
            String id = c.getString(c.getColumnIndex("_id"));
            String name = c.getString(c.getColumnIndex("name"));
            String text = c.getString(c.getColumnIndex("text"));
            String language = c.getString(c.getColumnIndex("language"));

            Button myButton = new Button(this);
            myButton.setText(name);
            myButton.setTag(language + "::" + text);
            myButton.setOnClickListener(getOnClickTalk(myButton));

            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            LinearLayout newLL = new LinearLayout(this);
            newLL.setOrientation(LinearLayout.HORIZONTAL);
            ViewGroup.LayoutParams newLP = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            newLL.addView(myButton, newLP);

            Button rmvButton = new Button(this);
            rmvButton.setText("-");
            rmvButton.setTag(id);
            rmvButton.setOnClickListener(getOnClickRemove(rmvButton));
            newLL.addView(rmvButton, newLP);

            ll.addView(newLL, lp);
        }
        c.close();
    }

    View.OnClickListener getOnClickTalk(final Button button)  {
        return new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            public void onClick(View v) {
                String tag = button.getTag().toString();
                String language = tag.substring(0, tag.indexOf("::") - 1);
                String text = tag.substring(tag.indexOf("::") + 2);

                Intent checkTTSIntent = new Intent();
                checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
                startActivityForResult(checkTTSIntent, 0);
                
                engine.setLanguage(new Locale(language));
                engine.speak(text, TextToSpeech.QUEUE_ADD, null);
            }
        };
    }

    View.OnClickListener getOnClickRemove(final Button button)  {
        return new View.OnClickListener() {
            public void onClick(View v) {
                String id = button.getTag().toString();
                rmvBtnFromDb(id);
                updateBtns();
            }
        };
    }

    public void addBtnToDb(String name, String text, String language) {
        // Gets the data repository in write mode
        SQLiteDatabase writeDb = db.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("text", text);
        values.put("language", language);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = writeDb.insert("btns", null, values);
    }

    public void rmvBtnFromDb(String id) {
        // Gets the data repository in write mode
        SQLiteDatabase writeDb = db.getWritableDatabase();

        int count = writeDb.delete("btns", "_id=" + id, null);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
