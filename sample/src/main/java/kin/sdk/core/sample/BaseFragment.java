package kin.sdk.core.sample;

import android.app.Fragment;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

/**
 * Fragment with back and title in the action bar
 */

public abstract class BaseFragment extends Fragment {
    protected KinOperations kinOperations;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getActivity() instanceof KinOperations) {
            kinOperations = (KinOperations) getActivity();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTitle();
    }

    private void updateTitle(){
        if (getActivity() instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            actionBar.setTitle(getTitle());
            actionBar.setDisplayHomeAsUpEnabled(hasBack());
        }
    }

    public abstract String getTitle();
    public abstract boolean hasBack();
}
