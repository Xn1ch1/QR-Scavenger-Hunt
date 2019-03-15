package com.xn1ch1.qrscavengerhunt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class ActivityHunt extends AppCompatActivity {

    final static int SCAN_BARCODE = 88;

    SharedPreferences mDataStore;
    SharedPreferences.Editor mDataStoreEditor;

    String[][] mClueData = new String[10][2];

    TextView mClueTitle;
    TextView mClueText;

    int mClueNumber = 0;

    Boolean mIsLastClue;

    MediaPlayer mErrorSound;
    MediaPlayer mSolvedSound;
    MediaPlayer mFinishSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hunt);

        loadClueData();

        mErrorSound = MediaPlayer.create(this, R.raw.hunt_error);
        mSolvedSound = MediaPlayer.create(this, R.raw.hunt_solved);
        mFinishSound = MediaPlayer.create(this, R.raw.hunt_finish);

        mIsLastClue = (mClueNumber == 9 || mClueData[mClueNumber + 1][0].equals(""));

        mClueText = findViewById(R.id.huntClueText);
        mClueTitle = findViewById(R.id.huntClueTitle);

        applyClueData();
        solvedInvisible();
        finishInvisible();
        errorInvisible();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, ActivityHome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == SCAN_BARCODE) {
                if (data.getStringExtra("code").equals(mClueData[mClueNumber][1])) {

                    clueInvisible();
                    errorInvisible();

                    if (mIsLastClue) {

                        writeHuntFinished();
                        mFinishSound.start();
                        finishVisible();

                        findViewById(R.id.huntLayout).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onBackPressed();
                            }
                        });

                    } else {

                        writeClueSolved();
                        mSolvedSound.start();
                        solvedVisible();

                    }

                } else {

                    mErrorSound.start();
                    errorVisible();

                }
            }

        }
    }

    /* *******************
        User Interactions
       ******************* */
    public void scanBarcode(View v) {
        errorInvisible();
        Intent scan = new Intent(this, BarcodeScan.class);
        startActivityForResult(scan, SCAN_BARCODE);
    }

    public void goBack(View view) {
        onBackPressed();
    }

    public void goNext(View view) {
        mClueNumber++;
        mIsLastClue = (mClueNumber == 9 || mClueData[mClueNumber + 1][0].equals(""));
        applyClueData();
        solvedInvisible();
        clueVisible();
        errorInvisible();
    }

    /* *****************
        Display Changes
       ***************** */
    private void applyClueData() {
        mClueText.setText(mClueData[mClueNumber][0]);
        if (mIsLastClue) {
            mClueTitle.setText(getResources().getString(R.string.huntFinalClueTitle));
        } else {
            mClueTitle.setText(getResources().getString(R.string.allClueNumberTitle, mClueNumber + 1));
        }
    }

    private void clueVisible() {
        findViewById(R.id.huntClueTitle).setVisibility(View.VISIBLE);
        findViewById(R.id.huntClueText).setVisibility(View.VISIBLE);
        findViewById(R.id.huntClueError).setVisibility(View.VISIBLE);
        findViewById(R.id.huntClueScan).setVisibility(View.VISIBLE);
        findViewById(R.id.huntClueBack).setVisibility(View.VISIBLE);
    }

    private void clueInvisible() {
        findViewById(R.id.huntClueTitle).setVisibility(View.INVISIBLE);
        findViewById(R.id.huntClueText).setVisibility(View.INVISIBLE);
        findViewById(R.id.huntClueError).setVisibility(View.INVISIBLE);
        findViewById(R.id.huntClueScan).setVisibility(View.INVISIBLE);
        findViewById(R.id.huntClueBack).setVisibility(View.INVISIBLE);
    }

    private void errorVisible() {
        findViewById(R.id.huntClueError).setVisibility(View.VISIBLE);
    }

    private void errorInvisible() {
        findViewById(R.id.huntClueError).setVisibility(View.INVISIBLE);
    }

    private void solvedVisible() {
        findViewById(R.id.huntSolvedTitle).setVisibility(View.VISIBLE);
        findViewById(R.id.huntSolvedImage).setVisibility(View.VISIBLE);
        findViewById(R.id.huntSolvedBack).setVisibility(View.VISIBLE);
        findViewById(R.id.huntSolvedNext).setVisibility(View.VISIBLE);
    }

    private void solvedInvisible() {
        findViewById(R.id.huntSolvedTitle).setVisibility(View.INVISIBLE);
        findViewById(R.id.huntSolvedImage).setVisibility(View.INVISIBLE);
        findViewById(R.id.huntSolvedBack).setVisibility(View.INVISIBLE);
        findViewById(R.id.huntSolvedNext).setVisibility(View.INVISIBLE);
    }

    private void finishVisible() {
        findViewById(R.id.huntFinishedImage).setVisibility(View.VISIBLE);
        findViewById(R.id.huntFinishedTitle).setVisibility(View.VISIBLE);
        findViewById(R.id.huntFinishedMessage).setVisibility(View.VISIBLE);
    }

    private void finishInvisible() {
        findViewById(R.id.huntFinishedImage).setVisibility(View.INVISIBLE);
        findViewById(R.id.huntFinishedTitle).setVisibility(View.INVISIBLE);
        findViewById(R.id.huntFinishedMessage).setVisibility(View.INVISIBLE);
    }

    /* *****************
        Data Read/Write
       ***************** */
    private void loadClueData() {
        mDataStore = getSharedPreferences(getResources().getString(R.string.dataClueData), Context.MODE_PRIVATE);
        mClueNumber = mDataStore.getInt(getResources().getString(R.string.dataCurrentClue), 0);
        for (int i = 0; i < 10; i++) {
            mClueData[i][0] = mDataStore.getString("clue" + i, "");
            mClueData[i][1] = mDataStore.getString("code" + i, "");
        }
    }

    public void writeClueSolved() {
        mDataStoreEditor = mDataStore.edit();
        mDataStoreEditor.putInt(getResources().getString(R.string.dataCurrentClue), mClueNumber);
        mDataStoreEditor.apply();
    }

    private void writeHuntFinished() {
        mDataStoreEditor = mDataStore.edit();
        mDataStoreEditor.putInt(getResources().getString(R.string.dataCurrentClue), 0);
        mDataStoreEditor.putBoolean(getResources().getString(R.string.dataHuntStarted), false);
        mDataStoreEditor.apply();
    }
}
