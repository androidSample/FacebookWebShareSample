package com.hthan.facebookWeb;

import android.os.Bundle;


public interface DialogListener {
	 /**
     * Called when a dialog completes.
     *
     * Executed by the thread that initiated the dialog.
     *
     * @param values
     *            Key-value string pairs extracted from the response.
     */
    public void onComplete(Bundle values);

    /**
     * Called when a Facebook responds to a dialog with an error.
     *
     * Executed by the thread that initiated the dialog.
     *
     */
    public void onFacebookError(FacebookError e);

    /**
     * Called when a dialog has an error.
     *
     * Executed by the thread that initiated the dialog.
     *
     */
    public void onError(DialogError e);

    /**
     * Called when a dialog is canceled by the user.
     *
     * Executed by the thread that initiated the dialog.
     *
     */
    public void onCancel();

}
