package com.battleshippark.bsp_langpod.presentation.setting;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.battleshippark.bsp_langpod.R;
import com.battleshippark.bsp_langpod.util.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SettingFragment extends Fragment {
    private static final String TAG = SettingFragment.class.getSimpleName();
    private static final Logger logger = new Logger(TAG);
    @BindView(R.id.wifi_checkbox)
    CheckBox wifiCheckbox;
    @BindView(R.id.cache_button)
    Button cacheButton;
    Unbinder unbinder;

    public SettingFragment() {
    }

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.wifi_checkbox, R.id.cache_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.wifi_checkbox:
                break;
            case R.id.cache_button:
                break;
        }
    }
}
