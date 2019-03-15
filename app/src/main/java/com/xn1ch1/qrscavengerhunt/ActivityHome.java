package com.xn1ch1.qrscavengerhunt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;

public class ActivityHome extends AppCompatActivity {

    SharedPreferences clueData;

    Boolean huntStarted;

    ImageButton huntButton;
    ImageButton setButton;

    int clueNumber;

    float on;
    float off;

    TypedValue outValue = new TypedValue();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        clueData = getSharedPreferences(getResources().getString(R.string.dataClueData), Context.MODE_PRIVATE);
        huntStarted = clueData.getBoolean(getResources().getString(R.string.dataHuntStarted), false);
        clueNumber = clueData.getInt(getResources().getString(R.string.dataCurrentClue), 0);

        getResources().getValue(R.dimen.allViewEnabled, outValue, true);
        on = outValue.getFloat();

        getResources().getValue(R.dimen.allViewDisabled, outValue, true);
        off = outValue.getFloat();

        huntButton = findViewById(R.id.huntButton);

        setButton = findViewById(R.id.setButton);

        if (huntStarted) {
            setButton.setAlpha(off);
            huntButton.setAlpha(on);
        } else {
            setButton.setAlpha(on);
            huntButton.setAlpha(off);
        }


    }


    public void setClues(View v) {

        if (huntStarted) {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
            builder.setMessage(getResources().getString(R.string.homeHuntStarted));
            builder.setNeutralButton(getResources().getString(R.string.ok), null);
            builder.show();
        } else {
            Intent intent = new Intent(this, ActivitySetClues.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    public void goHunt(View v) {
        if (!huntStarted) {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
            builder.setMessage(getResources().getString(R.string.homeHuntNotStarted));
            builder.setNeutralButton(getResources().getString(R.string.ok), null);
            builder.show();
        } else {
            Intent intent = new Intent(this, ActivityHunt.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }


}
