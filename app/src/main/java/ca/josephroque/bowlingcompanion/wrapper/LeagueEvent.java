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
    /** Indicates if this instance represents a league or an event. */
    private final boolean mIsEvent;
    /** Name of the league / event. */
    private final String mLeagueEventName;
    /** Average of the league / event. */
    private final float mLeagueEventAverage;
    /** Initial average of the league. */
    private final short mLeagueBaseAverage;
    /** Initial number of games played. */
    private final int mLeagueBaseGames;
    /** Number of games in the league / event. */
    private final int mLeagueEventNumberOfGames;
    /** Indicates if this league / event has been deleted. */
    private boolean mIsDeleted;

    /**
     * Assigns the member variables to the parameters provided.
     *
     * @param id unique id of the league / event
     * @param name name of the league / event
     * @param isEvent indicates if this instance should represent a league or an event
     * @param average average of the league / event
     * @param baseAverage base average of the league
     * @param baseGames number of games used towards the base average
     * @param numberOfGames number of games in the league / event
     */
    public LeagueEvent(long id,
                       String name,
                       boolean isEvent,
                       float average,
                       short baseAverage,
                       int baseGames,
                       int numberOfGames) {
        this.mLeagueEventId = id;
        this.mLeagueEventName = name;
        this.mIsEvent = isEvent;
        this.mLeagueEventAverage = average;
        this.mLeagueBaseAverage = baseAverage;
        this.mLeagueBaseGames = baseGames;
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
        this.mIsEvent = pc.readInt() == 1;
        this.mLeagueEventAverage = pc.readFloat();
        this.mLeagueBaseAverage = (short) pc.readInt();
        this.mLeagueBaseGames = pc.readInt();
        this.mLeagueEventNumberOfGames = pc.readInt();
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
    public float getLeagueEventAverage() {
        return mLeagueEventAverage;
    }

    /**
     * If this instance is a league, gets its base average.
     *
     * @return the value of {@code mLeagueBaseAverage}, or -1 if {@code isEvent()} is true
     */
    public short getBaseAverage() {
        if (isEvent())
            return -1;
        else
            return mLeagueBaseAverage;
    }

    /**
     * If this instance is a league, gets the number of games used to achieve the base average.
     *
     * @return the value of {@code mLeagueBaseGames}, or -1 if {@code isEvent()} is true
     */
    public int getBaseGames() {
        if (isEvent())
            return 0;
        else
            return mLeagueBaseGames;
    }

    /**
     * Gets the league / event's number of games.
     *
     * @return the value of {@code mLeagueEventNumberOfGames}
     */
    public int getLeagueEventNumberOfGames() {
        return mLeagueEventNumberOfGames;
    }

    /**
     * Checks if this object is a league or an event.
     *
     * @return the value of {@code mIsEvent}
     */
    public boolean isEvent() {
        return mIsEvent;
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
    public float getAverage() {
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
        pc.writeInt(mIsEvent
                ? 1
                : 0);
        pc.writeFloat(mLeagueEventAverage);
        pc.writeInt(mLeagueBaseAverage);
        pc.writeInt(mLeagueBaseGames);
        pc.writeInt(mLeagueEventNumberOfGames);
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
        return getLeagueEventName().equals(leagueEvent.getLeagueEventName()) && isEvent() == leagueEvent.isEvent();
    }

    @SuppressWarnings("CheckStyle")
    @Override
    public int hashCode() {
        int result = 19;
        int c = getLeagueEventName().hashCode();
        c += mIsEvent
                ? 1
                : 0;
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
