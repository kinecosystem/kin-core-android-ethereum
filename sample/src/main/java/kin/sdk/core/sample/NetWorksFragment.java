package kin.sdk.core.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by shaybaz on 06/11/2017.
 */

public class NetWorksFragment extends BaseFragment {

    public static final String TAG = NetWorksFragment.class.getSimpleName();

    public static NetWorksFragment newInstance() {
        NetWorksFragment fragment = new NetWorksFragment();
        return fragment;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d("###", "###user  network " + isVisibleToUser);
    }



    @Override
    public String getTitle() {
        return "Networks";
    }

    @Override
    public boolean hasBack() {
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("###", "#### on resume networkd");
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d("###", "###user net work  onCreateView " );

        View view = inflater.inflate(R.layout.networks_fragmnet, container, false);
        view.findViewById(R.id.btn_test_net).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kinOperations.setMainNetwork(false);
            }
        });
        view.findViewById(R.id.btn_main_net).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kinOperations.setMainNetwork(true);
            }
        });

        return view;
    }
}
