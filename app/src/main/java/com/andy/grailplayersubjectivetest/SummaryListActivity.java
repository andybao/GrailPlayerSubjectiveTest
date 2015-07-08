package com.andy.grailplayersubjectivetest;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;


public class SummaryListActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_list);

        String contentXMLFolder = "/data/data/com.andy.grailplayersubjectivetest/shared_prefs";

        final ArrayList<String> summaryList;
        ListView listView = (ListView) this.findViewById(R.id.summary_list);

        summaryList = createDataIndex(contentXMLFolder, "xml");
        summaryList.remove("user_summary");
        summaryList.remove("montior_server_info");
        summaryList.remove("summary");
        summaryList.add(0, "summary");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SummaryListActivity.this,
                android.R.layout.simple_list_item_1, summaryList);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String userName = summaryList.get(i);
                Intent intent = new Intent(SummaryListActivity.this, DisplaySummaryActivity.class);
                intent.putExtra("user_name", userName);
                startActivity(intent);
                //Toast.makeText(SummaryListActivity.this, userName, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_summary_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public ArrayList<String> createDataIndex (String folder_path, String file_name_filter) {

        ArrayList<String> dataIndexArrayList = new ArrayList<String>();
        String[] dataIndexTemp = scanDataFromFolder(folder_path, file_name_filter);

        for(int i = 0; i < dataIndexTemp.length; i++) {
            int dot = dataIndexTemp[i].lastIndexOf(".");
            dataIndexArrayList.add(dataIndexTemp[i].substring(0, dot));
        }

        //String[] dataIndex = (String[]) dataIndexArrayList.toArray();

        return dataIndexArrayList;

    }

    protected String[] scanDataFromFolder (String folder_path, final String file_name_filter) {

        FilenameFilter fileNameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return (s.endsWith(file_name_filter));
            }
        };
        File file = new File(folder_path);

        if (file_name_filter.equals("0")){
            return file.list();
        }
        else {
            return file.list(fileNameFilter);
        }
    }
}
