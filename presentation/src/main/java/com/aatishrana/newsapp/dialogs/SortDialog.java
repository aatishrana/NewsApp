package com.aatishrana.newsapp.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.aatishrana.newsapp.R;
import com.aatishrana.data.Keys;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aatish on 9/10/2017.
 */

public class SortDialog extends Dialog
{
    private Button btnOk;
    private Spinner spinner;
    private List<String> keys;
    private SortDialogListener listener;

    public interface SortDialogListener
    {
        void onSort(String key);
    }

    public SortDialog(Context context)
    {
        super(context);
        listener = (SortDialogListener) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_sort);

        spinner = (Spinner) findViewById(R.id.dialog_sort_spinner);
        btnOk = (Button) findViewById(R.id.dialog_sort_btn_ok);

        btnOk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (listener != null)
                    listener.onSort(keys.get(spinner.getSelectedItemPosition()));
            }
        });

        keys = new ArrayList<>();
        keys.add(Keys.sortId);
        keys.add(Keys.sortTitle);
        keys.add(Keys.sortPublisher);
        keys.add(Keys.sortCategory);
        keys.add(Keys.sortTime);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, keys);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }
}
