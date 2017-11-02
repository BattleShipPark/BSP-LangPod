package com.battleshippark.bsp_langpod.presentation.setting;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.battleshippark.bsp_langpod.R;
import com.battleshippark.bsp_langpod.data.storedvalue.StoredValueApi;
import com.battleshippark.bsp_langpod.domain.GetStoredValue;
import com.battleshippark.bsp_langpod.domain.PutStoredValue;
import com.battleshippark.bsp_langpod.util.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.functions.Actions;
import rx.subscriptions.CompositeSubscription;

public class SettingFragment extends Fragment {
    private static final String TAG = SettingFragment.class.getSimpleName();
    private static final Logger logger = new Logger(TAG);

    @BindView(R.id.wifi_checkbox)
    CheckBox wifiCheckbox;

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

        StoredValueApi storedValueApi = new StoredValueApi(getActivity());
        getStoredValue = new GetStoredValue(storedValueApi);
        putStoredValue = new PutStoredValue(storedValueApi);
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
                getStoredValue.downloadOnlyWifi().subscribe(wifiCheckbox::setChecked)
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

    @OnClick({R.id.wifi_checkbox, R.id.download_list_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.wifi_checkbox:
                putStoredValue.downloadOnlyWifi(wifiCheckbox.isChecked())
                        .subscribe(Actions.empty(), logger::w);
                break;
/*            case R.id.clear_cache_tv:
                new AlertDialog.Builder(getActivity()).setMessage(R.string.setting_clear_cache_message)
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.cancel())
                        .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.cancel())
                        .show();
                break;*/
            case R.id.download_list_tv:
                startActivity(new Intent(getActivity(), SettingDownloadListActivity.class));
                break;
        }
    }
}
