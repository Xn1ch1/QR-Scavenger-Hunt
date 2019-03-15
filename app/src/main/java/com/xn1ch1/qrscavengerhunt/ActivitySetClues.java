package com.xn1ch1.qrscavengerhunt;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class ActivitySetClues extends AppCompatActivity {

    SharedPreferences mDataStore;
    SharedPreferences.Editor mDataStoreEditor;

    InputMethodManager mInputMethod;

    int mRowID = 0;

    String[][] mClueData = new String[10][2];

    int[] mClueChecks = new int[2];
    int[] mCodeChecks = new int[2];
    float[] mCheckStatus = new float[2];

    TypedValue mOutValue = new TypedValue();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_clues);


        getResources().getValue(R.dimen.allViewSemiTransparent, mOutValue, true);
        mCheckStatus[0] = mOutValue.getFloat();
        getResources().getValue(R.dimen.allViewEnabled, mOutValue, true);
        mCheckStatus[1] = mOutValue.getFloat();

        mCodeChecks[0] = R.drawable.set_clue_scan_unchecked;
        mCodeChecks[1] = R.drawable.set_clue_scan_checked;

        mClueChecks[0] = R.drawable.set_clue_clue_text_unchecked;
        mClueChecks[1] = R.drawable.set_clue_clue_text_checked;

        loadClueData();
        setAllTextAndChecks();
        textInputInvisible();

    }

    @Override
    public void onBackPressed() {
        if (findViewById(R.id.SetCluesTextInputOverlay).getVisibility() == View.VISIBLE) {
            saveTextInput();
            setSingleTextAndChecks(mRowID);
            textInputInvisible();
        } else {
            super.onBackPressed();
            writeClueData();
            Intent intent = new Intent(this, ActivityHome.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int mRequestCode, int mResultCode, Intent mData) {
        if (mResultCode == Activity.RESULT_OK) {

            String mReturnedCode = mData.getStringExtra("code");

            if (codeAlreadyAssigned(mReturnedCode)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
                builder.setMessage(getResources().getString(R.string.setCluesCodeUsed, mReturnedCode));
                builder.setNeutralButton(getResources().getString(R.string.ok), null).show();
            } else {
                mClueData[mRequestCode][1] = mReturnedCode;
                writeClueData();
                setSingleTextAndChecks(mRequestCode);

                final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
                builder.setMessage(getResources().getString(R.string.setCluesCodeOK, mReturnedCode, mRequestCode + 1));
                builder.setNeutralButton(getResources().getString(R.string.ok), null);
                builder.show();
            }
        }
    }

    /* ******************
        User Interaction
       ****************** */
    public void inputClueText(View view) {
        mRowID = view.getId();
        String mRowName = getResources().getResourceEntryName(mRowID);
        mRowName = mRowName.substring(mRowName.length() - 1);
        mRowID = Integer.parseInt(mRowName);
        textInputVisible(mRowID);
    }

    public void setCluesScanBarcode(View view) {

        mRowID = view.getId();
        String mRowName = getResources().getResourceEntryName(mRowID);
        mRowName = mRowName.substring(mRowName.length() - 1);
        mRowID = Integer.parseInt(mRowName);

        if (mClueData[mRowID][1].equals("")) {
            Intent scan = new Intent(this, BarcodeScan.class);
            startActivityForResult(scan, mRowID);
        } else {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_NEGATIVE:
                            return;

                        case DialogInterface.BUTTON_POSITIVE:
                            mClueData[mRowID][1] = "";
                            setSingleTextAndChecks(mRowID);
                            writeClueData();
                            Intent scan = new Intent(ActivitySetClues.this, BarcodeScan.class);
                            startActivityForResult(scan, mRowID);
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
            builder.setMessage(getResources().getString(R.string.setCluesRemoveCode, mClueData[mRowID][1])).setPositiveButton(getResources().getString(R.string.allYes), dialogClickListener)
                    .setNegativeButton(getResources().getString(R.string.allNo), dialogClickListener).show();
        }

    }

    public void goBackHome(View view) {
        onBackPressed();
    }

    public void completeSetUp(View view) {

        boolean mIsDataOk = true;

        if (mClueData[0][0].equals("") || mClueData[0][1].equals("")) {
            mIsDataOk = false;
        }

        for (int i = 1; i < 9; i++) {
            if ((mClueData[i][0].equals("") ^ mClueData[i][1].equals("")) || (mClueData[i][0].equals("") && !mClueData[i][0].equals(""))) {
                mIsDataOk = false;
            }
        }

        if (mIsDataOk) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_NEGATIVE:
                            return;
                        case DialogInterface.BUTTON_POSITIVE:
                            writeClueData();
                            writeHuntStart();
                            onBackPressed();
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
            builder.setMessage(getResources().getString(R.string.setCluesBeginHunt));
            builder.setPositiveButton(getResources().getString(R.string.allYes), dialogClickListener);
            builder.setNegativeButton(getResources().getString(R.string.allNo), dialogClickListener);
            builder.show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
            builder.setMessage(getResources().getString(R.string.setCluesCantFinish));
            builder.setNeutralButton(getResources().getString(R.string.ok), null);
            builder.show();
        }
    }

    public void resetData(View view) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_NEGATIVE:
                        return;

                    case DialogInterface.BUTTON_POSITIVE:
                        eraseClueData();
                        setAllTextAndChecks();
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        builder.setMessage(getResources().getString(R.string.setCluesResetAllWarning));
        builder.setPositiveButton(getResources().getString(R.string.allYes), dialogClickListener);
        builder.setNegativeButton(getResources().getString(R.string.allNo), dialogClickListener);
        builder.show();
    }

    public void returnTextInput(View view) {
        saveTextInput();
        setSingleTextAndChecks(mRowID);
        textInputInvisible();
    }

    public void clearInput(View view) {
        TextView mTextInput = findViewById(R.id.setCluesTextInput);
        mTextInput.setText("");
    }

    public void doneInput(View view) {
        saveTextInput();
        setSingleTextAndChecks(mRowID);
        textInputInvisible();
    }

    /* *****************
        Display Changes
       ***************** */
    private void setAllTextAndChecks() {

        for (int i = 0; i < mClueData.length; i++) {

            setSingleTextAndChecks(i);

        }

    }

    private void setSingleTextAndChecks(int mRowNumber) {
        int mTextID = getResources().getIdentifier("setClueText" + mRowNumber, "id", getPackageName());
        int mTextCheckID = getResources().getIdentifier("setClueTextCheck" + mRowNumber, "id", getPackageName());
        int mScanCheckID = getResources().getIdentifier("setClueCodeCheck" + mRowNumber, "id", getPackageName());

        TextView mText = findViewById(mTextID);
        ImageButton mTextCheck = findViewById(mTextCheckID);
        ImageButton mScanCheck = findViewById(mScanCheckID);

        int mHasText = mClueData[mRowNumber][0].equals("") ? 0 : 1;
        int mHasCode = mClueData[mRowNumber][1].equals("") ? 0 : 1;

        mTextCheck.setAlpha(mCheckStatus[mHasText]);
        mTextCheck.setImageResource(mClueChecks[mHasText]);

        mScanCheck.setAlpha(mCheckStatus[mHasCode]);
        mScanCheck.setImageResource(mCodeChecks[mHasCode]);

        if (mClueData[mRowNumber][0].equals("")) {
            mText.setText(getResources().getString(R.string.setCluesNotSet));
        } else {
            mText.setText(mClueData[mRowNumber][0]);
        }
    }

    private void textInputVisible(int mRow) {
        EditText mTextInput = findViewById(R.id.setCluesTextInput);

        mTextInput.setText(mClueData[mRow][0]);
        mTextInput.requestFocus();
        mTextInput.setSelection(mTextInput.getText().length());

        mTextInput.setVisibility(View.VISIBLE);

        findViewById(R.id.SetCluesTextInputOverlay).setVisibility(View.VISIBLE);
        findViewById(R.id.setCluesInputClear).setVisibility(View.VISIBLE);
        findViewById(R.id.setCluesInputDone).setVisibility(View.VISIBLE);

        mInputMethod = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethod.showSoftInput(mTextInput, InputMethodManager.SHOW_IMPLICIT);
    }

    private void textInputInvisible() {
        TextView mTextInput = findViewById(R.id.setCluesTextInput);

        mTextInput.setVisibility(View.INVISIBLE);

        findViewById(R.id.SetCluesTextInputOverlay).setVisibility(View.INVISIBLE);
        findViewById(R.id.setCluesInputClear).setVisibility(View.INVISIBLE);
        findViewById(R.id.setCluesInputDone).setVisibility(View.INVISIBLE);

        mInputMethod = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethod.hideSoftInputFromWindow(mTextInput.getWindowToken(), 0);
    }

    /* *********
        Methods
       ********* */
    private boolean codeAlreadyAssigned(String mCode) {

        boolean mAssigned = false;

        for (String[] codes : mClueData) {
            if (codes[1].equals(mCode)) {
                mAssigned = true;
                break;
            }
        }

        return mAssigned;

    }

    /* *****************
        Data Read/Write
       ***************** */
    private void loadClueData() {
        mDataStore = getSharedPreferences(getResources().getString(R.string.dataClueData), Context.MODE_PRIVATE);
        for (int i = 0; i < 10; i++) {
            mClueData[i][0] = mDataStore.getString("clue" + i, "");
            mClueData[i][1] = mDataStore.getString("code" + i, "");
        }
    }

    private void writeClueData() {
        mDataStoreEditor = mDataStore.edit();
        for (int i = 0; i < 10; i++) {
            mDataStoreEditor.putString("clue" + i, mClueData[i][0]);
            mDataStoreEditor.putString("code" + i, mClueData[i][1]);
        }
        mDataStoreEditor.apply();
    }

    private void writeHuntStart() {
        mDataStoreEditor = mDataStore.edit();
        mDataStoreEditor.putBoolean(getResources().getString(R.string.dataHuntStarted), true);
        mDataStoreEditor.commit();
    }

    private void eraseClueData() {
        mDataStoreEditor = mDataStore.edit();
        for (int i = 0; i < 10; i++) {
            mClueData[i][0] = "";
            mClueData[i][1] = "";
            mDataStoreEditor.putString("clue" + i, "");
            mDataStoreEditor.putString("code" + i, "");
        }
        mDataStoreEditor.apply();
    }

    private void saveTextInput() {
        TextView mTextInput = findViewById(R.id.setCluesTextInput);
        mClueData[mRowID][0] = mTextInput.getText().toString();
    }
}
