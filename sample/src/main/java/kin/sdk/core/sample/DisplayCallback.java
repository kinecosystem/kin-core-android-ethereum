package kin.sdk.core.sample;

import android.content.Context;
import android.view.View;
import java.lang.ref.WeakReference;
import kin.sdk.core.ResultCallback;

/**
 * Will hide a progressBar and display result on a displayView passed at constructor
 * Holds the views as weakReferences and clears the references when canceled
 */
public abstract class DisplayCallback<T> implements ResultCallback<T> {

    private WeakReference<View> progressBarReference;
    private WeakReference<View> displayViewReference;

    public DisplayCallback(View progressBar, View displayView) {
        progressBarReference = new WeakReference<>(progressBar);
        displayViewReference = new WeakReference<>(displayView);
    }

    public DisplayCallback(View progressBar) {
        progressBarReference = new WeakReference<>(progressBar);
    }

    /**
     * Method will only be called if the callback hasn't been canceled
     * displayView will be null if DisplayCallback was constructed using the single parameter constructor.
     */
    abstract public void displayResult(Context context, View displayView, T result);

    @Override
    public void onResult(T result) {
        View progressBar = progressBarReference.get();
        View displayView = displayViewReference != null ? displayViewReference.get() : null;
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
            displayResult(progressBar.getContext(), displayView, result);
        }
    }

    @Override
    public void onError(Exception e) {
        View progressBar = progressBarReference.get();
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
            ViewUtils.alert(progressBar.getContext(), e.getMessage());
        }
    }

    public void onDetach() {
        if (progressBarReference != null) {
            progressBarReference.clear();
        }
        if (displayViewReference != null) {
            displayViewReference.clear();
        }
    }
}