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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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

    /** URL to upload or download data to/from. */
    private static final String TRANSFER_SERVER_URL = "http://10.0.2.2:8080/upload";

    private static final int ERROR_IO_EXCEPTION = 5;
    private static final int ERROR_OUT_OF_MEMORY = 4;
    private static final int ERROR_FILE_NOT_FOUND = 3;
    private static final int ERROR_MALFORMED_URL = 2;
    private static final int ERROR_EXCEPTION = 1;
    private static final int SUCCESS = 0;

    /** The current {@link android.os.AsyncTask} being executed, so it can be cancelled if necessary. */
    private AsyncTask<Void, Integer, Integer> mCurrentTransferTask = null;

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
        cancelButton.getBackground()
                .setColorFilter(DisplayUtils.getColorResource(getResources(), R.color.theme_red_tertiary),
                        PorterDuff.Mode.MULTIPLY);
        cancelButton.setOnClickListener(new View.OnClickListener() {
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
            extends AsyncTask<Void, Integer, Integer> {

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
        protected Integer doInBackground(Void... params) {
            TransferFragment fragment = mFragment.get();
            if (fragment == null || fragment.getContext() == null)
                return 0;

            // Most of this method retrieved from this StackOverflow question.
            // http://stackoverflow.com/a/7645328/4896787

            // Preparing the database
            File dbFile = fragment.getContext().getDatabasePath(DatabaseHelper.DATABASE_NAME);
            String dbFilePath = dbFile.getAbsolutePath();
            Log.d(TAG, "Database file: " + dbFilePath);

            DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH:mm:ss", Locale.CANADA);

            HttpURLConnection connection;
            DataOutputStream outputStream;
            DataInputStream inputStream;

            final String lineEnd = "\r\n";
            final String twoHyphens = "--";
            final String boundary = "*****";
            int bytesRead;
            int bytesAvailable;
            int bufferSize;
            final int maxBufferSize = 1024 * 1024;
            final int serverSuccessResponse = 200;
            byte[] buffer;

            try {
                FileInputStream fileInputStream = new FileInputStream(dbFile);
                URL url = new URL(TRANSFER_SERVER_URL);

                // Preparing connection for upload
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);

                outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + dbFilePath + "\"" + lineEnd);
                outputStream.writeBytes(lineEnd);

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                Log.d(TAG, "Database size: " + bytesAvailable);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                try {
                    while (bytesRead > 0) {
                        try {
                            outputStream.write(buffer, 0, bufferSize);
                        } catch (OutOfMemoryError ex) {
                            Log.e(TAG, "Out of memory sending file.", ex);
                            return ERROR_OUT_OF_MEMORY;
                        }

                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }
                } catch (Exception ex) {
                    Log.e(TAG, "Error sending file.", ex);
                    return ERROR_EXCEPTION;
                }

                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                int serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();
                Log.d(TAG, "Response code: " + serverResponseCode);
                Log.d(TAG, "Message: " + serverResponseMessage);

                int response = ERROR_EXCEPTION;
                if (serverResponseCode == serverSuccessResponse) {
                    response = SUCCESS;
                }

                String connectionDate = null;
                Date serverTime = new Date(connection.getDate());
                try {
                    connectionDate = dateFormat.format(serverTime);
                } catch (Exception ex) {
                    Log.e(TAG, "Error parsing date.", ex);
                }
                Log.d(TAG, "Server response time: " + connectionDate);

                String filename = connectionDate + dbFilePath.substring(dbFilePath.lastIndexOf("."),
                        dbFilePath.length());
                Log.d(TAG, "Filename on server: " + filename);

                fileInputStream.close();
                outputStream.flush();
                outputStream.close();

                try {
                  inputStream = new DataInputStream(connection.getInputStream());
                  String inStr;
                  while ((inStr = inputStream.readLine()) != null) {
                    Log.d(TAG, "Server response: " + inStr);
                  }

                  inputStream.close();
                } catch (IOException ex) {
                  Log.e(TAG, "Error reading server response.", ex);
                }

                return response;
            } catch (FileNotFoundException ex) {
                Log.e(TAG, "Unable to find database file.", ex);
                return ERROR_FILE_NOT_FOUND;
            } catch (MalformedURLException ex) {
                Log.e(TAG, "Malformed url. I have no idea how this happened.", ex);
                return ERROR_MALFORMED_URL;
            } catch (IOException ex) {
                Log.e(TAG, "Couldn't open or maintain connection.", ex);
                return ERROR_IO_EXCEPTION;
            }
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
        protected void onPostExecute(Integer result) {
            TransferFragment fragment = mFragment.get();
            if (fragment == null || fragment.getContext() == null)
                return;

            Log.d(TAG, "Response: " + result);
            cleanupTask(fragment);
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
    }
}
