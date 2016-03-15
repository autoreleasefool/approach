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
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ca.josephroque.bowlingcompanion.MainActivity;
import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;
import ca.josephroque.bowlingcompanion.theme.Theme;
import ca.josephroque.bowlingcompanion.utilities.DisplayUtils;
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

    /** The current {@link android.os.AsyncTask} being executed, so it can be cancelled if necessary. */
    private AsyncTask<Boolean, Integer, String> mCurrentTransferTask = null;

    /** LinearLayout that contains the views of the Fragment. */
    private LinearLayout mLinearLayoutRoot = null;
    /** Reference to the last view which was added to the layout. */
    private CardView mLastViewAdded = null;

    /** Indicates if the fragment is currently showing the import or export options. */
    private boolean mShowingImport = false;
    /** Number of times importing data failed. */
    private int mImportFailures = 0;
    /** Number of times exporting data failed. */
    private int mExportFailures = 0;

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

        if (savedInstanceState != null) {
            mImportFailures = savedInstanceState.getInt(IMPORT_FAILURES, 0);
            mExportFailures = savedInstanceState.getInt(EXPORT_FAILURES, 0);
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
                if (mCurrentTransferTask == null) {
                    mCurrentTransferTask = new DataExportTask(TransferFragment.this);
                    mCurrentTransferTask.execute(mExportFailures > 0);
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
                progressBar.setProgress(0);

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

            if (!TransferUtils.getServerStatus()) {
                return TransferUtils.ERROR_UNAVAILABLE;
            }

            // Displays text that upload has begun
            publishProgress(-1);

            // Most of this method retrieved from this StackOverflow question.
            // http://stackoverflow.com/a/7645328/4896787

            // Preparing the database
            File dbFile = fragment.getContext().getDatabasePath(DatabaseHelper.DATABASE_NAME);
            String dbFilePath = dbFile.getAbsolutePath();
            String dbFileName = dbFile.getName();
            Log.d(TAG, "Database file: " + dbFilePath);

            DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH:mm:ss", Locale.CANADA);

            HttpURLConnection connection;
            DataOutputStream outputStream;
            BufferedReader reader;

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
                FileInputStream fileInputStream = new FileInputStream(dbFile);
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

                Log.d(TAG, "Database size: " + bytesAvailable);
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
                    fileInputStream.close();
                    outputStream.flush();
                    outputStream.close();
                    return TransferUtils.ERROR_CANCELLED;
                }

                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                publishProgress(TransferUtils.TARGET_PERCENTAGE);

                // Get info about the status of the server
                int serverResponseCode = connection.getResponseCode();
                String connectionDate = null;
                Date serverTime = new Date(connection.getDate());
                try {
                    connectionDate = dateFormat.format(serverTime);
                } catch (Exception ex) {
                    Log.e(TAG, "Error parsing date.", ex);
                }
                Log.d(TAG, "Server response time: " + connectionDate);

                fileInputStream.close();
                outputStream.flush();
                outputStream.close();

                StringBuilder responseBuilder = new StringBuilder();
                if (serverResponseCode == TransferUtils.SUCCESS_RESPONSE) {
                    try {
                        reader = new BufferedReader(
                                new InputStreamReader(new DataInputStream(connection.getInputStream())));
                        String line = reader.readLine();
                        while (line != null && !isCancelled()) {
                            responseBuilder.append(line);
                            line = reader.readLine();
                        }

                        reader.close();
                    } catch (IOException ex) {
                        Log.e(TAG, "Error reading server response.", ex);
                        return TransferUtils.ERROR_IO_EXCEPTION;
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
            }
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            if (progress[0] < 0) {
                TextView textView = mTextViewProgress.get();
                if (textView != null)
                    textView.setText(R.string.text_uploading);
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

            Log.d(TAG, "Cancelled");

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
                default:
                    int requestIdIndex = result.indexOf("requestId");
                    if (requestIdIndex >= 0) {
                        textColor = DisplayUtils.getColorResource(fragment.getResources(), android.R.color.black);
                        int start = requestIdIndex + TransferUtils.TRANSFER_KEY_START;
                        textViewResult.setText(String.format(fragment.getResources()
                                .getString(R.string.text_transfer_upload_complete),
                                result.substring(start, start + TransferUtils.TRANSFER_KEY_LENGTH)));
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
