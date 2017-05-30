package com.battleshippark.bsp_langpod.presentation;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import com.battleshippark.bsp_langpod.R;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.presentation.entire_list.EntireListFragment;

public class MainActivity extends Activity implements EntireListFragment.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getFragmentManager()
                .beginTransaction()
                .add(R.id.main_fragment_layout, EntireListFragment.newInstance())
                .commit();
    }

    @Override
    public void onListFragmentInteraction(ChannelRealm item) {
        
    }
}
