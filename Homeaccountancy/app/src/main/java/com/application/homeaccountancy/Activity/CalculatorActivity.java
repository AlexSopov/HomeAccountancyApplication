package com.application.homeaccountancy.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.application.homeaccountancy.R;

import java.util.regex.Pattern;

public class CalculatorActivity extends AppCompatActivity {

    private TextView resultDisplayTextView;
    private String displayText = "0";
    private String currentOperator = "";
    private String result = "";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculator_activity);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("result")) {
            displayText = extras.getString("result");
        }

        resultDisplayTextView = (TextView)findViewById(R.id.textView);
        resultDisplayTextView.setText(displayText);
    }

    private void updateDisplayText(){
        resultDisplayTextView.setText(displayText);
    }
    private double operate(String a, String b, String op){
        switch (op){
            case "+": return Double.valueOf(a) + Double.valueOf(b);
            case "-": return Double.valueOf(a) - Double.valueOf(b);
            case "x": return Double.valueOf(a) * Double.valueOf(b);
            case "÷":
                if (Double.valueOf(b) == 0)
                    return 0;
                else
                    return Double.valueOf(a) / Double.valueOf(b);
            default: return -1;
        }
    }
    private boolean getResult(){
        if(currentOperator.isEmpty())
            return false;

        String[] operation = displayText.split(Pattern.quote(currentOperator));
        if(operation.length < 2)
            return false;

        result = String.valueOf(operate(operation[0], operation[1], currentOperator));
        return true;
    }
    private boolean isOperator(char operator){
        switch (operator){
            case '+':
            case '-':
            case 'x':
            case '÷': return true;
            default: return false;
        }
    }

    public void onClickClear(View v){
        clearText();
        updateDisplayText();
    }
    public void onClickNumber(View v){
        if(!result.equals("")){
            clearText();
            updateDisplayText();
        }
        Button senderButton = (Button) v;
        displayText += senderButton.getText();

        updateDisplayText();
    }
    public void onClickOperator(View v){
        if(displayText.isEmpty())
            return;

        Button senderButton = (Button)v;

        if(!result.isEmpty()){
            String currentDisplay = result;
            clearText();
            displayText = currentDisplay;
        }

        if(!currentOperator.isEmpty()){
            if(isOperator(displayText.charAt(displayText.length()-1))){
                displayText = displayText.replace(displayText.charAt(displayText.length()-1), senderButton.getText().charAt(0));
                updateDisplayText();
                return;
            }
            else{
                getResult();
                displayText = result;
                result = "";
            }
            currentOperator = senderButton.getText().toString();
        }
        displayText += senderButton.getText();
        currentOperator = senderButton.getText().toString();
        updateDisplayText();
    }
    public void onClickEqual(View v){
        if(displayText.equals("") || !getResult())
            return;

        displayText = result;
        resultDisplayTextView.setText(result);
    }
    public void onClickDeleteSymbol(View view) {
        if (displayText.length() > 0) {
            displayText = displayText.substring(0, displayText.length() - 1);
            updateDisplayText();
        }
    }
    public void onClickOk(View view) {
        Intent data = new Intent();

        try
        {
            double temp = Double.valueOf(displayText);
            if (Math.abs(temp) < 0.1)
                data.putExtra("result", 0);
            else if (temp > 1e6)
                data.putExtra("result", 1e6);
            else if (temp < -1e6)
                data.putExtra("result", -1e6);
            else
                data.putExtra("result", Math.abs(temp));
        }
        catch (Exception e) {
            data.putExtra("result", 0);
        }

        setResult(RESULT_OK, data);
        finish();
    }

    private void clearText(){
        displayText = "";
        currentOperator = "";
        result = "";
    }
}