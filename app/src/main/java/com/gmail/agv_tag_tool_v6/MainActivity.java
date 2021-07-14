package com.gmail.agv_tag_tool_v6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcV;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    SearchView InputTag;
    ListView lvRecentTag;
    ImageButton addFavorite, inputDone;
    String getTagWritevalue;
//    ArrayList<String> arrayTag, arrayListHistory, arrayListFavorite;
    public ArrayList<TagShow> arrayTag, arrayTag_Save;
    //    ArrayAdapter adapterRecentTagList;
    public TagShowAdapter adapterRecentTagList;


    private TextView txt_TagContent;
    private TextView txt_status;
    private Button   btn_writeTag, btn_Copytag;
    private byte[] tagWrite_buff;
    byte[] response;

    private Tag mTag, currentTag;
    private NfcAdapter myNfcAdapter;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private PendingIntent mPendingIntent;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Lock auto rotate screen
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        //
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setIcon(R.mipmap.ic_launcher);
        // Init variable
        Variable_init();
        // Menu bar action select
        menubar_activity();
        // initialize NFC
        configureNfc();
        if ((myNfcAdapter==null)|| !myNfcAdapter.isEnabled())
        {
            txt_status.setText("NFC is not available!");
            txt_status.setTextColor(Color.RED);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        getSaved_data();
        if (arrayTag_Save==null) {
            arrayTag_Save = new ArrayList<>();
            arrayTag = new ArrayList<>(arrayTag_Save);
        }else arrayTag = new ArrayList<>(arrayTag_Save);

        adapterRecentTagList = new TagShowAdapter(MainActivity.this, R.layout.line_tag_show,arrayTag);
        lvRecentTag.setAdapter(adapterRecentTagList);
//        arrayTag = new ArrayList<>(arrayListFavorite);
//        adapterRecentTagList = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, arrayTag);

        // perform set on query text focus change listener event
        InputTag.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // do something when the focus of the query text field changes
                lvRecentTag.setVisibility(View.VISIBLE);
            }
        });
        // Filter Item
        InputTag.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
//                if (!addFavorite.isSelected()&&!btn_writeTag.isSelected())
                adapterRecentTagList.filter(s);
                lvRecentTag.setVisibility(View.VISIBLE);
//                adapterRecentTagList.getFilter().filter(s);
                return false;
            }
        });
        // Select item on list
        lvRecentTag.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                InputTag.setQuery(adapterRecentTagList.getItem(pos).toString(), true);
                lvRecentTag.setVisibility(View.GONE);
            }
        });
        inputDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                lvRecentTag.setVisibility(View.INVISIBLE);
            }
        });
        addFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (InputTag.getQuery().toString().equals(""))
                {
                    Toast.makeText(MainActivity.this, " Type Tag First!", Toast.LENGTH_SHORT).show();
                }else{
//                for (int i = 0; i<arrayListFavorite.size(); i++){
//                    if ((InputTag.getQuery().toString().equals(arrayListFavorite.get(i))&&InputTag.getQuery().toString().equals(""))){
                    for (int i = 0; i<arrayTag.size(); i++){
                        if (InputTag.getQuery().toString().toLowerCase().contains(arrayTag.get(i).getTag_value().toLowerCase()))
                        {
                            Toast.makeText(MainActivity.this, "Tag is available", Toast.LENGTH_SHORT).show();
                            i = arrayTag.size();
                        }
                    }
                    if (arrayTag.size()== 0) // arrayTag = null after Filter
                    {
//                    arrayListFavorite.add(InputTag.getQuery().toString());
//                    arrayTag.add(InputTag.getQuery().toString());
                        String Tag_save = InputTag.getQuery().toString();
                        InputTag.setQuery("", true);
                        txt_status.setText("Added " + Tag_save + " to Favorite");
                        txt_status.setTextColor(Color.BLUE);
                        arrayTag.add(0, new TagShow(Tag_save, "Favorite Tag", "favorite", get_time()));

                        adapterRecentTagList = new TagShowAdapter(MainActivity.this, R.layout.line_tag_show, arrayTag);
                        lvRecentTag.setAdapter(adapterRecentTagList);
                        save_data();
                        Toast.makeText(MainActivity.this, "Added " + Tag_save + " to Favorite Tag", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        btn_Copytag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                InputTag.setQuery(StringUtil.getHexString(response),true);
                InputTag.setQuery(txt_TagContent.getText(),true);
                lvRecentTag.setVisibility(View.GONE);
                btn_Copytag.setEnabled(false);
            }
        });
        enableForegroundDispatchSystem();
    }
    @Override
    protected void onNewIntent(Intent intent) {
//        adapterRecentTagList = new TagShowAdapter(MainActivity.this, R.layout.line_tag_show,arrayTag);
//        lvRecentTag.setAdapter(adapterRecentTagList);
        lvRecentTag.setVisibility(View.GONE);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()) || NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {

            currentTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            mTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            myNfcV_ReadNewTag();

            if (!txt_TagContent.getText().toString().equals("")) {
                String getInputValue = InputTag.getQuery().toString();
                InputTag.setQuery("", true);
                adapterRecentTagList.notifyDataSetChanged();
//                arrayTag.add(0, txt_TagContent.getText().toString());
                arrayTag.add(0,new TagShow(txt_TagContent.getText().toString(),"Read","history", get_time()));
                adapterRecentTagList.notifyDataSetChanged();
                lvRecentTag.setVisibility(View.GONE);
                hideKeyboard();
                save_data();
                btn_Copytag.setEnabled(true);
                InputTag.setQuery(getInputValue, true);
            }
        }
        // Write button click
        btn_writeTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTagWritevalue = InputTag.getQuery().toString();
                InputTag.setQuery("", true);
//                adapterRecentTagList.notifyDataSetChanged();
                if (!getTagWritevalue.equals("")){
                    writeTag();
                    arrayTag.add(0,new TagShow(getTagWritevalue.toUpperCase(),"Write", "history", get_time()));
                    adapterRecentTagList = new TagShowAdapter(MainActivity.this, R.layout.line_tag_show,arrayTag);
                    lvRecentTag.setAdapter(adapterRecentTagList);

                    lvRecentTag.setVisibility(View.GONE);
                    hideKeyboard();
                    btn_Copytag.setEnabled(true);
                    getTagWritevalue= "";
                    InputTag.setQuery(getTagWritevalue, true);
                    save_data();
                }else
                    Toast.makeText(MainActivity.this, "Input a Tag value first!", Toast.LENGTH_SHORT).show();
            }
        });
        super.onNewIntent(intent);
    }

    @Override
    protected void onDestroy() {
        save_data();
        super.onDestroy();
    }

    protected void onPause() {
        super.onPause();
        save_data();
        disableForegroundDispatchSystem();
    }

    private void menubar_activity() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // Set mainscreen selected
        bottomNavigationView.setSelectedItemId(R.id.mainscreen);
        //perform Item selected
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId())
                {
                    case R.id.personal:
                        startActivity(new Intent(getApplicationContext(),PersonalActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.mainscreen:
                        return true;
                    case R.id.about:
                        startActivity(new Intent(getApplicationContext(),AboutActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    private void disableForegroundDispatchSystem() {
        myNfcAdapter.disableForegroundDispatch(this);
    }

    private void enableForegroundDispatchSystem() {
        if (myNfcAdapter != null) {
            myNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
        }
    }

    private void hideKeyboard() {
        View view = MainActivity.this.getCurrentFocus();
        if (view!= null){
            InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    private void configureNfc() {
        myNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter intnfcv = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        mFilters = new IntentFilter[] {intnfcv};
        mTechLists = new String[][] { new String[] {NfcV.class.getName() }};
    }
    private String get_time()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
        String date_time = simpleDateFormat.format(calendar.getTime());
        return date_time;
    }

    private void myNfcV_ReadNewTag() {
        byte[] id = currentTag.getId();
        for (String tech : currentTag.getTechList()) {
            if (tech.equals(NfcV.class.getName())) {
                final NfcV nfcvTag = NfcV.get(currentTag);
                try {
                    nfcvTag.connect();
//                    Toast.makeText(this, "Tag detected!", Toast.LENGTH_LONG).show();
                    byte[] cmd = new byte[]{
                            (byte)0x20,     // flags: addressed (= UID field present)
//                            (byte)0x2B,     // command: Read Single block
                            (byte)0x23,     // command: READ MULTIPLE BLOCKS
//                            (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, // placeholder for tag UID
                            (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, // placeholder for tag UID
                            (byte)(0x00),  // first block number__Number of block to read
                            (byte)(0x00) // number of blocks
                    };
                    System.arraycopy(id, 0, cmd, 2, 8);
                    response = nfcvTag.transceive(cmd);
                    txt_TagContent.setText(StringUtil.getHexString(response));
                    txt_TagContent.setTextColor(Color.GREEN);
                    txt_status.setText(" Detected new tag");
                    txt_status.setTextColor(Color.BLUE);
                    nfcvTag.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private synchronized void writeTag(){
        if(mTag !=null){
            try {
                new WriteVecinityTagTask().execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            mTag=null;
        }
    }
    private class WriteVecinityTagTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(result)
            {
                txt_status.setText(" Write Tag Successful");
//                txt_status.setTextSize(48);
                txt_status.setTextColor(Color.GREEN);
//                arrayTag.add(0,InputTag.getQuery().toString());
//                adapterRecentTagList.notifyDataSetChanged();
            }else{
                txt_status.setText(" Write Tag Error");
                txt_status.setTextColor(Color.RED);
            }
//            String message = (result) ? "WRITE OK" : "ERROR";
//            Toast.makeText(MainActivity.this, message , Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
//            String Tag_Data="";
            String Tag_Data = getTagWritevalue.toUpperCase();
            tagWrite_buff = Tag_Data.getBytes();
            byte[] nfcv_sendTag = new byte[]{
                    0x00,                   // flags: addressed (= UID field present)
                    0x21,                   // command: Write single block
                    (byte)0,                // block 0
                    0x00,0x00,0x00,0x00     // 4 byte data
            };
            System.arraycopy(tagWrite_buff, 0, nfcv_sendTag, 3, 4); // copy 4 byte data from edittext to struct to send command
            try{
                NfcV vecinityTag = NfcV.get(mTag);
                vecinityTag.connect();
                vecinityTag.transceive(nfcv_sendTag);
//                vecinityTag.transceive(
//                        new byte[] {0x00, 0x21, (byte) 0,0x30, 0x54, 0x30, 0x33}
//                );
                txt_TagContent.setText(Tag_Data);
                txt_TagContent.setTextColor(Color.GREEN);
                vecinityTag.close();
                return true;
            }catch(IOException e){
                e.printStackTrace();
                return false;
            }
//            return true;
        }
    }
    private void save_data()
    {
        data_storage_collect();
        SharedPreferences sharedPreferences = getSharedPreferences("ListViewData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
//        String json = gson.toJson(arrayListFavorite);
//        editor.putString("FavoriteTag",json);
        String json = gson.toJson(arrayTag_Save);
        editor.putString("TagList",json);
        editor.apply();
    }
    private void getSaved_data()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("ListViewData", MODE_PRIVATE);
        Gson gson = new Gson();
//        String json = sharedPreferences.getString("FavoriteTag",null);
//        Type type = new TypeToken<ArrayList<String>>(){}.getType();
//        arrayListFavorite = gson.fromJson(json, type);
//        if (arrayListFavorite== null) {
//            arrayListFavorite = new ArrayList<String>();
//        }
//        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        String json = sharedPreferences.getString("TagList",null);
        Type type = new TypeToken<ArrayList<TagShow>>(){}.getType();
        arrayTag_Save = gson.fromJson(json,type);
//        if ((arrayTag_Save== null)) {
//            arrayTag_Save = new ArrayList<>();
//            arrayTag_Save.add(new TagShow("0T00","Favorite Tag", "favorite",get_time()));
////            arrayTag_Save.add(new TagShow("0T02","Favorite Tag", "favorite",get_time()));
////            arrayTag_Save.add(new TagShow("0T03","Favorite Tag", "favorite",get_time()));
////            arrayTag_Save.add(new TagShow("0T05","Favorite Tag", "favorite",get_time()));
////            arrayTag_Save.add(new TagShow("0T07","Favorite Tag", "favorite",get_time()));
////            arrayTag_Save.add(new TagShow("0T08","Favorite Tag", "favorite",get_time()));
//        }
    }
    private void data_storage_collect() {
        boolean check_appear = true;
        if (arrayTag_Save.isEmpty())
            arrayTag_Save.addAll(arrayTag);
        else {
            for (int i = 0; i < arrayTag.size(); i++) {
                for (int j = 0; j < arrayTag_Save.size(); j++) {
                    if (arrayTag.get(i).equals(arrayTag_Save.get(j))) {
                        check_appear = true;
                        j = arrayTag_Save.size();
                    } else check_appear = false;
                }
                if (check_appear == false) {
                    arrayTag_Save.add(0, arrayTag.get(i));
                    check_appear = true;
                }
            }
        }
    }
    private void Variable_init() {
        InputTag    = findViewById(R.id.searchviewInputTag);
        lvRecentTag = findViewById(R.id.listViewRecentTag);
        inputDone   = findViewById(R.id.btnInputDone);
        addFavorite = findViewById(R.id.btnAddFavorite);

        txt_TagContent  = findViewById(R.id.txt_TagContent);
        btn_writeTag    = findViewById(R.id.btn_writeTag);
        btn_Copytag     = findViewById(R.id.btnCopyTag);
        txt_status      = findViewById(R.id.txtStatus);
    }
}
