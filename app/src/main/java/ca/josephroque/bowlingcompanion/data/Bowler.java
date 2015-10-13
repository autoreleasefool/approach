package ca.josephroque.bowlingcompanion.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Joseph Roque on 2015-07-22. Organizes the data for a bowler.
 */
public class Bowler
        implements Parcelable, NameAverageId {

    /** Name of the bowler. */
    private final String mBowlerName;
    /** Unique id of the bowler. */
    private long mBowlerId;
    /** Average of the bowler. */
    private final short mBowlerAverage;
    /** Indicates if this bowler has been deleted. */
    private boolean mIsDeleted;

    /**
     * Assigns the member variables to the parameters provided.
     *
     * @param id unique id of the bowler
     * @param name name of the bowler
     * @param average average of the bowler
     */
    public Bowler(long id, String name, short average) {
        this.mBowlerId = id;
        this.mBowlerName = name;
        this.mBowlerAverage = average;
    }

    /**
     * Recreates a bowler object from a {@link android.os.Parcel}.
     *
     * @param pc bowler data
     */
    public Bowler(Parcel pc) {
        mBowlerId = pc.readLong();
        mBowlerName = pc.readString();
        mBowlerAverage = (short) pc.readInt();
    }


    /**
     * Gets the bowler's name.
     *
     * @return the value of {@code mBowlerName}
     */
    public String getBowlerName() {
        return mBowlerName;
    }

    /**
     * Sets a new value for {@code mBowlerId}.
     *
     * @param bowlerId the new id
     */
    public void setBowlerId(long bowlerId) {
        this.mBowlerId = bowlerId;
    }

    /**
     * Gets the bowler's id.
     *
     * @return the value of {@code mBowlerId}
     */
    public long getBowlerId() {
        return mBowlerId;
    }

    /**
     * Gets the bowler's average.
     *
     * @return the value of {@code mBowlerAverage}
     */
    public short getBowlerAverage() {
        return mBowlerAverage;
    }

    @Override
    public String getName() {
        return getBowlerName();
    }

    @Override
    public short getAverage() {
        return getBowlerAverage();
    }

    @Override
    public long getId() {
        return getBowlerId();
    }

    @Override
    public void writeToParcel(Parcel pc, int flags) {
        pc.writeLong(mBowlerId);
        pc.writeString(mBowlerName);
        pc.writeInt(mBowlerAverage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Used to create objects and arrays from this class.
     */
    public static final Parcelable.Creator<Bowler> CREATOR = new Parcelable.Creator<Bowler>() {

        @Override
        public Bowler createFromParcel(Parcel pc) {
            return new Bowler(pc);
        }

        @Override
        public Bowler[] newArray(int size) {
            return new Bowler[size];
        }
    };

    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof Bowler))
            return false;
        if (other == this)
            return true;

        Bowler bowler = (Bowler) other;
        return getBowlerName().equals(bowler.getBowlerName());
    }

    @SuppressWarnings("CheckStyle")
    @Override
    public int hashCode() {
        int result = 89;
        int c = getBowlerName().hashCode();
        return 37 * result + c;
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
