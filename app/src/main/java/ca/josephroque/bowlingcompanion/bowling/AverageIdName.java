package ca.josephroque.bowlingcompanion.bowling;

/**
 * Created by Joseph Roque on 2015-07-22. Offers methods for retrieving a name, average and id from an object.
 */
public interface AverageIdName
        extends DeletableItem {

    /**
     * Gets the name for this object.
     *
     * @return a name for the object
     */
    String getName();

    /**
     * Gets the average for this object.
     *
     * @return an average for this object
     */
    short getAverage();

    /**
     * Gets the id for this object.
     *
     * @return an id for this object
     */
    long getId();
}
