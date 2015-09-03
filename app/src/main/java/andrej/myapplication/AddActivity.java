package andrej.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class AddActivity extends ActionBarActivity {

    DbHelper db;
    Map<String,String> localePairs;

    public final static String EXTRA_MESSAGE = "com.mycompany.myfirstapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_add);

        db = new DbHelper(getApplicationContext());

        Locale[] locales = Locale.getAvailableLocales();
        //ArrayList<String> localCodes = new ArrayList<String>();
        ArrayList<String> localNames = new ArrayList<String>();
        localePairs = new HashMap<String,String>();
        for(Locale l:locales)
        {
            //localCodes.add(l.toString());
            localNames.add(l.getDisplayName().toString());

            localePairs.put(l.getDisplayName().toString(), l.toString());
        }
        Collections.sort(localNames, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, localNames);
        spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        Spinner spin = (Spinner)findViewById(R.id.spinLanguage);
        spin.setAdapter(spinnerArrayAdapter);
    }

    //put new btn parameters in db
    //intent doesn't need extra message
    public void addBtn(View view) {
        EditText nameTxt = (EditText)findViewById(R.id.txtName);
        String name = nameTxt.getText().toString();

        EditText textTxt = (EditText)findViewById(R.id.txtText);
        String text = textTxt.getText().toString();

        Spinner spin = (Spinner)findViewById(R.id.spinLanguage);
        String lang = spin.getSelectedItem().toString();
        String langCode = localePairs.get(lang);

        if (!name.isEmpty() && !text.isEmpty()) {
            addBtnToDb(name, text, langCode);
            Intent intent = new Intent(AddActivity.this, MainActivity.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(this, "Name and text must be filled.", Toast.LENGTH_LONG).show();
        }
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_add, menu);
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
