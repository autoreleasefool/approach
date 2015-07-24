package ca.josephroque.bowlingcompanion.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

import ca.josephroque.bowlingcompanion.R;

/**
 * Created by Joseph Roque on 15-03-26. Provides methods for sharing the statistics and games
 * tracked by the application.
 */
@SuppressWarnings("Convert2Lambda")
public final class ShareUtils
{

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "ShareUtils";

    /**
     * Default private constructor.
     */
    private ShareUtils()
    {
        // does nothing
    }

    /**
     * Shows a dialog to prompt user to share the series or save it to the device.
     *
     * @param activity parent activity for the dialog
     * @param seriesId id of the series to share
     */
    public static void showShareDialog(final Activity activity, final long seriesId)
    {
        final CharSequence[] options = {"Save", "Share"};
        AlertDialog.Builder shareBuilder = new AlertDialog.Builder(activity);
        shareBuilder.setTitle("Save to device or share?")
                .setSingleChoiceItems(options, 0, null)
                .setPositiveButton(R.string.dialog_okay, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        int selectedItem =
                                ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        if (selectedItem == 0)
                            saveSeriesToDevice(activity, seriesId);
                        else
                            shareSeries(new WeakReference<Context>(activity), seriesId);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    /**
     * Creates a task to save an image of the series to the device and prompt the user to share it
     * with another service.
     *
     * @param context the current context
     * @param seriesId id of the series to share
     */
    @SuppressWarnings("unchecked")
    private static void shareSeries(WeakReference<Context> context, long seriesId)
    {
        new ShareSeriesTask().execute(Pair.create(context, seriesId));
    }

    /**
     * Creates an image of the series and saves it to the device.
     *
     * @param activity parent activity for the dialog
     * @param seriesId id of the series to share
     */
    private static void saveSeriesToDevice(final Activity activity, final long seriesId)
    {
        new Thread(new Runnable()
        {
            @SuppressWarnings("UnusedAssignment") //seriesBitmap set to null to free memory
            @Override
            public void run()
            {
                Bitmap seriesBitmap = ImageUtils.createImageFromSeries(activity, seriesId);
                final Uri imageUri = ImageUtils.insertImage(activity.getContentResolver(),
                        seriesBitmap,
                        String.valueOf(System.currentTimeMillis()),
                        "Series: " + seriesId);
                seriesBitmap.recycle();
                seriesBitmap = null;
                System.gc();

                activity.runOnUiThread(new Runnable()
                {
                    @SuppressWarnings("ConstantConditions")
                    @Override
                    public void run()
                    {
                        MediaScannerConnection.scanFile(activity,
                                new String[]{imageUri.getPath()}, null,
                                new MediaScannerConnection.OnScanCompletedListener()
                                {
                                    public void onScanCompleted(String path, Uri uri)
                                    {
                                        Log.i("ExternalStorage", "Scanned " + path + ":");
                                        Log.i("ExternalStorage", "-> uri=" + uri);
                                    }
                                });

                        Toast toast;
                        if (imageUri != null)
                            toast = Toast.makeText(
                                    activity, "Image successfully saved!", Toast.LENGTH_SHORT);
                        else
                            toast = Toast.makeText(
                                    activity, "Unable to save image", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }
        }).start();
    }

    /**
     * Creates an image for the series and prompts user to share it.
     */
    private static class ShareSeriesTask
            extends AsyncTask<Pair<WeakReference<Context>, Long>, Void, Pair<WeakReference<Context>,
            WeakReference<Intent>>>
    {

        @SafeVarargs
        @SuppressWarnings("UnusedAssignment") //image set to null to free memory
        @Override
        public final Pair<WeakReference<Context>, WeakReference<Intent>> doInBackground(
                Pair<WeakReference<Context>, Long>... params)
        {
            Context context = params[0].first.get();
            if (context == null)
                return null;
            long seriesId = params[0].second;
            Bitmap image = ImageUtils.createImageFromSeries(context, seriesId);
            Uri imageUri = ImageUtils.insertImage(context.getContentResolver(),
                    image,
                    String.valueOf(System.currentTimeMillis()),
                    "Series: " + seriesId);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/jpeg");
            OutputStream outStream = null;
            try
            {
                outStream = context.getContentResolver()
                        .openOutputStream(imageUri);
                //noinspection CheckStyle
                image.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                image.recycle();
                image = null;
                System.gc();
            }
            catch (Exception ex)
            {
                Log.e(TAG, "Could not create output stream", ex);
            }
            finally
            {
                if (outStream != null)
                {
                    try
                    {
                        outStream.close();
                    }
                    catch (IOException ex)
                    {
                        //does nothing - could not close output stream
                    }
                }
            }

            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            return Pair.create(params[0].first, new WeakReference<>(shareIntent));
        }

        @Override
        public void onPostExecute(Pair<WeakReference<Context>, WeakReference<Intent>> params)
        {
            if (params != null)
            {
                Context context = params.first.get();
                Intent intent = params.second.get();
                if (context == null || intent == null)
                    return;

                context.startActivity(Intent.createChooser(intent, "Share Image"));
            }
        }
    }
}
