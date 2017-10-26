package com.battleshippark.bsp_langpod.presentation;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.View;

import com.battleshippark.bsp_langpod.R;
import com.battleshippark.bsp_langpod.presentation.entire_list.EntireChannelListFragment;
import com.battleshippark.bsp_langpod.presentation.my_list.MyChannelListFragment;
import com.battleshippark.bsp_langpod.presentation.setting.SettingFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends Activity {
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.backpress)
    View backPressView;

    private Unbinder unbinder;
    private BackPressHandler backPressHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);

        initData();
        initUI();

        showEntireList();
    }

    private void initData() {
        backPressHandler = new BackPressHandler(this);
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

    private void showSetting() {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment_layout, SettingFragment.newInstance())
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
                    case 2:
                        showSetting();
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
    public void onBackPressed() {
        if (backPressHandler.isClosable()) {
            super.onBackPressed();
        } else {
            backPressHandler.show(backPressView);
        }
    }
}
