package ca.josephroque.bowlingcompanion.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joseph Roque on 2015-07-22. Organizes the data for a series.
 */
public class Series
        implements Parcelable, DeleteableData
{

    /** Unique id of the series. */
    private long mSeriesId;
    /** Date that the series occurred. */
    private String mSeriesDate;
    /** Scores of the games in the series. */
    private List<Short> mSeriesGames;
    /** Indicates if this series has been deleted. */
    private boolean mIsDeleted;

    /**
     * Assigns references for member variables to parameters.
     *
     * @param id unique id of the series
     * @param date date the series occurred
     * @param games scores of the games in the series
     */
    public Series(long id, String date, List<Short> games)
    {
        this.mSeriesId = id;
        this.mSeriesDate = date;
        this.mSeriesGames = games;
    }

    /**
     * Recreates a series object from a {@link android.os.Parcel}.
     *
     * @param pc series data
     */
    @SuppressWarnings("unchecked")
    public Series(Parcel pc)
    {
        mSeriesId = pc.readLong();
        mSeriesDate = pc.readString();
        mSeriesGames = (ArrayList<Short>) pc.readArrayList(Short.class.getClassLoader());
    }

    /**
     * Gets the series' date.
     *
     * @return the value of {@code mSeriesDate}
     */
    public String getSeriesDate()
    {
        return mSeriesDate;
    }

    /**
     * Sets a new date for the series.
     *
     * @param seriesDate new value for {@code mSeriesDate}
     */
    public void setSeriesDate(String seriesDate)
    {
        this.mSeriesDate = seriesDate;
    }

    /**
     * Gets the series' id.
     *
     * @return the value of {@code mSeriesId}
     */
    public long getSeriesId()
    {
        return mSeriesId;
    }

    /**
     * Gets the series' games.
     *
     * @return the value of {@code mSeriesGames}
     */
    public List<Short> getSeriesGames()
    {
        return mSeriesGames;
    }

    /**
     * Gets the number of games in the series.
     *
     * @return the size of {@code mSeriesGames}
     */
    public byte getNumberOfGames()
    {
        return (byte) mSeriesGames.size();
    }

    @Override
    public void writeToParcel(Parcel pc, int flags)
    {
        pc.writeLong(mSeriesId);
        pc.writeString(mSeriesDate);
        pc.writeList(mSeriesGames);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    /**
     * Used to create objects and arrays from this class.
     */
    public static final Parcelable.Creator<Series> CREATOR = new Parcelable.Creator<Series>()
    {

        @Override
        public Series createFromParcel(Parcel pc)
        {
            return new Series(pc);
        }

        @Override
        public Series[] newArray(int size)
        {
            return new Series[size];
        }
    };

    @Override
    public boolean equals(Object other)
    {
        if (other == null || !(other instanceof Series))
            return false;
        if (other == this)
            return true;

        Series series = (Series) other;
        return getSeriesId() == series.getSeriesId();
    }

    @SuppressWarnings("CheckStyle")
    @Override
    public int hashCode()
    {
        int result = 17;
        int c = (int) (mSeriesId ^ (mSeriesId >> 32));
        return 37 * result + c;
    }

    @Override
    public void setIsDeleted(boolean deleted)
    {
        this.mIsDeleted = deleted;
    }

    @Override
    public boolean wasDeleted()
    {
        return mIsDeleted;
    }
}
