package com.andy.grailplayersubjectivetest;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DisplaySummaryActivity extends Activity {

    String  USER_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_display_summary);

        Intent intent = getIntent();
        USER_NAME = intent.getStringExtra("user_name");

        TextView textView = (TextView) this.findViewById(R.id.summary_id);
        textView.setText(USER_NAME);

        xmlFileAnalysis xml = new xmlFileAnalysis();
        xml.setXMLFileName(USER_NAME);

        List<xmlFileAnalysis> xmlItems = new ArrayList<>();
        for (int i=1; i<=xml.getDataIndex(); i++) {

            xmlFileAnalysis xmlItem = new xmlFileAnalysis();
            xmlItem.setXMLFileName(USER_NAME);
            xmlItem.setFlag(i);
            xmlItem.setKeyAndValue();
            xmlItems.add(xmlItem);

        }

        ListView listView = (ListView) this.findViewById(R.id.list_view);

        List<HashMap<String, Object>> data = new ArrayList<HashMap<String,Object>>();

        for (xmlFileAnalysis xmlItem : xmlItems) {

            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("content", xmlItem.getContent());
            item.put("rmhd", xmlItem.getRmhd());
            item.put("264", xmlItem.getH264());
            item.put("similar", xmlItem.getSimilar());
            item.put("total", xmlItem.getTotal());
            data.add(item);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.layout_display_summary_item,
                new String[]{"content", "rmhd", "264", "similar", "total"},
                new int[]{R.id.content, R.id.rmhd, R.id.h264, R.id.similar, R.id.total});

        listView.setAdapter(adapter);

        //Toast.makeText(DisplaySummaryActivity.this, USER_NAME, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_summary, menu);
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

    public class xmlFileAnalysis {

        private String content;
        private int rmhd;
        private int h264;
        private int similar;
        private int total;
        private int flag;

        int dataIndex;
        SharedPreferences xmlFile;

        public String getContent() {
            return content;
        }

        public int getRmhd() {
            return rmhd;
        }

        public int getH264() {
            return h264;
        }

        public int getSimilar() {
            return similar;
        }

        public int getTotal() {
            return total;
        }

        public int getDataIndex() {
            return dataIndex;
        }

        public void setFlag(int i) {
            flag = i;
        }

        protected void setXMLFileName(String file_name) {

            xmlFile = getSharedPreferences(file_name, MODE_PRIVATE);
            Map<String, ?> userFileMap = xmlFile.getAll();

            dataIndex = userFileMap.size() / 3;

            //Toast.makeText(DisplaySummaryActivity.this, "Test " + dataIndex, Toast.LENGTH_SHORT).show();
        }

        private void setKeyAndValue() {

            String flagString = String.valueOf(flag);

            String rmhdKey = flagString + "-264_1";
            String h264Key = flagString + "-264_0";
            String similarKey = flagString + "-264_2";

            content = flagString + "-264";
            rmhd = xmlFile.getInt(rmhdKey, 0);
            h264 = xmlFile.getInt(h264Key, 0);
            similar = xmlFile.getInt(similarKey, 0);
            total = rmhd + h264 + similar;

        }


    }

}
