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

public class CreateAccountFragment extends BaseFragment {

    public static final String TAG = CreateAccountFragment.class.getSimpleName();
    private static final String ARG_IS_MAIN_NET = "main_net";
    private View createAccountBtn;

    public static CreateAccountFragment newInstance(boolean isMainNet) {
        CreateAccountFragment fragment = new CreateAccountFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_MAIN_NET, isMainNet);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("###", "#### on resume craree accoubt");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d("###", "###user  accont " + isVisibleToUser);
    }

    @Override
    public String getTitle() {
        return "Create Fff acoint";
    }

    @Override
    public boolean hasBack() {
        return true;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_account_fragment, container, false);
        createAccountBtn = view.findViewById(R.id.btn_create_account);
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(ARG_IS_MAIN_NET)) {
            final boolean isMain = arguments.getBoolean(ARG_IS_MAIN_NET);
            if (isMain) {
                int color = getActivity().getResources().getColor(R.color.colorMain);
                createAccountBtn.setBackgroundColor(color);
            }
        }
        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kinOperations.createAccount();
            }
        });
        return view;
    }
}
