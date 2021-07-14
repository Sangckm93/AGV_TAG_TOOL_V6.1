package com.gmail.agv_tag_tool_v6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class PersonalActivity extends AppCompatActivity {
    SearchView InputTag;
    ListView lvRecentTag;
    ImageButton addFavorite, inputDone;
    Button Clr_favorite, Clr_History, Clr_all, Btn_Submit;
    public ArrayList<TagShow> arrayTag_edit, arrayTag_Save;
    public TagShowAdapter adapterRecentTagList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        // Lock auto rotate screen
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        // Init variable
        Variable_init();
        // Menu bar action select
        menubar_activity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSaved_data();
        arrayTag_edit = new ArrayList<>(arrayTag_Save);
        adapterRecentTagList = new TagShowAdapter(PersonalActivity.this, R.layout.line_tag_show,arrayTag_edit);
        lvRecentTag.setAdapter(adapterRecentTagList);
        add_Favorite_Tag();
        Remove_selected_item();
        Remove_favorite_Tag();
        Remove_history_Tag();
        Remove_all_Tag();
        Btn_Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_data();
            }
        });
    }



    @Override
    protected void onPause() {
        super.onPause();
    }

    private void save_data()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("ListViewData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(arrayTag_edit);
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
    private String get_time()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
        String date_time = simpleDateFormat.format(calendar.getTime());
        return date_time;
    }
    private void Remove_selected_item() {
        lvRecentTag.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int CurrentPosition, long id) {
                Toast.makeText(PersonalActivity.this,adapterRecentTagList.getItem(CurrentPosition).toString()+ " was removed!", Toast.LENGTH_LONG).show();
                arrayTag_edit.remove(CurrentPosition);
                adapterRecentTagList.notifyDataSetChanged();
                return false;
            }
        });
    }
    private void Remove_favorite_Tag() {
        Clr_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i<arrayTag_edit.size();i++)
                {
                    if (arrayTag_edit.get(i).getTag_type().contains("Favorite Tag")) {
                        arrayTag_edit.remove(i);
                        i--;
                        Toast.makeText(PersonalActivity.this,"All Favorite tag removed!", Toast.LENGTH_LONG).show();
                        adapterRecentTagList.notifyDataSetChanged();
                    }
                }
            }
        });
    }
    private void Remove_all_Tag() {
        Clr_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                arrayTag_edit.removeAll(arrayTag_edit);
                arrayTag_edit.clear();
//                arrayTag_edit = null;
                Toast.makeText(PersonalActivity.this,"All Tag removed!", Toast.LENGTH_LONG).show();
                adapterRecentTagList.notifyDataSetChanged();
            }
        });
    }

    private void Remove_history_Tag() {
        Clr_History.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i<arrayTag_edit.size();i++)
                {
                    if (!arrayTag_edit.get(i).getTag_type().contains("Favorite Tag")) {
                        arrayTag_edit.remove(i);
                        i--;
                        Toast.makeText(PersonalActivity.this,"All History tag removed!", Toast.LENGTH_LONG).show();
                        adapterRecentTagList.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void add_Favorite_Tag() {
        addFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean check_appeared = true;
                if (InputTag.getQuery().toString().equals(""))
                {
                    Toast.makeText(PersonalActivity.this, " Type Tag First!", Toast.LENGTH_SHORT).show();
                }else{
                    for (int i = 0; i<arrayTag_edit.size(); i++){
                        if (InputTag.getQuery().toString().toLowerCase().contains(arrayTag_edit.get(i).getTag_value().toLowerCase()))
                        {
                            Toast.makeText(PersonalActivity.this, "Tag is available", Toast.LENGTH_SHORT).show();
                            check_appeared = true;
                            i = arrayTag_edit.size();
                        }else check_appeared = false;
                    }
                    if ((check_appeared == false)||arrayTag_edit.isEmpty())
                    {
                        String Tag_save = InputTag.getQuery().toString();
                        InputTag.setQuery("", true);
                        arrayTag_edit.add(0, new TagShow(Tag_save, "Favorite Tag", "favorite", get_time()));
                        adapterRecentTagList.notifyDataSetChanged();
                        Toast.makeText(PersonalActivity.this, "Added " + Tag_save + " to Favorite Tag", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
    private void menubar_activity() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // Set mainscreen selected
        bottomNavigationView.setSelectedItemId(R.id.personal);
        //perform Item selected
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId())
                {
                    case R.id.personal:
                        return true;
                    case R.id.mainscreen:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
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
    private void Variable_init() {
        InputTag    = findViewById(R.id.searchviewInputTag);
        lvRecentTag = findViewById(R.id.listViewRecentTag);
        inputDone   = findViewById(R.id.btnInputDone);
        addFavorite = findViewById(R.id.btnAddFavorite);
        Btn_Submit  = findViewById(R.id.BtnSubmit);
        Clr_all     = findViewById(R.id.ClrAll);
        Clr_favorite= findViewById(R.id.ClrFavorite);
        Clr_History = findViewById(R.id.ClrHistory);

    }
}
