package ca.josephroque.bowlingcompanion.fragment;


import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

import ca.josephroque.bowlingcompanion.MainActivity;
import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;
import ca.josephroque.bowlingcompanion.theme.Theme;
import ca.josephroque.bowlingcompanion.utilities.DisplayUtils;
import ca.josephroque.bowlingcompanion.utilities.FileUtils;
import ca.josephroque.bowlingcompanion.utilities.NavigationController;
import ca.josephroque.bowlingcompanion.utilities.TransferUtils;

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

    /** Represents the number of failed imports. */
    private static final String IMPORT_FAILURES = "im_fail";
    /** Represents the number of failed exports. */
    private static final String EXPORT_FAILURES = "ex_fail";
    /** Represents the last transfer key the user received from the server. */
    private static final String LAST_KEY_RECEIVED = "last_key";

    /** Represents the import cardview. */
    private static final byte VIEW_IMPORT = 0;
    /** Represents the export cardview. */
    private static final byte VIEW_EXPORT = 1;
    /** Represents the restore/delete cardview. */
    private static final byte VIEW_RESTORE_DELETE = 2;

    /** The current {@link android.os.AsyncTask} being executed, so it can be cancelled if necessary. */
    private AsyncTask<Boolean, Integer, String> mCurrentTransferTask = null;

    /** LinearLayout that contains the views of the Fragment. */
    private LinearLayout mLinearLayoutRoot = null;
    /** Reference to the last view which was added to the layout. */
    private CardView mLastViewAdded = null;

    /** Indicates if the fragment is currently showing the import, export, or restore/delete options. */
    private byte mCurrentCardView = -1;
    /** Number of times importing data failed. */
    private int mImportFailures = 0;
    /** Number of times exporting data failed. */
    private int mExportFailures = 0;

    /** Last key that the user received from the server after a successful upload. */
    private String mLastKeyReceived = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ScrollView rootView = (ScrollView) inflater.inflate(R.layout.fragment_transfer, container, false);

        View.OnClickListener cardClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentTransferTask == null) {
                    if (v.getId() == R.id.btn_import)
                        showCardView(VIEW_IMPORT);
                    else if (v.getId() == R.id.btn_export)
                        showCardView(VIEW_EXPORT);
                    else if (v.getId() == R.id.btn_restore_delete)
                        showCardView(VIEW_RESTORE_DELETE);

                    rootView.post(new Runnable() {
                        @Override
                        public void run() {
                            rootView.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                }
            }
        };
        rootView.findViewById(R.id.btn_import).setOnClickListener(cardClickListener);
        rootView.findViewById(R.id.btn_export).setOnClickListener(cardClickListener);
        rootView.findViewById(R.id.btn_restore_delete).setOnClickListener(cardClickListener);

        mLinearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.ll_transfer);

        if (savedInstanceState != null) {
            mImportFailures = savedInstanceState.getInt(IMPORT_FAILURES, 0);
            mExportFailures = savedInstanceState.getInt(EXPORT_FAILURES, 0);
            mLastKeyReceived = savedInstanceState.getString(LAST_KEY_RECEIVED, null);
        }

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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(IMPORT_FAILURES, mImportFailures);
        outState.putInt(EXPORT_FAILURES, mExportFailures);
        outState.putString(LAST_KEY_RECEIVED, mLastKeyReceived);
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
     * @param cardView id of the card view to show.
     */
    private void showCardView(byte cardView) {
        if (cardView != mCurrentCardView)
            hideCurrentCard();
        else
            return;
        mCurrentCardView = cardView;

        if (mCurrentCardView == VIEW_IMPORT) {
            mLastViewAdded = (CardView) LayoutInflater.from(getContext())
                    .inflate(R.layout.cardview_transfer_import, mLinearLayoutRoot, false);
            setupImportInteractions(mLastViewAdded);
        } else if (mCurrentCardView == VIEW_EXPORT) {
            mLastViewAdded = (CardView) LayoutInflater.from(getContext())
                    .inflate(R.layout.cardview_transfer_export, mLinearLayoutRoot, false);
            setupExportInteractions(mLastViewAdded);
        } else {
            mLastViewAdded = (CardView) LayoutInflater.from(getContext())
                    .inflate(R.layout.cardview_transfer_restore_delete, mLinearLayoutRoot, false);
            setupRestoreDeleteInteractions(mLastViewAdded);
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
    private void setupExportInteractions(final View rootView) {
        Button cancelButton = (Button) rootView.findViewById(R.id.btn_cancel);
        cancelButton.getBackground()
                .setColorFilter(DisplayUtils.getColorResource(getResources(), R.color.theme_red_tertiary),
                        PorterDuff.Mode.MULTIPLY);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentTransferTask != null) {
                    // Display text that task is being cancelled
                    if (mLastViewAdded != null) {
                        TextView textView = (TextView) mLastViewAdded.findViewById(R.id.tv_export_progress);
                        if (textView != null)
                            textView.setText(R.string.text_cancelling);
                    }

                    mCurrentTransferTask.cancel(true);
                }
            }
        });

        Button exportButton = (Button) rootView.findViewById(R.id.btn_begin_export);
        exportButton.getBackground().setColorFilter(Theme.getPrimaryThemeColor(), PorterDuff.Mode.MULTIPLY);
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLastKeyReceived == null) {
                    if (mCurrentTransferTask == null) {
                        mCurrentTransferTask = new DataExportTask(TransferFragment.this);
                        mCurrentTransferTask.execute(mExportFailures > 0);
                    }
                } else {
                    TextView textView = (TextView) rootView.findViewById(R.id.tv_transfer_export_result);
                    textView.setText(String.format(getResources().getString(R.string.text_transfer_exported_already),
                            mLastKeyReceived));
                    textView.setTextColor(DisplayUtils.getColorResource(getResources(), R.color.transfer_error));
                    textView.setVisibility(View.VISIBLE);
                }
            }
        });

        ((ProgressBar) rootView.findViewById(R.id.pb_export)).getProgressDrawable()
                .setColorFilter(Theme.getPrimaryThemeColor(), PorterDuff.Mode.SRC_IN);
    }

    /**
     * Sets listeners and colors for views in the import CardView.
     *
     * @param rootView root CardView.
     */
    private void setupImportInteractions(final View rootView) {
        Button cancelButton = (Button) rootView.findViewById(R.id.btn_cancel);
        cancelButton.getBackground()
                .setColorFilter(DisplayUtils.getColorResource(getResources(), R.color.theme_red_tertiary),
                        PorterDuff.Mode.MULTIPLY);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentTransferTask != null) {
                    // Display text that task is being cancelled
                    if (mLastViewAdded != null) {
                        TextView textView = (TextView) mLastViewAdded.findViewById(R.id.tv_import_progress);
                        if (textView != null)
                            textView.setText(R.string.text_cancelling);
                    }

                    mCurrentTransferTask.cancel(true);
                }
            }
        });

        final Button importButton = (Button) rootView.findViewById(R.id.btn_begin_import);
        importButton.setEnabled(false);
        importButton.getBackground().setColorFilter(Theme.getPrimaryThemeColor(), PorterDuff.Mode.MULTIPLY);
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentTransferTask == null) {
                    String transferKey = ((EditText) rootView.findViewById(R.id.et_transfer_code)).getText().toString();
                    if (!TextUtils.isEmpty(transferKey) && transferKey.length() == TransferUtils.TRANSFER_KEY_LENGTH) {
                        mCurrentTransferTask = new DataImportTask(TransferFragment.this, transferKey);
                        mCurrentTransferTask.execute(mImportFailures > 0);
                    } else {
                        TextView textView = (TextView) rootView.findViewById(R.id.tv_transfer_import_result);
                        textView.setText(String.format(getResources().getString(R.string.text_transfer_invalid_key),
                                TransferUtils.TRANSFER_KEY_LENGTH));
                        textView.setTextColor(DisplayUtils.getColorResource(getResources(), R.color.transfer_error));
                        textView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        ((EditText) rootView.findViewById(R.id.et_transfer_code)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Unused
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Unused
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == TransferUtils.TRANSFER_KEY_LENGTH) {
                    importButton.setEnabled(true);
                } else if (importButton.isEnabled()) {
                    importButton.setEnabled(false);
                }
            }
        });

        ((ProgressBar) rootView.findViewById(R.id.pb_import)).getProgressDrawable()
                .setColorFilter(Theme.getPrimaryThemeColor(), PorterDuff.Mode.SRC_IN);
    }

    /**
     * Sets listeners and colors for views in the restore/delete CardView.
     *
     * @param rootView root CardView.
     */
    private void setupRestoreDeleteInteractions(final View rootView) {
        rootView.findViewById(R.id.btn_restore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setMessage(R.string.text_restore_backup_prompt)
                        .setPositiveButton(R.string.text_restore, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new ReplaceBowlerDataTask(TransferFragment.this).execute(false);
                            }
                        })
                        .setNegativeButton(R.string.text_cancel, null)
                        .create()
                        .show();
            }
        });

        rootView.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setMessage(R.string.text_delete_backup_prompt)
                        .setPositiveButton(R.string.dialog_delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new DeleteBowlerDataTask(TransferFragment.this).execute(false);
                            }
                        })
                        .setNegativeButton(R.string.text_cancel, null)
                        .create()
                        .show();
            }
        });
    }

    /**
     * Displays a prompt asking the user if they want to override their current data. If they answer yes, the process of
     * replacing the current data with either the backup data or downloaded data begins.
     *
     * @param replaceWithDownloaded {@code true} to replace the default data with newly downloaded data, or {@code
     * false} to replace downloaded data with the original data
     * @param deleteDownloaded {@code true} to delete the downloaded data when the overwrite is finished
     */
    private void promptUserToOverride(final boolean replaceWithDownloaded, final boolean deleteDownloaded) {
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    new ReplaceBowlerDataTask(TransferFragment.this).execute(replaceWithDownloaded, deleteDownloaded);
                } else if (deleteDownloaded && replaceWithDownloaded) {
                    new DeleteBowlerDataTask(TransferFragment.this).execute(true);
                }

                dialog.dismiss();
            }
        };

        new AlertDialog.Builder(getContext())
                .setTitle(R.string.text_overwrite_data_title)
                .setMessage(R.string.text_overwrite_data_message)
                .setPositiveButton(R.string.text_overwrite, onClickListener)
                .setNegativeButton(R.string.text_cancel, onClickListener)
                .create()
                .show();
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
     * Deletes either the user's backup or downloaded bowler data.
     */
    private static final class DeleteBowlerDataTask
            extends AsyncTask<Boolean, Void, Void> {

        /** Weak reference to the parent fragment. */
        private final WeakReference<TransferFragment> mFragment;

        /**
         * Assigns a weak reference to the parent fragment.
         *
         * @param fragment parent fragment
         */
        private DeleteBowlerDataTask(TransferFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @SuppressWarnings("ResultOfMethodCallIgnored")
        @Override
        protected Void doInBackground(Boolean... deleteDownloaded) {
            TransferFragment fragment = mFragment.get();
            if (fragment == null || fragment.getContext() == null)
                return null;

            String modifier = (deleteDownloaded[0])
                    ? TransferUtils.DATA_DOWNLOADED
                    : TransferUtils.DATA_BACKUP;
            File originalDatabase = fragment.getContext().getDatabasePath(DatabaseHelper.DATABASE_NAME);
            File databaseToDelete = new File(originalDatabase.getAbsolutePath() + modifier);
            databaseToDelete.delete();

            return null;
        }
    }

    /**
     * Replaces the user's current data with either downloaded data or a backup of old data.
     */
    private static final class ReplaceBowlerDataTask
            extends AsyncTask<Boolean, Void, Integer> {

        /** Weak reference to the parent fragment. */
        private final WeakReference<TransferFragment> mFragment;

        /**
         * Assigns a weak reference to the parent fragment.
         *
         * @param fragment parent fragment
         */
        private ReplaceBowlerDataTask(TransferFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        protected void onPreExecute() {
            TransferFragment fragment = mFragment.get();
            if (fragment == null)
                return;

            NavigationController navigationController = (NavigationController) fragment.getActivity();
            if (navigationController == null)
                return;

            // Disable database and app navigation
            navigationController.setNavigationEnabled(false);
            DatabaseHelper.closeInstance();
        }

        @Override
        protected Integer doInBackground(Boolean... options) {
            final TransferFragment fragment = mFragment.get();
            if (fragment == null || fragment.getContext() == null)
                return 0;

            boolean replaceWithDownloaded = options[0];
            boolean deleteDownloaded = false;
            if (options.length >= 2)
                deleteDownloaded = options[1];

            File originalDatabase = fragment.getContext().getDatabasePath(DatabaseHelper.DATABASE_NAME);
            File replacementDatabase;

            if (replaceWithDownloaded) {
                replacementDatabase = new File(originalDatabase.getAbsolutePath() + TransferUtils.DATA_DOWNLOADED);

                if (!FileUtils.copyFile(originalDatabase,
                        originalDatabase.getAbsolutePath() + TransferUtils.DATA_BACKUP)) {
                    return R.string.text_backup_failure;
                }
            } else {
                replacementDatabase = new File(originalDatabase.getAbsolutePath() + TransferUtils.DATA_BACKUP);
            }

            if (!replacementDatabase.exists()) {
                return R.string.text_transfer_nonexist;
            }

            if (!FileUtils.copyFile(replacementDatabase, originalDatabase.getAbsolutePath())) {
                return R.string.text_transfer_overwrite_failed;
            }

            if (replaceWithDownloaded && deleteDownloaded) {
                // Delete the downloaded data if it was successfully copied over
                fragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new DeleteBowlerDataTask(fragment).execute(true);
                    }
                });
            }

            return R.string.text_transfer_successful;
        }

        @Override
        protected void onPostExecute(Integer result) {
            TransferFragment fragment = mFragment.get();
            if (fragment == null)
                return;

            NavigationController navigationController = (NavigationController) fragment.getActivity();
            if (navigationController == null)
                return;
            navigationController.setNavigationEnabled(true);

            new AlertDialog.Builder(fragment.getContext())
                    .setMessage(result)
                    .setPositiveButton(R.string.dialog_okay, null)
                    .create()
                    .show();
        }
    }

    /**
     * Imports the user's data from a remote server.
     */
    private static final class DataImportTask
            extends AsyncTask<Boolean, Integer, String> {

        /** Weak reference to the parent fragment. */
        private final WeakReference<TransferFragment> mFragment;
        /** Weak reference to the progress bar for the task. */
        private final WeakReference<ProgressBar> mProgressBar;
        /** Weak reference to the text view to display results of the text. */
        private final WeakReference<TextView> mTextViewProgress;

        /** Key which represents data on the server. */
        private final String mTransferKey;

        /**
         * Assigns a weak reference to the parent fragment.
         *
         * @param fragment parent fragment
         * @param key unique key for requesting data from server
         */
        private DataImportTask(TransferFragment fragment, String key) {
            mFragment = new WeakReference<>(fragment);
            mProgressBar = new WeakReference<>((ProgressBar) fragment.mLastViewAdded.findViewById(R.id.pb_import));
            mTextViewProgress = new WeakReference<>(
                    (TextView) fragment.mLastViewAdded.findViewById(R.id.tv_import_progress));
            mTransferKey = key;
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
            ProgressBar progressBar = mProgressBar.get();
            if (progressBar != null)
                progressBar.setProgress(-1);

            // Displaying progress text
            TextView textView = mTextViewProgress.get();
            if (textView != null)
                textView.setText(R.string.text_contacting_server);

            // Display appropriate views
            fragment.mLastViewAdded.findViewById(R.id.et_transfer_code).setVisibility(View.GONE);
            fragment.mLastViewAdded.findViewById(R.id.tv_transfer_import_result).setVisibility(View.GONE);
            fragment.mLastViewAdded.findViewById(R.id.ll_transfer_progress).setVisibility(View.VISIBLE);
        }

        @SuppressWarnings("CheckStyle") // Ignore length of method
        @Override
        protected String doInBackground(Boolean... retry) {
            TransferFragment fragment = mFragment.get();
            if (fragment == null || fragment.getContext() == null)
                return TransferUtils.ERROR_EXCEPTION;

            if (!TransferUtils.isConnectionAvailable(fragment.getContext())) {
                return TransferUtils.ERROR_NO_INTERNET;
            } else if (!TransferUtils.getServerStatus()) {
                return TransferUtils.ERROR_UNAVAILABLE;
            } else if (!TransferUtils.isKeyValid(mTransferKey)) {
                return TransferUtils.ERROR_INVALID_KEY;
            }

            File dbFile = fragment.getContext().getDatabasePath(DatabaseHelper.DATABASE_NAME);
            String dbFilePath = dbFile.getAbsolutePath() + TransferUtils.DATA_DOWNLOADED;
            dbFile = new File(dbFilePath);

            final int timeout = (retry[0])
                    ? TransferUtils.CONNECTION_TIMEOUT
                    : TransferUtils.CONNECTION_EXTENDED_TIMEOUT;

            HttpURLConnection connection;

            try {
                URL url = new URL(TransferUtils.getDownloadEndpoint(mTransferKey));

                // Preparing connection for upload
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(timeout);
                connection.setConnectTimeout(timeout);

                // Get the expected size of the file
                int contentLength = connection.getContentLength();

                InputStream inputStream = url.openStream();
                FileOutputStream outputStream = new FileOutputStream(dbFile);

                byte[] data = new byte[TransferUtils.MAX_BUFFER_SIZE];
                int totalDataRead = 0;
                int lastProgressPercentage = 0;

                try {
                    int dataRead = inputStream.read(data);
                    while (dataRead != -1 && !isCancelled()) {
                        totalDataRead += dataRead;

                        // Update the progress bar
                        int currentProgressPercentage = (int) ((totalDataRead) / (float) contentLength
                                * TransferUtils.TARGET_PERCENTAGE);
                        if (currentProgressPercentage > lastProgressPercentage) {
                            lastProgressPercentage = currentProgressPercentage;
                            publishProgress(currentProgressPercentage);
                        }

                        outputStream.write(data, 0, dataRead);
                        dataRead = inputStream.read(data);
                    }
                } catch (Exception ex) {
                    Log.e(TAG, "Error receiving file.", ex);
                    return TransferUtils.ERROR_EXCEPTION;
                } finally {
                    try {
                        outputStream.close();
                        if (inputStream != null) {
                            inputStream.close();
                        }
                    } catch (IOException ex) {
                        Log.e(TAG, "Error closing streams.", ex);
                    }
                }

                // Update progress bar
                publishProgress(TransferUtils.TARGET_PERCENTAGE);
            } catch (MalformedURLException ex) {
                Log.e(TAG, "Malformed url. I have no idea how this happened.", ex);
                return TransferUtils.ERROR_MALFORMED_URL;
            } catch (SocketTimeoutException ex) {
                Log.e(TAG, "Timed out reading response.", ex);
                return TransferUtils.ERROR_TIMEOUT;
            } catch (IOException ex) {
                Log.e(TAG, "Couldn't open or maintain connection.", ex);
                return TransferUtils.ERROR_IO_EXCEPTION;
            } catch (Exception ex) {
                Log.e(TAG, "Unknown exception.", ex);
                return TransferUtils.ERROR_EXCEPTION;
            }

            return TransferUtils.SUCCESSFUL_IMPORT;
        }

        @Override
        public void onProgressUpdate(Integer... progress) {
            if (progress[0] < 0) {
                TextView textView = mTextViewProgress.get();
                if (textView != null)
                    textView.setText(R.string.text_downloading);
                ProgressBar progressBar = mProgressBar.get();
                if (progressBar != null)
                    progressBar.setProgress(0);
            } else if (progress[0] == 0) {
                TextView textView = mTextViewProgress.get();
                if (textView != null)
                    textView.setText(R.string.text_cancelling);
            } else if (progress[0] >= TransferUtils.TARGET_PERCENTAGE) {
                ProgressBar progressBar = mProgressBar.get();
                if (progressBar != null)
                    progressBar.setProgress(TransferUtils.TARGET_PERCENTAGE);
                TextView textView = mTextViewProgress.get();
                if (textView != null)
                    textView.setText(R.string.text_processing);
            } else {
                // Updating progress bar
                ProgressBar progressBar = mProgressBar.get();
                if (progressBar != null)
                    progressBar.setProgress(progress[0]);
            }
        }

        @Override
        protected void onCancelled() {
            mProgressBar.clear();

            TransferFragment fragment = mFragment.get();
            if (fragment == null || fragment.getContext() == null)
                return;

            cleanupTask(fragment);
            displayResult(fragment, TransferUtils.ERROR_CANCELLED);
            fragment.mImportFailures++;
        }

        @Override
        protected void onPostExecute(String result) {
            mProgressBar.clear();

            TransferFragment fragment = mFragment.get();
            if (fragment == null || fragment.getContext() == null)
                return;

            cleanupTask(fragment);
            displayResult(fragment, result);

            if (result.equals(TransferUtils.SUCCESSFUL_IMPORT)) {
                fragment.mImportFailures = 0;
                fragment.promptUserToOverride(true, true);
            } else {
                fragment.mImportFailures++;
            }
        }

        /**
         * Returns the fragment to the state it was in before the task was started.
         *
         * @param fragment fragment to clean up.
         */
        private void cleanupTask(TransferFragment fragment) {
            // Disabling cancel button
            View view = fragment.mLastViewAdded.findViewById(R.id.btn_cancel);
            view.setEnabled(false);
            view.setVisibility(View.GONE);

            // Hiding progress bar
            view = fragment.mLastViewAdded.findViewById(R.id.ll_transfer_progress);
            view.setVisibility(View.GONE);

            fragment.mLastViewAdded.findViewById(R.id.et_transfer_code).setVisibility(View.VISIBLE);

            fragment.mCurrentTransferTask = null;
        }

        /**
         * Shows a {@code TextView} with the result of the upload.
         *
         * @param fragment fragment with text view to display result
         * @param result string indicating the result of the upload
         */
        private void displayResult(TransferFragment fragment, String result) {
            TextView textViewResult = (TextView) fragment.mLastViewAdded.findViewById(R.id.tv_transfer_import_result);
            int textColor = DisplayUtils.getColorResource(fragment.getResources(), R.color.transfer_error);
            boolean showTextView = true;

            switch (result) {
                case TransferUtils.ERROR_CANCELLED:
                    textViewResult.setText(R.string.text_transfer_cancelled);
                    break;
                case TransferUtils.ERROR_IO_EXCEPTION:
                case TransferUtils.ERROR_EXCEPTION:
                case TransferUtils.ERROR_MALFORMED_URL:
                    textViewResult.setText(R.string.text_transfer_unknown_error);
                    break;
                case TransferUtils.ERROR_FILE_NOT_FOUND:
                    textViewResult.setText(R.string.text_transfer_file_not_found);
                    break;
                case TransferUtils.ERROR_OUT_OF_MEMORY:
                    textViewResult.setText(R.string.text_transfer_oom);
                    break;
                case TransferUtils.ERROR_TIMEOUT:
                    textViewResult.setText(R.string.text_transfer_try_again_later);
                    break;
                case TransferUtils.ERROR_UNAVAILABLE:
                    textViewResult.setText(R.string.text_transfer_unavailable);
                    break;
                case TransferUtils.ERROR_INVALID_KEY:
                    textViewResult.setText(String.format(fragment.getResources()
                            .getString(R.string.text_transfer_invalid_key), TransferUtils.TRANSFER_KEY_LENGTH));
                    break;
                case TransferUtils.ERROR_NO_INTERNET:
                    textViewResult.setText(R.string.text_transfer_no_internet);
                    break;
                default:
                    showTextView = false;
                    break;
            }

            if (showTextView) {
                textViewResult.setTextColor(textColor);
                textViewResult.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Exports the user's data to a remote server.
     */
    private static final class DataExportTask
            extends AsyncTask<Boolean, Integer, String> {

        /** Weak reference to the parent fragment. */
        private final WeakReference<TransferFragment> mFragment;
        /** Weak reference to the progress bar for the task. */
        private final WeakReference<ProgressBar> mProgressBar;
        /** Weak reference to the text view to display results of the text. */
        private final WeakReference<TextView> mTextViewProgress;

        /**
         * Assigns a weak reference to the parent fragment.
         *
         * @param fragment parent fragment
         */
        private DataExportTask(TransferFragment fragment) {
            mFragment = new WeakReference<>(fragment);
            mProgressBar = new WeakReference<>((ProgressBar) fragment.mLastViewAdded.findViewById(R.id.pb_export));
            mTextViewProgress = new WeakReference<>(
                    (TextView) fragment.mLastViewAdded.findViewById(R.id.tv_export_progress));
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
            ProgressBar progressBar = mProgressBar.get();
            if (progressBar != null)
                progressBar.setProgress(-1);

            // Displaying progress text
            TextView textView = mTextViewProgress.get();
            if (textView != null)
                textView.setText(R.string.text_contacting_server);

            // Display appropriate views
            fragment.mLastViewAdded.findViewById(R.id.tv_transfer_export_result).setVisibility(View.GONE);
            fragment.mLastViewAdded.findViewById(R.id.ll_transfer_progress).setVisibility(View.VISIBLE);
        }

        @SuppressWarnings("CheckStyle") // Ignore length of method
        @Override
        protected String doInBackground(Boolean... retry) {
            TransferFragment fragment = mFragment.get();
            if (fragment == null || fragment.getContext() == null)
                return TransferUtils.ERROR_EXCEPTION;

            if (!TransferUtils.isConnectionAvailable(fragment.getContext())) {
                return TransferUtils.ERROR_NO_INTERNET;
            } else if (!TransferUtils.getServerStatus()) {
                return TransferUtils.ERROR_UNAVAILABLE;
            }

            // Most of this method retrieved from this StackOverflow question.
            // http://stackoverflow.com/a/7645328/4896787

            // Preparing the database
            File dbFile = fragment.getContext().getDatabasePath(DatabaseHelper.DATABASE_NAME);
            String dbFilePath = dbFile.getAbsolutePath();
            String dbFileName = dbFile.getName();

            HttpURLConnection connection;
            FileInputStream fileInputStream = null;
            DataOutputStream outputStream = null;
            BufferedReader reader = null;

            final int timeout = (retry[0])
                    ? TransferUtils.CONNECTION_TIMEOUT
                    : TransferUtils.CONNECTION_EXTENDED_TIMEOUT;
            final String lineEnd = "\r\n";
            final String twoHyphens = "--";
            final String boundary = "*****";
            final String transferApiKey = fragment.getResources().getString(R.string.transfer_api_key);
            int bytesRead;
            int bytesAvailable;
            int bufferSize;
            byte[] buffer;

            try {
                fileInputStream = new FileInputStream(dbFile);
                URL url = new URL(TransferUtils.getUploadEndpoint());

                // Preparing connection for upload
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setConnectTimeout(timeout);
                connection.setReadTimeout(timeout);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("Authorization", transferApiKey);

                outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);

                outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
                        + dbFileName + "\"" + lineEnd);
                outputStream.writeBytes(lineEnd);

                final int totalBytes = fileInputStream.available();
                int lastProgressPercentage = 0;
                bytesAvailable = totalBytes;
                bufferSize = Math.min(bytesAvailable, TransferUtils.MAX_BUFFER_SIZE);
                buffer = new byte[bufferSize];

                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                try {
                    while (bytesRead > 0 && !isCancelled()) {
                        try {
                            outputStream.write(buffer, 0, bufferSize);
                        } catch (OutOfMemoryError ex) {
                            Log.e(TAG, "Out of memory sending file.", ex);
                            return TransferUtils.ERROR_OUT_OF_MEMORY;
                        }

                        // Update the progress bar
                        int currentProgressPercentage = (int) ((-bytesAvailable + totalBytes) / (float) totalBytes
                                * TransferUtils.TARGET_PERCENTAGE);
                        if (currentProgressPercentage > lastProgressPercentage) {
                            lastProgressPercentage = currentProgressPercentage;
                            publishProgress(currentProgressPercentage);
                        }

                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, TransferUtils.MAX_BUFFER_SIZE);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }
                } catch (Exception ex) {
                    Log.e(TAG, "Error sending file.", ex);
                    return TransferUtils.ERROR_EXCEPTION;
                }

                if (isCancelled()) {
                    publishProgress(0);
                    return TransferUtils.ERROR_CANCELLED;
                }

                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                publishProgress(TransferUtils.TARGET_PERCENTAGE);

                // Get info about the status of the server
                int serverResponseCode = connection.getResponseCode();

                StringBuilder responseBuilder = new StringBuilder();
                if (serverResponseCode == HttpURLConnection.HTTP_OK) {
                    try {
                        reader = new BufferedReader(
                                new InputStreamReader(new DataInputStream(connection.getInputStream())));
                        String line = reader.readLine();
                        while (line != null && !isCancelled()) {
                            responseBuilder.append(line);
                            line = reader.readLine();
                        }
                    } catch (IOException ex) {
                        Log.e(TAG, "Error reading server response.", ex);
                        return TransferUtils.ERROR_IO_EXCEPTION;
                    } finally {
                        try {
                            if (reader != null)
                                reader.close();
                        } catch (IOException ex) {
                            Log.e(TAG, "Error closing stream.", ex);
                        }
                    }
                }

                if (isCancelled())
                    return TransferUtils.ERROR_CANCELLED;
                else
                    return responseBuilder.toString();
            } catch (FileNotFoundException ex) {
                Log.e(TAG, "Unable to find database file.", ex);
                return TransferUtils.ERROR_FILE_NOT_FOUND;
            } catch (MalformedURLException ex) {
                Log.e(TAG, "Malformed url. I have no idea how this happened.", ex);
                return TransferUtils.ERROR_MALFORMED_URL;
            } catch (SocketTimeoutException ex) {
                Log.e(TAG, "Timed out reading response.", ex);
                return TransferUtils.ERROR_TIMEOUT;
            } catch (IOException ex) {
                Log.e(TAG, "Couldn't open or maintain connection.", ex);
                return TransferUtils.ERROR_IO_EXCEPTION;
            } catch (Exception ex) {
                Log.e(TAG, "Unknown exception.", ex);
                return TransferUtils.ERROR_EXCEPTION;
            } finally {
                try {
                    if (fileInputStream != null)
                        fileInputStream.close();
                    if (outputStream != null) {
                        outputStream.flush();
                        outputStream.close();
                    }
                } catch (IOException ex) {
                    Log.e(TAG, "Error closing streams.", ex);
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            if (progress[0] < 0) {
                TextView textView = mTextViewProgress.get();
                if (textView != null)
                    textView.setText(R.string.text_uploading);
                ProgressBar progressBar = mProgressBar.get();
                if (progressBar != null)
                    progressBar.setProgress(0);
            } else if (progress[0] == 0) {
                TextView textView = mTextViewProgress.get();
                if (textView != null)
                    textView.setText(R.string.text_cancelling);
            } else if (progress[0] >= TransferUtils.TARGET_PERCENTAGE) {
                ProgressBar progressBar = mProgressBar.get();
                if (progressBar != null)
                    progressBar.setProgress(TransferUtils.TARGET_PERCENTAGE);
                TextView textView = mTextViewProgress.get();
                if (textView != null)
                    textView.setText(R.string.text_processing);
            } else {
                // Updating progress bar
                ProgressBar progressBar = mProgressBar.get();
                if (progressBar != null)
                    progressBar.setProgress(progress[0]);
            }
        }

        @Override
        protected void onCancelled() {
            mProgressBar.clear();

            TransferFragment fragment = mFragment.get();
            if (fragment == null || fragment.getContext() == null)
                return;

            cleanupTask(fragment);
            displayResult(fragment, TransferUtils.ERROR_CANCELLED);
            fragment.mExportFailures++;
        }

        @Override
        protected void onPostExecute(String result) {
            mProgressBar.clear();

            TransferFragment fragment = mFragment.get();
            if (fragment == null || fragment.getContext() == null)
                return;

            cleanupTask(fragment);
            displayResult(fragment, result);

            if (result.contains("requestId")) {
                fragment.mExportFailures = 0;

            } else {
                fragment.mExportFailures++;
            }
        }

        /**
         * Returns the fragment to the state it was in before the task was started.
         *
         * @param fragment fragment to clean up.
         */
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

        /**
         * Shows a {@code TextView} with the result of the upload.
         *
         * @param fragment fragment with text view to display result
         * @param result string indicating the result of the upload
         */
        private void displayResult(TransferFragment fragment, String result) {
            TextView textViewResult = (TextView) fragment.mLastViewAdded.findViewById(R.id.tv_transfer_export_result);
            int textColor = DisplayUtils.getColorResource(fragment.getResources(), R.color.transfer_error);

            switch (result) {
                case TransferUtils.ERROR_CANCELLED:
                    textViewResult.setText(R.string.text_transfer_cancelled);
                    break;
                case TransferUtils.ERROR_IO_EXCEPTION:
                case TransferUtils.ERROR_EXCEPTION:
                case TransferUtils.ERROR_MALFORMED_URL:
                    textViewResult.setText(R.string.text_transfer_unknown_error);
                    break;
                case TransferUtils.ERROR_FILE_NOT_FOUND:
                    textViewResult.setText(R.string.text_transfer_file_not_found);
                    break;
                case TransferUtils.ERROR_OUT_OF_MEMORY:
                    textViewResult.setText(R.string.text_transfer_oom);
                    break;
                case TransferUtils.ERROR_TIMEOUT:
                    textViewResult.setText(R.string.text_transfer_try_again_later);
                    break;
                case TransferUtils.ERROR_UNAVAILABLE:
                    textViewResult.setText(R.string.text_transfer_unavailable);
                    break;
                case TransferUtils.ERROR_NO_INTERNET:
                    textViewResult.setText(R.string.text_transfer_no_internet);
                    break;
                default:
                    int requestIdIndex = result.indexOf("requestId");
                    if (requestIdIndex >= 0) {
                        int start = requestIdIndex + TransferUtils.TRANSFER_KEY_START;
                        String key = result.substring(start, start + TransferUtils.TRANSFER_KEY_LENGTH);
                        fragment.mLastKeyReceived = key;
                        textColor = DisplayUtils.getColorResource(fragment.getResources(), android.R.color.black);
                        textViewResult.setText(String.format(fragment.getResources()
                                .getString(R.string.text_transfer_upload_complete), key));
                    } else {
                        textViewResult.setText(R.string.text_transfer_unknown_error);
                    }
                    break;
            }

            textViewResult.setTextColor(textColor);
            textViewResult.setVisibility(View.VISIBLE);
        }
    }
}
