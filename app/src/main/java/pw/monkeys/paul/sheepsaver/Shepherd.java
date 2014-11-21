package pw.monkeys.paul.sheepsaver;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;


public class Shepherd extends ListActivity {

    private String godKey = new String();
    private ShepardDB shepardDB;
    private ListView passwordListView;
    private ListPasswordAdapter passwordAdapter;
    private List<PasswordItem> passwords;

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
        passwords = shepardDB.getAllStoredPasswords();
        passwordAdapter = new ListPasswordAdapter(this, android.R.layout.simple_list_item_1, passwords);
        setListAdapter(passwordAdapter);
        passwordAdapter.notifyDataSetChanged();

        EditText searchText = (EditText) findViewById(R.id.searchText);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                passwordAdapter.getFilter().filter(charSequence);
            }
        });
        //Toast.makeText(getApplicationContext(),"The key is :"+godKey,Toast.LENGTH_LONG).show();

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                PasswordItem shownItem = (PasswordItem) getListView().getItemAtPosition(position);
                startActionMode(callBack);
                return false;

            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        PasswordItem shownItem = (PasswordItem) getListView().getItemAtPosition(position);
        try {
            new AlertDialog.Builder(this)
                    .setTitle(shownItem.getDomain())
                    .setMessage(
                            "Username: " + shownItem.getUsername() + "\n"
                                    + "Password: " + Encryption.DCrypt(godKey, shownItem.getStoredPassword()))
                    .setPositiveButton("Copy", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), "Did NOT copy password to clipboard", Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
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
        if (id == R.id.logoutMenu) {
            finish();
        }
        if (id == R.id.addPasswordMenu) {
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.addpassword, null);
            final EditText domainText = (EditText) layout.findViewById(R.id.domainText);
            final EditText usernameText = (EditText) layout.findViewById(R.id.usernameText);
            final EditText passwordText = (EditText) layout.findViewById(R.id.passwordText);

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(layout);
            builder.setCancelable(false);
            builder.setTitle("Add New Password");
            builder.setMessage("Add a password to be managed by Shepard");
            builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }

            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            final AlertDialog alertDialog = builder.create();
            alertDialog.show();
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //do some checking here is password empty? is password too short?
                    //calculate 'strength'
                    //#Jose
                    String newDomain = domainText.getText().toString();
                    String newUsername = usernameText.getText().toString();
                    String newPassword = passwordText.getText().toString();
                    int newStrength = 50; //make this be calculated based on password complexity

                    if (newDomain.isEmpty() || newPassword.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Domain or Password empty", Toast.LENGTH_LONG).show();
                    } else {
                        try {
                            passwordAdapter.add(shepardDB.createStoredPassword(newDomain, newUsername, Encryption.ECrypt(godKey, newPassword), newStrength));
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        alertDialog.dismiss();
                    }

                    passwordAdapter.notifyDataSetChanged();
                }
            });

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

    private ActionMode.Callback callBack = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.actionbar, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }

    };
}