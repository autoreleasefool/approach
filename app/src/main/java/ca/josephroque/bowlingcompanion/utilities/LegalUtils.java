package ca.josephroque.bowlingcompanion.utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import ca.josephroque.bowlingcompanion.R;

/**
 * Created by Joseph Roque on 2016-01-14. Provides utility methods for displaying legal text to users.
 */
public final class LegalUtils {

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "LegalUtils";

    /**
     * Displays a dialog with the license text of the open source software used in the application.
     *
     * @param context to display dialog
     */
    public static void displayAttributions(Context context) {
        String licenses = FileUtils.retrieveTextFileAsset(context, "licenses.txt");

        // Error loading the license text
        if (licenses == null)
            return;

        // Creating alert dialog
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        View rootView = View.inflate(context, R.layout.dialog_scrollable_text, null);

        dialog.setView(rootView);
        final AlertDialog alertDialog = dialog.create();

        // Getting license text for dialog
        ((TextView) rootView.findViewById(R.id.tv_scrollable_text)).setText(
                Html.fromHtml(licenses.replace("\n", "<br />")));
        ((TextView) rootView.findViewById(R.id.tv_scrollable_text_dialog_title)).setText(context.getResources()
                .getString(R.string.text_attributions));

        rootView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    /**
     * Default private constructor.
     */
    private LegalUtils() {
        // does nothing
    }
}
