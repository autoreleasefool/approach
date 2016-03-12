package ca.josephroque.bowlingcompanion.fragment;


import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.lang.ref.WeakReference;

import ca.josephroque.bowlingcompanion.MainActivity;
import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.theme.Theme;
import ca.josephroque.bowlingcompanion.utilities.DisplayUtils;

/**
 * A {@link android.support.v4.app.Fragment} for users to upload or download data from a remote server to back up their
 * data.
 */
public final class TransferFragment
        extends Fragment
        implements Theme.ChangeableTheme {

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "TransferFragment";

    /** The current {@link android.os.AsyncTask} being executed, so it can be cancelled if necessary. */
    private AsyncTask<Void, Integer, Void> mCurrentTransferTask = null;

    /** LinearLayout that contains the views of the Fragment. */
    private LinearLayout mLinearLayoutRoot = null;
    /** Reference to the last view which was added to the layout. */
    private CardView mLastViewAdded = null;

    /** Indicates if the fragment is currently showing the import or export options. */
    private boolean mShowingImport = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_transfer, container, false);

        View.OnClickListener importExportClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentTransferTask == null) {
                    showImportOrExport(v.getId() == R.id.btn_import);
                }
            }
        };
        rootView.findViewById(R.id.btn_import).setOnClickListener(importExportClickListener);
        rootView.findViewById(R.id.btn_export).setOnClickListener(importExportClickListener);

        mLinearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.ll_transfer);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getActivity() != null) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.setActionBarTitle(R.string.title_fragment_transfer, true);
            mainActivity.setDrawerState(false);
            mainActivity.setFloatingActionButtonState(0, 0);
        }
    }

    @Override
    public void updateTheme() {

    }

    /**
     * Removes {@code mLastViewAdded} from {@code mLinearLayoutRoot} if the view is not null.
     */
    private void hideCurrentCard() {
        if (mLastViewAdded != null) {
            // The other button has been pressed so the current view needs to be removed.
            mLinearLayoutRoot.removeView(mLastViewAdded);
            mLastViewAdded = null;
        }
    }

    /**
     * Animates a container to allow a user to import or export their data.
     *
     * @param showImport if {@code true}, provides a view for the user to import data. If {@code false}, provides a view
     * for the user to export data.
     */
    private void showImportOrExport(boolean showImport) {
        if (showImport != mShowingImport)
            hideCurrentCard();
        mShowingImport = showImport;

        if (showImport) {
            mLastViewAdded = (CardView) LayoutInflater.from(getContext())
                    .inflate(R.layout.cardview_transfer_import, mLinearLayoutRoot, false);
            setupImportInteractions(mLastViewAdded);
        } else {
            mLastViewAdded = (CardView) LayoutInflater.from(getContext())
                    .inflate(R.layout.cardview_transfer_export, mLinearLayoutRoot, false);
            setupExportInteractions(mLastViewAdded);
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mLinearLayoutRoot.addView(mLastViewAdded, params);
    }

    /**
     * Sets listeners and colors for views in the export CardView.
     *
     * @param rootView root CardView.
     */
    private void setupExportInteractions(View rootView) {
        Button cancelButton = (Button) rootView.findViewById(R.id.btn_cancel);
        cancelButton.getBackground().setColorFilter(DisplayUtils.getColorResource(getResources(), R.color.theme_red_tertiary),
                PorterDuff.Mode.MULTIPLY);
        cancelButton .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentTransferTask != null) {
                    mCurrentTransferTask.cancel(true);
                }
            }
        });

        Button exportButton = (Button) rootView.findViewById(R.id.btn_begin_export);
        exportButton.getBackground().setColorFilter(Theme.getPrimaryThemeColor(), PorterDuff.Mode.MULTIPLY);
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentTransferTask == null) {
                    mCurrentTransferTask = new DataExportTask(TransferFragment.this);
                    mCurrentTransferTask.execute();
                }
            }
        });
    }

    /**
     * Sets listeners and colors for views in the import CardView.
     *
     * @param rootView root CardView.
     */
    private void setupImportInteractions(View rootView) {
    }

    /**
     * Creates a new instance of this fragment.
     *
     * @return the new instance
     */
    public static TransferFragment newInstance() {
        return new TransferFragment();
    }

    /**
     * Exports the user's data to a remote server.
     */
    private static final class DataExportTask
            extends AsyncTask<Void, Integer, Void> {

        /** Weak reference to the parent fragment. */
        private final WeakReference<TransferFragment> mFragment;

        /**
         * Assigns a weak reference to the parent fragment.
         *
         * @param fragment parent fragment
         */
        private DataExportTask(TransferFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        protected void onPreExecute() {
            TransferFragment fragment = mFragment.get();
            if (fragment == null || fragment.getContext() == null)
                return;

            // Setting up cancel button
            View view = fragment.mLastViewAdded.findViewById(R.id.btn_cancel);
            view.setEnabled(true);
            view.setVisibility(View.VISIBLE);

            // Displaying progress bar
            ProgressBar progressBar = (ProgressBar) fragment.mLastViewAdded.findViewById(R.id.pb_export);
            progressBar.setProgress(0);

            view = fragment.mLastViewAdded.findViewById(R.id.ll_transfer_progress);
            view.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Log.d(TAG, "Export interrupted");
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
        }

        @Override
        protected void onCancelled() {
            TransferFragment fragment = mFragment.get();
            if (fragment == null || fragment.getContext() == null)
                return;

            cleanupTask(fragment);
        }

        @Override
        protected void onPostExecute(Void result) {
            TransferFragment fragment = mFragment.get();
            if (fragment == null || fragment.getContext() == null)
                return;

            cleanupTask(fragment);
        }

        private void cleanupTask(TransferFragment fragment) {
            // Disabling cancel button
            View view = fragment.mLastViewAdded.findViewById(R.id.btn_cancel);
            view.setEnabled(false);
            view.setVisibility(View.GONE);

            // Hiding progress bar
            view = fragment.mLastViewAdded.findViewById(R.id.ll_transfer_progress);
            view.setVisibility(View.GONE);

            fragment.mCurrentTransferTask = null;
        }
    }
}
