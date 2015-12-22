package ca.josephroque.bowlingcompanion.wrapper;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Joseph Roque on 2015-07-22. Organizes the data for a league or event.
 */
public class LeagueEvent
        implements Parcelable, NameAverageId {

    /** Unique id of the league / event. */
    private long mLeagueEventId;
    /** Name of the league / event. */
    private final String mLeagueEventName;
    /** Average of the league / event. */
    private final short mLeagueEventAverage;
    /** Number of games in the league / event. */
    private final byte mLeagueEventNumberOfGames;
    /** Indicates if this league / event has been deleted. */
    private boolean mIsDeleted;

    /**
     * Assigns the member variables to the parameters provided.
     *
     * @param id unique id of the league / event
     * @param name name of the league / event
     * @param average average of the league / event
     * @param numberOfGames number of games in the league / event
     */
    public LeagueEvent(long id, String name, short average, byte numberOfGames) {
        this.mLeagueEventId = id;
        this.mLeagueEventName = name;
        this.mLeagueEventAverage = average;
        this.mLeagueEventNumberOfGames = numberOfGames;
    }

    /**
     * Recreates a league / event object from a {@link android.os.Parcel}.
     *
     * @param pc league / event data
     */
    public LeagueEvent(Parcel pc) {
        this.mLeagueEventId = pc.readLong();
        this.mLeagueEventName = pc.readString();
        this.mLeagueEventAverage = (short) pc.readInt();
        this.mLeagueEventNumberOfGames = pc.readByte();
    }

    /**
     * Gets the league / event's name.
     *
     * @return the value of {@code mLeagueEventName}
     */
    public String getLeagueEventName() {
        return mLeagueEventName;
    }

    /**
     * Gets the league / event's id.
     *
     * @return the value of {@code mLeagueEventId}
     */
    public long getLeagueEventId() {
        return mLeagueEventId;
    }

    /**
     * Gets the league / event's average.
     *
     * @return the value of {@code mLeagueEventAverage}
     */
    public short getLeagueEventAverage() {
        return mLeagueEventAverage;
    }

    /**
     * Gets the league / event's number of games.
     *
     * @return the value of {@code mLeagueEventNumberOfGames}
     */
    public byte getLeagueEventNumberOfGames() {
        return mLeagueEventNumberOfGames;
    }

    /**
     * Checks if this object is a league or an event.
     *
     * @return {@code true} if the object's name starts with 'E'
     */
    public boolean isEvent() {
        return getLeagueEventName().startsWith("E");
    }

    /**
     * Sets a new value for {@code mLeagueEventId}.
     *
     * @param leagueEventId the new id
     */
    public void setLeagueEventId(long leagueEventId) {
        this.mLeagueEventId = leagueEventId;
    }

    @Override
    public String getName() {
        return getLeagueEventName();
    }

    @Override
    public short getAverage() {
        return getLeagueEventAverage();
    }

    @Override
    public long getId() {
        return getLeagueEventId();
    }

    @Override
    public void writeToParcel(Parcel pc, int flags) {
        pc.writeLong(mLeagueEventId);
        pc.writeString(mLeagueEventName);
        pc.writeInt(mLeagueEventAverage);
        pc.writeByte(mLeagueEventNumberOfGames);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Used to create objects and arrays from this class.
     */
    public static final Parcelable.Creator<LeagueEvent> CREATOR
            = new Parcelable.Creator<LeagueEvent>() {

        @Override
        public LeagueEvent createFromParcel(Parcel pc) {
            return new LeagueEvent(pc);
        }

        @Override
        public LeagueEvent[] newArray(int size) {
            return new LeagueEvent[size];
        }
    };

    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof LeagueEvent))
            return false;
        if (other == this)
            return true;

        LeagueEvent leagueEvent = (LeagueEvent) other;
        return getLeagueEventName().equals(leagueEvent.getLeagueEventName());
    }

    @SuppressWarnings("CheckStyle")
    @Override
    public int hashCode() {
        int result = 19;
        int c = getLeagueEventName().hashCode();
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
