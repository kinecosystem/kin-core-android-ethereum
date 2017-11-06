package kin.sdk.core.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import kin.sdk.core.Balance;
import kin.sdk.core.ResultCallback;

/**
 * Created by shaybaz on 06/11/2017.
 */

public class WalletFragment extends BaseFragment {
    public static final String TAG = WalletFragment.class.getSimpleName();
    private static final String ARG_IS_MAIN_NET = "main_net";
    private View sendTransactionBtn, updateBalanceBtn;
    private TextView publicKey, balance;

    public static WalletFragment newInstance(boolean isMainNet) {
        WalletFragment fragment = new WalletFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_MAIN_NET, isMainNet);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("###", "#### on resume wallet");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d("###", "###user  wallet " + isVisibleToUser);
    }

    @Override
    public String getTitle() {
        return "WalletF";
    }

    @Override
    public boolean hasBack() {
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wallet_fragment, container, false);
        publicKey = view.findViewById(R.id.public_key);
        balance = view.findViewById(R.id.balance);
        sendTransactionBtn = view.findViewById(R.id.send_transaction_btn);
        updateBalanceBtn = view.findViewById(R.id.update_balance_btn);
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(ARG_IS_MAIN_NET)) {
            final boolean isMain = arguments.getBoolean(ARG_IS_MAIN_NET);
            if (isMain) {
                int color = getActivity().getResources().getColor(R.color.colorMain);
                sendTransactionBtn.setBackgroundColor(color);
                updateBalanceBtn.setBackgroundColor(color);
            }
        }

        sendTransactionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kinOperations.createAccount();
            }
        });

        initWalletData();
        return view;
    }

    private void initWalletData() {
        publicKey.setText(kinOperations.getPublicAddress());
        kinOperations.getBalance(new ResultCallback<Balance>() {
            @Override
            public void onResult(Balance result) {
                balance.setText(result.toString());
            }

            @Override
            public void onError(Exception e) {
                kinOperations.onError("Cant get Balance", e.getMessage());
            }
        });

    }
}
