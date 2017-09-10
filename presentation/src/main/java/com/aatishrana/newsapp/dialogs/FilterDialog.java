package com.aatishrana.newsapp.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.aatishrana.newsapp.R;
import com.example.Keys;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aatish on 9/10/2017.
 */

public class FilterDialog extends Dialog implements View.OnClickListener
{
    private Button btnOk;
    private EditText editText;
    private Spinner spinner;
    private FilterDialogListener listener;
    private List<String> keys;

    public interface FilterDialogListener
    {
        void onFilter(String key, String value);
    }

    public FilterDialog(Context context)
    {
        super(context);
        this.listener = (FilterDialogListener) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_filter);

        spinner = (Spinner) findViewById(R.id.dialog_filter_spinner);
        editText = (EditText) findViewById(R.id.dialog_filter_tv);
        btnOk = (Button) findViewById(R.id.dialog_filter_button_ok);
        btnOk.setOnClickListener(FilterDialog.this);

        keys = new ArrayList<>();
        keys.add(Keys.filterTitle);
        keys.add(Keys.filterPublisher);
        keys.add(Keys.filterCategory);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, keys);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }


    @Override
    public void onClick(View v)
    {
        if (listener != null && editText.getText() != null)
            listener.onFilter(keys.get(spinner.getSelectedItemPosition()), editText.getText().toString());
    }
}
