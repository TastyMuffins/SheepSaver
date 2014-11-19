package pw.monkeys.paul.sheepsaver;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;


public class Shepherd extends ListActivity {

    private String godKey = new String();
    private ShepardDB shepardDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shepherd);

        Intent intent = getIntent();
        godKey = intent.getStringExtra("GodKey");

        shepardDB = new ShepardDB(this);
        try {
            shepardDB.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<PasswordItem> passwords = shepardDB.getAllStoredPasswords();
        ListPasswordAdapter passwordAdapter = new ListPasswordAdapter(this,android.R.layout.simple_list_item_1,passwords);
        setListAdapter(passwordAdapter);
        //create a record in teh database for debug purposes
        //shepardDB.createStoredPassword("Test.com","Paul","Password123",50);
        passwordAdapter.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(),"The key is :"+godKey,Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shepherd, menu);
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
    protected void onResume() {
        try {
            shepardDB.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        shepardDB.close();
        super.onPause();
    }
}
