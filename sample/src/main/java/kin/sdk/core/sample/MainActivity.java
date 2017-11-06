package kin.sdk.core.sample;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import kin.sdk.core.Balance;
import kin.sdk.core.KinClient;
import kin.sdk.core.ResultCallback;
import kin.sdk.core.ServiceProvider;
import kin.sdk.core.exception.CreateAccountException;
import kin.sdk.core.exception.EthereumClientException;

public class MainActivity extends AppCompatActivity implements KinOperations {

    private final String INFURA_ROPSTEN_BASE_URL = "https://ropsten.infura.io/";
    private final String INFURA_MAIN_BASE_URL = "https://mainnet.infura.io/";
    //replace with your infura token
    private final String infuraToken = "yourInfuraToken";
    private KinClient kinClientTest, kinClientMain;
    private View btnMainNet, btnTestNet;
    private boolean isMainNet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnMainNet = findViewById(R.id.btn_main_net);
        btnTestNet = findViewById(R.id.btn_test_net);
        addFragment(NetWorksFragment.newInstance(), NetWorksFragment.TAG);
        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Log.d("###", "### on backstatge chabge ");
                Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment_container);
                Log.d("####", "#### fragment  " + fragment);
                if(fragment == null){
                    finish();
                }else {
                    BaseFragment baseFragment = (BaseFragment) fragment;
                    getSupportActionBar().setTitle(baseFragment.getTitle());
                    getSupportActionBar().setDisplayHomeAsUpEnabled(baseFragment.hasBack());
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getFragmentManager().popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initKinClient() {
        try {
            final String baseUrl = isMainNet ? INFURA_MAIN_BASE_URL : INFURA_ROPSTEN_BASE_URL;
            final int netWorkId = isMainNet ? ServiceProvider.NETWORK_ID_MAIN : ServiceProvider.NETWORK_ID_ROPSTEN;
            final KinClient kinClient = new KinClient(getApplicationContext(),
                    new ServiceProvider(baseUrl + infuraToken, netWorkId));
            if (isMainNet) {
                kinClientMain = kinClient;
            } else {
                kinClientTest = kinClient;
            }
            onInitComplete();
        } catch (EthereumClientException e) {
            e.printStackTrace();

        }
    }

    private void onInitComplete() {
        if (!getKinClient().hasAccounts()) {
            addFragment(CreateAccountFragment.newInstance(isMainNet), CreateAccountFragment.TAG);
        } else {
            addFragment(WalletFragment.newInstance(isMainNet), WalletFragment.TAG);
        }
    }

    @Override
    public void onBackPressed() {
        getFragmentManager().popBackStack();
    }

    private void addFragment(Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, fragment, tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    private KinClient getKinClient() {
        return isMainNet ? kinClientMain : kinClientTest;
    }

    private String getPassPhrase() {
        return "12345";
    }


    @Override
    public void createAccount() {
        try {
            getKinClient().createAccount(getPassPhrase());
            removFragment(CreateAccountFragment.TAG);
            addFragment(WalletFragment.newInstance(isMainNet), WalletFragment.TAG);
        } catch (CreateAccountException e) {
            e.printStackTrace();
        }
    }

    private void removFragment(String tag) {
        getFragmentManager().beginTransaction()
                .remove(getFragmentManager().findFragmentByTag(tag))
                .commit();
        getFragmentManager().popBackStack();
    }

    @Override
    public void sendTransaction() {
        Log.d("###", "#### main sendTransaction");

    }

    @Override
    public String getPublicAddress() {
        return getKinClient().getAccount().getPublicAddress();
    }

    @Override
    public void getBalance(ResultCallback<Balance> callback) {
        getKinClient().getAccount().getBalance(callback);
    }

    @Override
    public void onError(String title, String error) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(error);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    public void setMainNetwork(boolean isMainNet) {
        this.isMainNet = isMainNet;
        if (getKinClient() == null) {
            initKinClient();
        } else {
            onInitComplete();
        }
    }
}