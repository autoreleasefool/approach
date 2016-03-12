package ca.josephroque.bowlingcompanion.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.josephroque.bowlingcompanion.MainActivity;
import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.theme.Theme;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transfer, container, false);
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
     * Creates a new instance of this fragment.
     *
     * @return the new instance
     */
    public static TransferFragment newInstance() {
        return new TransferFragment();
    }
}
