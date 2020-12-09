package com.assessor.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.assessor.android.BaseActivity;
import com.assessor.android.R;
import com.assessor.android.activity.model.BatchInfo;
import com.assessor.android.adapter.SimpleRecyclerAdapter;
import com.assessor.android.bs.BSLogoutDialogFragment;
import com.assessor.android.iface.ItemClickListener;
import com.assessor.android.utility.LocalConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Dashboard extends BaseActivity implements ItemClickListener, View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    List<BatchInfo> batchInfos = new ArrayList<>();
    String date = "26-02-2020";
    ImageView imgDatePicker;
    ItemClickListener clickListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        clickListener = this;
        imgDatePicker = findViewById(R.id.action_date);
        imgDatePicker.setOnClickListener(this);
        init();
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener(this);
        inflater.inflate(R.menu.menu_main, popup.getMenu());
        popup.show();
    }


    public void init() {
        getDumeryData();
        RecyclerView mRecycler = findViewById(R.id.recycler);
        mRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        SimpleRecyclerAdapter menuAdapter = new SimpleRecyclerAdapter(getApplicationContext(), batchInfos, this);
        mRecycler.setAdapter(menuAdapter);
    }

    private void getDumeryData() {
        batchInfos.clear();
        batchInfos.add(new BatchInfo(1, "1937JAMH/Q0301-333221", "Retail", date, "ODAPADA SKILL DEVELOPMENT", "Sahil Sahoo"));
        batchInfos.add(new BatchInfo(2, "2037MH09/A0431-433322", "Electronics", date, "INKTEST TEAM", "Sangam Mishra"));
        batchInfos.add(new BatchInfo(3, "1873JAMH/Q1341-532121", "Retail", date, "SAHAYA TEAM", "Satyajeet Majhi"));
        batchInfos.add(new BatchInfo(4, "1213JEMH/Q3001-300021", "WEB", date, "SKILL DEVELOPMENT", "Sahil Sahoo"));
        batchInfos.add(new BatchInfo(5, "1937JAMH/Q0301-333221", "Retail", date, "INKTEST TEAM", "Sahil Sahoo"));
        batchInfos.add(new BatchInfo(6, "1117JAMH/Q0202-355221", "Electronics", date, "SAHAYA TEAM", "Sahil Sahoo"));
        batchInfos.add(new BatchInfo(7, "1937JAMH/Q0301-333221", "Retail", date, "ODAPADA SKILL DEVELOPMENT", "Sahil Sahoo"));
        batchInfos.add(new BatchInfo(8, "2037MH09/A0431-433322", "Electronics", date, "INKTEST TEAM", "Sangam Mishra"));
        batchInfos.add(new BatchInfo(9, "1873JAMH/Q1341-532121", "Retail", date, "SAHAYA TEAM", "Satyajeet Majhi"));
        batchInfos.add(new BatchInfo(10, "1213JEMH/Q3001-300021", "WEB", date, "SKILL DEVELOPMENT", "Sahil Sahoo"));
        batchInfos.add(new BatchInfo(11, "1937JAMH/Q0301-333221", "Retail", date, "INKTEST TEAM", "Sahil Sahoo"));
        batchInfos.add(new BatchInfo(12, "1117JAMH/Q0202-355221", "Electronics", date, "SAHAYA TEAM", "Sahil Sahoo"));

    }

    @Override
    public void onItemClickListener(Object object) {
        if (object instanceof BatchInfo) {
            BatchInfo batchInfo = (BatchInfo) object;
            Intent in = new Intent(getApplicationContext(), StudentListActivity.class);
            startActivity(in);

        } else if (object instanceof Calendar) {
            Calendar c1 = (Calendar) object;
            Date dateOne = c1.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
            date = sdf.format(dateOne);
            init();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == imgDatePicker.getId()) {
            showDatePicker(this, clickListener);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        showLogOutDialog();
        return false;
    }

    public void showLogOutDialog() {
        BSLogoutDialogFragment bottomSheetFragment = new BSLogoutDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(LocalConstants.CONST_TITLE, getString(R.string.app_name));
        bundle.putString(LocalConstants.CONST_MESSAGE, "Are you sure to logout?");
        bundle.putSerializable(LocalConstants.CONST_LISTENER, null);
        bottomSheetFragment.setArguments(bundle);
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }
}
