package ca.josephroque.bowlingcompanion;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.josephroque.bowlingcompanion.database.BowlingContract.*;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;
import ca.josephroque.bowlingcompanion.dialog.AddBowlerDialog;

public class MainActivity extends ActionBarActivity
    implements AddBowlerDialog.AddBowlerDialogListener
{

    private List<String> bowlerNamesList = null;
    private List<Long> bowlerIDsList = null;
    private ArrayAdapter<String> bowlerAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Preferences.clearPreferences(this);

        SQLiteDatabase database = DatabaseHelper.getInstance(this).getReadableDatabase();
        final ListView listBowlerNames = (ListView) findViewById(R.id.list_bowler_name);

        Cursor cursor = database.query(BowlerEntry.TABLE_NAME,
                new String[]{BowlerEntry.COLUMN_NAME_BOWLER_NAME, BowlerEntry._ID},
                null,   //All rows
                null,   //No args
                null,   //No group
                null,   //No having
                BowlerEntry.COLUMN_NAME_DATE_MODIFIED + " DESC");  //No order

        bowlerNamesList = new ArrayList<String>();
        bowlerIDsList = new ArrayList<Long>();
        if (cursor.moveToFirst())
        {
            while(!cursor.isAfterLast())
            {
                bowlerNamesList.add(cursor.getString(cursor.getColumnIndex(BowlerEntry.COLUMN_NAME_BOWLER_NAME)));
                bowlerIDsList.add(cursor.getLong(cursor.getColumnIndex(BowlerEntry._ID)));
                cursor.moveToNext();
            }
        }

        bowlerAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, bowlerNamesList);
        listBowlerNames.setAdapter(bowlerAdapter);
        bowlerAdapter.notifyDataSetChanged();

        listBowlerNames.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    String bowlerNameSelected = (String)listBowlerNames.getItemAtPosition(position);

                    long selectedBowlerID;

                    try
                    {
                        selectedBowlerID = bowlerIDsList.get(bowlerNamesList.indexOf(bowlerNameSelected));
                    }
                    catch (IndexOutOfBoundsException ex)
                    {
                        Log.w("MainActivity", "onCreate method, onItemClick caught exception.");
                        showAddBowlerDialog();
                        return;
                    }

                    SQLiteDatabase database = DatabaseHelper.getInstance(MainActivity.this).getWritableDatabase();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date();

                    ContentValues values = new ContentValues();
                    values.put(BowlerEntry.COLUMN_NAME_DATE_MODIFIED, dateFormat.format(date));

                    database.beginTransaction();

                    try
                    {
                        database.update(BowlerEntry.TABLE_NAME,
                                values,
                                BowlerEntry._ID + "=?",
                                new String[]{String.valueOf(selectedBowlerID)});
                        database.setTransactionSuccessful();
                    }
                    catch (Exception ex)
                    {
                        Log.w("MainActivity", "Error updating bowler: " + ex.getMessage());
                    }
                    finally
                    {
                        database.endTransaction();
                    }

                    Preferences.setPreferences(MainActivity.this, bowlerNamesList.get(bowlerIDsList.indexOf(selectedBowlerID)), "", selectedBowlerID, -1, -1, -1, -1);

                    Intent leagueIntent = new Intent(MainActivity.this, LeagueActivity.class);
                    startActivity(leagueIntent);
                }
            });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id)
        {
            case R.id.action_add_bowler:
                showAddBowlerDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        DatabaseHelper.closeInstance();
    }

    private void showAddBowlerDialog()
    {
        DialogFragment dialog = new AddBowlerDialog();
        dialog.show(getSupportFragmentManager(), "AddBowlerDialogFragment");
    }

    @Override
    public void onAddNewBowler(String bowlerName)
    {
        boolean validInput = true;
        String invalidInputMessage = null;

        if (bowlerName == null || bowlerName.length() == 0)
        {
            validInput = false;
            invalidInputMessage = "You must enter a name.";
        }
        else if (bowlerNamesList.contains(bowlerName))
        {
            validInput = false;
            invalidInputMessage = "That name has already been used. You must choose another.";

        }

        if (!validInput)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage(invalidInputMessage)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            //do nothing
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
            return;
        }

        long newID = -1;
        SQLiteDatabase database = DatabaseHelper.getInstance(MainActivity.this).getWritableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        ContentValues values = new ContentValues();
        values.put(BowlerEntry.COLUMN_NAME_BOWLER_NAME, bowlerName);
        values.put(BowlerEntry.COLUMN_NAME_DATE_MODIFIED, dateFormat.format(date));

        database.beginTransaction();
        try
        {
            newID = database.insert(BowlerEntry.TABLE_NAME, null, values);
            database.setTransactionSuccessful();
        }
        catch (Exception ex)
        {
            Log.w("MainActivity", "Error adding new bowler: " + ex.getMessage());
        }
        finally
        {
            database.endTransaction();
        }

        bowlerNamesList.add(0, bowlerName);
        bowlerIDsList.add(0, newID);
        bowlerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCancelNewBowler()
    {
        //does nothing
    }
}
