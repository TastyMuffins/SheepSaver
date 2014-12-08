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
import android.widget.ProgressBar;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Shepherd extends ListActivity {

    private String godKey = new String();
    private ShepardDB shepardDB;
    private ListView passwordListView;
    private ListPasswordAdapter passwordAdapter;
    private List<PasswordItem> passwords;
    private PasswordItem selectedItem;
    private Context context = this;

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
                selectedItem = (PasswordItem) getListView().getItemAtPosition(position);
                callBack.setClickedView(view);
                startActionMode(callBack);
                view.setSelected(true);
                return true;

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
                            Toast.makeText(context, "Did NOT copy password to clipboard", Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        } catch (Exception e) {
            Toast.makeText(context, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
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
            final View layout = inflater.inflate(R.layout.addpassword, null);
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

            final String newPassword = passwordText.getText().toString();
            final ProgressBar passwordStrengthBar = (ProgressBar) layout.findViewById(R.id.strengthBar);
            final PasswordItem myPasswordItem = new PasswordItem();

            passwordText.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    passwordStrengthBar.setProgress(PasswordItem.passwordStrengthMeter(passwordText.getText().toString()));

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            final AlertDialog alertDialog = builder.create();
            alertDialog.show();
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //For some reason it didn't like to store the domainText into a String nor Username for if statement
                    String newDomain = domainText.getText().toString();
                    String newUsername = usernameText.getText().toString();
                    int newStrength = PasswordItem.passwordStrengthMeter(passwordText.getText().toString()); //make this be calculated based on password complexity

                    if(newStrength!=0){

                        if (domainText.getText().toString().isEmpty() || usernameText.getText().toString().isEmpty()) {
                            Toast.makeText(context, "Domain or Password empty", Toast.LENGTH_LONG).show();
                        }else{
                            try {
                                passwordAdapter.add(shepardDB.createStoredPassword(newDomain, newUsername, Encryption.ECrypt(godKey, newPassword), newStrength));
                            }catch (Exception e) {
                                Toast.makeText(context, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                            alertDialog.dismiss();
                        }
                    }else{
                        Toast.makeText(context, "Password has to be 6 characters or more", Toast.LENGTH_LONG).show();
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

    private interface ActionCallback extends ActionMode.Callback {
        public void setClickedView(View view);
    }

    private ActionCallback callBack = new ActionCallback() {
        public View mClickedView;
        public void setClickedView(View view) {
            mClickedView = view;
        }
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
            switch(item.getItemId()){
                case R.id.deleteButton:
                    shepardDB.deleteStoredPassword(selectedItem.getId());
                    passwordAdapter.remove(selectedItem);
                    mode.finish();
                    return true;
                case R.id.editButton:
                    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                    View layout = inflater.inflate(R.layout.addpassword, null);
                    final EditText domainText = (EditText) layout.findViewById(R.id.domainText);
                    final EditText usernameText = (EditText) layout.findViewById(R.id.usernameText);
                    final EditText passwordText = (EditText) layout.findViewById(R.id.passwordText);

                    domainText.setText(selectedItem.getDomain());
                    usernameText.setText(selectedItem.getUsername());
                   // passwordText.setText(selectedItem.getStoredPassword());

                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setView(layout);
                    builder.setCancelable(false);
                    builder.setTitle("Edit a stored password");
                    builder.setMessage("Modify a stored entry managed by Shepard");
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
                            String newDomain = domainText.getText().toString();
                            String newUsername = usernameText.getText().toString();
                            String newPassword = passwordText.getText().toString();
                            int newStrength = PasswordItem.passwordStrengthMeter(passwordText.getText().toString());

                            if (newDomain.isEmpty() || newPassword.isEmpty()) {
                                Toast.makeText(context, "Domain or Password empty", Toast.LENGTH_LONG).show();
                            } else {
                                try {
                                    shepardDB.deleteStoredPassword(selectedItem.getId());
                                    passwordAdapter.add(shepardDB.createStoredPassword(newDomain, newUsername, Encryption.ECrypt(godKey, newPassword), newStrength));
                                } catch (Exception e) {
                                    Toast.makeText(context, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                                alertDialog.dismiss();
                            }
                            passwordAdapter.notifyDataSetChanged();
                        }
                    });
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }

    };



}