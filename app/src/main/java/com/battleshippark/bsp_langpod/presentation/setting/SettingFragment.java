package com.battleshippark.bsp_langpod.presentation.setting;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.battleshippark.bsp_langpod.R;
import com.battleshippark.bsp_langpod.domain.GetStoredValue;
import com.battleshippark.bsp_langpod.domain.PutStoredValue;
import com.battleshippark.bsp_langpod.util.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.subscriptions.CompositeSubscription;

public class SettingFragment extends Fragment {
    private static final String TAG = SettingFragment.class.getSimpleName();
    private static final Logger logger = new Logger(TAG);

    @BindView(R.id.wifi_checkbox)
    CheckBox wifiCheckbox;
    @BindView(R.id.cache_button)
    Button cacheButton;
    private Unbinder unbinder;

    private GetStoredValue getStoredValue;
    private PutStoredValue putStoredValue;
    private final CompositeSubscription subscription = new CompositeSubscription();

    public SettingFragment() {
    }

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getStoredValue = new GetStoredValue();
        putStoredValue = new PutStoredValue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initUI();
    }

    private void initUI() {
        subscription.add(
                getStoredValue.downloadOnlyWifi().subscribe(value -> wifiCheckbox.setSelected(value))
        );
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        subscription.unsubscribe();
        super.onDestroy();
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
