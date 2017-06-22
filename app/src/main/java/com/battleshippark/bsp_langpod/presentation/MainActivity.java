package com.battleshippark.bsp_langpod.presentation;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.widget.Toast;

import com.battleshippark.bsp_langpod.R;
import com.battleshippark.bsp_langpod.presentation.entire_list.EntireListFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends Activity {
    private Unbinder unbinder;

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);

        initUI();

        getFragmentManager()
                .beginTransaction()
                .add(R.id.main_fragment_layout, EntireListFragment.newInstance())
                .commit();
    }

    private void initUI() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Toast.makeText(MainActivity.this, tab.getText(), Toast.LENGTH_SHORT).show();
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

/*    @Override
    public void onListFragmentInteraction(ChannelRealm item) {
        
    }*/
}
