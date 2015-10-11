package ca.josephroque.bowlingcompanion.bowling;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joseph Roque on 2015-07-22. Organizes the data for a series.
 */
public class Series
        implements Parcelable, DeletableItem {

    /** Unique id of the series. */
    private final long mSeriesId;
    /** Date that the series occurred. */
    private String mSeriesDate;
    /** Scores of the games in the series. */
    private final List<Short> mSeriesGames;
    /** Match play results of the games in the series. */
    private final List<Byte> mSeriesMatchPlay;
    /** Indicates if this series has been deleted. */
    private boolean mIsDeleted;

    /**
     * Assigns references for member variables to parameters.
     *
     * @param id unique id of the series
     * @param date date the series occurred
     * @param games scores of the games in the series
     * @param matchPlay match play results of the games in the series
     */
    public Series(long id, String date, List<Short> games, List<Byte> matchPlay) {
        this.mSeriesId = id;
        this.mSeriesDate = date;
        this.mSeriesGames = games;
        this.mSeriesMatchPlay = matchPlay;
    }

    /**
     * Recreates a series object from a {@link android.os.Parcel}.
     *
     * @param pc series data
     */
    @SuppressWarnings("unchecked")
    public Series(Parcel pc) {
        mSeriesId = pc.readLong();
        mSeriesDate = pc.readString();
        mSeriesGames = (ArrayList<Short>) pc.readArrayList(Short.class.getClassLoader());
        mSeriesMatchPlay = (ArrayList<Byte>) pc.readArrayList(Byte.class.getClassLoader());
    }

    /**
     * Gets the series' date.
     *
     * @return the value of {@code mSeriesDate}
     */
    public String getSeriesDate() {
        return mSeriesDate;
    }

    /**
     * Sets a new date for the series.
     *
     * @param seriesDate new value for {@code mSeriesDate}
     */
    public void setSeriesDate(String seriesDate) {
        this.mSeriesDate = seriesDate;
    }

    /**
     * Gets the series' id.
     *
     * @return the value of {@code mSeriesId}
     */
    public long getSeriesId() {
        return mSeriesId;
    }

    /**
     * Gets the series' games.
     *
     * @return the value of {@code mSeriesGames}
     */
    public List<Short> getSeriesGames() {
        return mSeriesGames;
    }

    /**
     * Gets the series' math play results.
     *
     * @return the value of {@code mSeriesMatchPlay}
     */
    public List<Byte> getSeriesMatchPlayResults() {
        return mSeriesMatchPlay;
    }

    /**
     * Gets the number of games in the series.
     *
     * @return the size of {@code mSeriesGames}
     */
    public byte getNumberOfGames() {
        return (byte) mSeriesGames.size();
    }

    /**
     * Gets the total of all the games in the series.
     *
     * @return sum of final scores
     */
    public short getSeriesTotal() {
        short sum = 0;
        for (short score : mSeriesGames)
            sum += score;
        return sum;
    }

    @Override
    public void writeToParcel(Parcel pc, int flags) {
        pc.writeLong(mSeriesId);
        pc.writeString(mSeriesDate);
        pc.writeList(mSeriesGames);
        pc.writeList(mSeriesMatchPlay);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Used to create objects and arrays from this class.
     */
    public static final Parcelable.Creator<Series> CREATOR = new Parcelable.Creator<Series>() {

        @Override
        public Series createFromParcel(Parcel pc) {
            return new Series(pc);
        }

        @Override
        public Series[] newArray(int size) {
            return new Series[size];
        }
    };

    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof Series))
            return false;
        if (other == this)
            return true;

        Series series = (Series) other;
        return getSeriesId() == series.getSeriesId();
    }

    @Override
    public int hashCode() {
        final int result = 17;
        final int primeNumber = 37;
        final int bytesOffset = 32;
        int c = (int) (mSeriesId ^ (mSeriesId >> bytesOffset));
        return primeNumber * result + c;
    }

    @Override
    public void setIsDeleted(boolean deleted) {
        this.mIsDeleted = deleted;
    }

    @Override
    public boolean wasDeleted() {
        return mIsDeleted;
    }
}
