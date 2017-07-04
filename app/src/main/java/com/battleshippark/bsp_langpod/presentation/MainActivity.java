package com.battleshippark.bsp_langpod.presentation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.widget.Toast;

import com.battleshippark.bsp_langpod.R;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.presentation.channel.ChannelActivity;
import com.battleshippark.bsp_langpod.presentation.entire_list.EntireChannelListFragment;
import com.battleshippark.bsp_langpod.presentation.my_list.MyChannelListFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends Activity implements EntireChannelListFragment.EntireListFragmentListener, MyChannelListFragment.MyListFragmentListener {
    private Unbinder unbinder;

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);

        initUI();

        showEntireList();
    }

    private void showEntireList() {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment_layout, EntireChannelListFragment.newInstance())
                .commit();
    }

    private void showMyList() {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment_layout, MyChannelListFragment.newInstance())
                .commit();
    }

    private void initUI() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        showEntireList();
                        break;
                    case 1:
                        showMyList();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    @Override
    public void onClickEntireListItem(ChannelRealm item) {
        startActivity(new Intent(this, ChannelActivity.class));
        Toast.makeText(this, "CLICK", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClickMyChannelItem(ChannelRealm item) {

    }
}
