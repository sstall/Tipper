package com.tipper.samss.tipper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Loads the text filed objects into EditText variables
        final TextInputLayout billLayout = findViewById(R.id.bill_input_textlayout);
        final TextInputLayout tipLayout = findViewById(R.id.tip_input_textlayout);
        final EditText billField = getBillEditText();
        final EditText tipField = getTipEditText();

        billLayout.setHintEnabled(false);
        tipLayout.setHintEnabled(false);

        //Textwatcher is used to scan for changes in the input fields so the output can be continuously
        //updated
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0){
                    billLayout.setHintEnabled(false);
                }
                else {
                    billLayout.setHintEnabled(true);
                }
                //calculate() can throw a NumberFormatException however it has been handled atm
                try {
                    calculate();
                } catch(NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Exception: Invalid Input", Toast.LENGTH_SHORT).show();
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        };

        TextWatcher textWatchertip = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0){
                    tipLayout.setHintEnabled(false);
                }
                else {
                    tipLayout.setHintEnabled(true);
                }
                //calculate() can throw a NumberFormatException however it has been handled atm
                try {
                    calculate();
                } catch(NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Exception: Invalid Input", Toast.LENGTH_SHORT).show();
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        };

        //Attaches the textwatcher object to the input fields that need to be monitored
        billField.addTextChangedListener(textWatcher);
        tipField.addTextChangedListener(textWatchertip);


        setFocus(billField);

        billField.setFilters(new InputFilter[] {new InputFilterDecimal(5,2)});

        billField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i== EditorInfo.IME_ACTION_NEXT){
                    setFocus(tipField);
                }
                return false;
            }
        });

        tipField.setFilters(new InputFilter[] {new InputFilterDecimal(5,2)});

        tipField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i== EditorInfo.IME_ACTION_DONE){
                    tipField.clearFocus();
                }
                return false;
            }
        });

        final CheckBox checkBox_round = findViewById(R.id.round_check);
        checkBox_round.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                calculate();
            }
        });
    }

    public EditText getBillEditText() {
        final TextInputLayout billLayout = findViewById(R.id.bill_input_textlayout);
        final EditText billField = billLayout.getEditText();
        return billField;
    }

    public EditText getTipEditText() {
        final TextInputLayout TipLayout = findViewById(R.id.tip_input_textlayout);
        final EditText tipField = TipLayout.getEditText();
        return tipField;
    }

    //Function that takes an EditText object and sets focus to it
    public void setFocus(EditText editText) {
        editText.clearFocus();
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    //Clears all input fields using the clearBill() and clearTip() functions
    public void clearAll(View view) {
        clearTip(view);
        //Calls clearBill() second because the cursor should end highlighting the billField input
        clearBill(view);
    }

    //Clears the Bill input field
    public void clearBill(View view) {
        EditText billField = getBillEditText();
        //Clears the text
        billField.setText("");
        //Requests the cursor to move to the billField input
        setFocus(billField);
    }

    //Clears the Tip input field
    public void clearTip(View view) {
        EditText tipField = getTipEditText();
        //Clears the text
        tipField.setText("");
        //Requests the cursor to move to the tup percent input
        setFocus(tipField);
    }

    //Selects the billField input field
    public void selectBill(View view) {
        EditText billField = getBillEditText();
        setFocus(billField);
    }

    //Selects the tip input field
    public void selectTip(View view) {
        EditText tipField = getTipEditText();
        setFocus(tipField);
    }

    //Sets tip based on which tip button was pressed
    public void setTip(View view) {
        EditText tipField = getTipEditText();
        tipField.setText("");
        switch(view.getId()) {
            case R.id.fifteenPercent_button:
                tipField.setText("15");
                break;
            case R.id.eighteenPercent_button:
                tipField.setText("18.5");
                break;
            case R.id.twentyPercent_button:
                tipField.setText("20");
                break;
        }
        EditText billField = getBillEditText();
        if(tipField.hasFocus()) {
            tipField.setSelection(tipField.getText().length());;
        }
        else if(billField.hasFocus()) {
            billField.setSelection(billField.getText().length());
        }
    }

    //Calculates the tip and total using the bill and tip percent amounts
    @SuppressLint("DefaultLocale")
    private void calculate() throws NumberFormatException {
        EditText billEdit = getBillEditText();
        EditText percentEdit = getTipEditText();

        double billField = 0;
        double percent = 0;

        //Checks that billEdit is not null, at least greater than 0 characters, and that it is valid input
        if(billEdit.length() >= 1) {
            String billString = billEdit.getText().toString();
            if(!(billString.contains("_") || billString.contains(",") || billString.contains("-") ||
                    billString.contains(".") && billString.length() == 1)) {
                //It is valid input therefore it is safe to load into the double billField
                billField = Double.parseDouble(billString);
            }
        }
        //Checks that percentEdit is not null, at least greater than 0 characters, and that it is valid input
        if(percentEdit != null && percentEdit.length() >= 1) {
            String percentString = percentEdit.getText().toString();
            if(!(percentString.contains("_") || percentString.contains(",") || percentString.contains("-") ||
                    percentString.contains(".") && percentString.length() == 1)) {
                //It is valid input therefore it is safe to load into the double percent
                percent = Double.parseDouble(percentString);
            }
        }

        final CheckBox round_box = findViewById(R.id.round_check);

        double tip;
        double total;

        //Checks if the rounding option is checked
        if(round_box.isChecked()) {
            tip = billField * (percent / 100);
            total = billField + tip;

            double round_up = Math.ceil(total);
            double amountToRound = round_up - total;

            tip += amountToRound;
            total = billField + tip;
        }
        else {
            //Calculate the tip
            tip = billField * (percent / 100);

            //Calculate the total
            total = billField + tip;
        }

        //Load the textView objects
        final TextView tipView = findViewById(R.id.tip_Output);
        final TextView totalView = findViewById(R.id.total_Output);

        //Set the text of the output fields formatted to two decimal places
        tipView.setText(String.format("$%1.2f", tip));
        totalView.setText(String.format("$%1.2f", total));
    }

    private static long sayBackPress;

    @Override
    public void onBackPressed() {
        if (sayBackPress + 1500 > System.currentTimeMillis()){
            super.onBackPressed();
        }
        else{
            Toast.makeText(MainActivity.this, "Back again to exit", Toast.LENGTH_SHORT).show();
            sayBackPress = System.currentTimeMillis();
        }
    }
}