package ca.josephroque.bowlingcompanion.data;

/**
 * Created by Joseph Roque on 2015-07-23. Offers method for setting an item's status to "deleted".
 */
public interface DeleteableData
{

    /**
     * Sets the object's "deleted" status.
     *
     * @param deleted true if the object is deleted, false otherwise
     */
    void setIsDeleted(boolean deleted);

    /**
     * Checks if the object has been deleted and returns true if so.
     *
     * @return true if the object is deleted, false otherwise
     */
    boolean wasDeleted();
}
