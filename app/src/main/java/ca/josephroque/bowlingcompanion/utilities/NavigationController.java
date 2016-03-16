package ca.josephroque.bowlingcompanion.utilities;

/**
 * Created by Joseph Roque on 2016-03-15. An object which can enable or disable navigation in the application.
 */
public interface NavigationController {

    /**
     * Enables or disables navigation within the controller.
     *
     * @param enable {@code true} to enable navigation, {@code false} to disable.
     */
    void setNavigationEnabled(boolean enable);
}
