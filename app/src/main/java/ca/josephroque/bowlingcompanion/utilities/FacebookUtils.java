package ca.josephroque.bowlingcompanion.utilities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

/**
 * Created by Joseph Roque on 2015-08-30. Provides methods for opening the app's Facebook page.
 */
public final class FacebookUtils {

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "FacebookUtils";

    /**
     * Intent to open the official Facebook app. If the Facebook app is not installed then the default web browser will
     * be used.
     *
     * @param pm Instance of the {@link PackageManager}.
     * @return An intent that will open the app's Facebook page.
     */
    public static Intent newFacebookIntent(PackageManager pm) {
        String facebookUrl = "https://www.facebook.com/BowlingCompanion";
        Uri uri;
        try {
            pm.getPackageInfo("com.facebook.katana", 0);
            // http://stackoverflow.com/a/24547437/1048340
            uri = Uri.parse("fb://facewebmodal/f?href=" + facebookUrl);
        } catch (PackageManager.NameNotFoundException e) {
            uri = Uri.parse(facebookUrl);
        }
        return new Intent(Intent.ACTION_VIEW, uri);
    }

    /**
     * Default private constructor.
     */
    private FacebookUtils() {
        // does nothing
    }
}
