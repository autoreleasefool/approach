package ca.josephroque.bowlingcompanion.utilities;

import ca.josephroque.bowlingcompanion.Constants;

/**
 * Created by Joseph Roque on 15-03-19. Provides methods for determining bowling scores based on user input
 */
@SuppressWarnings("CheckStyle")
public final class Score {

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "Score";

    /** Position of the left 2 pin. */
    public static final byte LEFT_2_PIN = 0;
    /** Position of the left 3 pin. */
    public static final byte LEFT_3_PIN = 1;
    /** Position of the head pin. */
    public static final byte HEAD_PIN = 2;
    /** Position of the right 3 pin. */
    public static final byte RIGHT_3_PIN = 3;
    /** Position of the right 2 pin. */
    public static final byte RIGHT_2_PIN = 4;

    /** Value of a 2 pin. */
    private static final byte TWO_PIN_VALUE = 2;
    /** Value of a 3 pin. */
    private static final byte THREE_PIN_VALUE = 3;
    /** Value of a head pin. */
    private static final byte HEAD_PIN_VALUE = 5;

    /**
     * Gets the score value of the frame from the balls.
     *
     * @param frame the frame to get score of
     * @return score of the frame, in a 5 pin game
     */
    public static int getValueOfFrame(boolean[] frame, boolean countPinsKnocked) {
        int frameValue = 0;
        for (byte i = 0; i < frame.length; i++) {
            if (frame[i] == countPinsKnocked) {
                switch (i) {
                    case LEFT_2_PIN:
                    case RIGHT_2_PIN:
                        frameValue += TWO_PIN_VALUE;
                        break;
                    case LEFT_3_PIN:
                    case RIGHT_3_PIN:
                        frameValue += THREE_PIN_VALUE;
                        break;
                    case HEAD_PIN:
                        frameValue += HEAD_PIN_VALUE;
                        break;
                    default: //do nothing
                }
            }
        }
        return frameValue;
    }

    /**
     * Gets the score value of the frame from the balls, depending on which balls were already knocked down in the
     * previous frame.
     *
     * @param prevFrame state of the pins in the previous frame
     * @param frameToGet state of the pins the current frame
     * @return score of the frame, in a 5 pin game
     */
    public static int getValueOfFrameDifference(boolean[] prevFrame, boolean[] frameToGet) {
        int frameValue = 0;
        for (byte i = 0; i < frameToGet.length; i++) {
            if (frameToGet[i] && !prevFrame[i]) {
                switch (i) {
                    case LEFT_2_PIN:
                    case RIGHT_2_PIN:
                        frameValue += TWO_PIN_VALUE;
                        break;
                    case LEFT_3_PIN:
                    case RIGHT_3_PIN:
                        frameValue += THREE_PIN_VALUE;
                        break;
                    case HEAD_PIN:
                        frameValue += HEAD_PIN_VALUE;
                        break;
                    default: //do nothing
                }
            }
        }
        return frameValue;
    }

    /**
     * Gets textual value of ball.
     *
     * @param pins state of the pins
     * @param ball the ball to get the value of
     * @param shouldReturnSymbol indicates if a symbol should be returned no matter what
     * @param isAfterStrike indicates if the ball being counted was after a strike
     * @return textual value of the ball
     */
    public static String getValueOfBall(boolean[] pins,
                                        int ball,
                                        boolean shouldReturnSymbol,
                                        boolean isAfterStrike) {
        int ballValue = 0;
        for (byte i = 0; i < pins.length; i++) {
            if (pins[i]) {
                switch (i) {
                    case LEFT_2_PIN:
                    case RIGHT_2_PIN:
                        ballValue += TWO_PIN_VALUE;
                        break;
                    case LEFT_3_PIN:
                    case RIGHT_3_PIN:
                        ballValue += THREE_PIN_VALUE;
                        break;
                    case HEAD_PIN:
                        ballValue += HEAD_PIN_VALUE;
                        break;
                    default: //do nothing
                }
            }
        }

        switch (ballValue) {
            default:
                throw new RuntimeException("Invalid value for ball: " + ballValue);
            case 0:
                return Constants.BALL_EMPTY;
            case 2:
            case 3:
            case 4:
            case 6:
            case 9:
            case 12:
                return String.valueOf(ballValue);
            case 5:
                if ((ball == 0 || shouldReturnSymbol) && pins[2]) {
                    return Constants.BALL_HEAD_PIN;
                } else {
                    return "5";
                }
            case 7:
                if ((ball == 0 || shouldReturnSymbol) && pins[2]) {
                    return Constants.BALL_HEAD_PIN_2;
                } else {
                    return "7";
                }
            case 8:
                if ((ball == 0 || shouldReturnSymbol) && pins[2]) {
                    return Constants.BALL_SPLIT;
                } else
                    return "8";
            case 10:
                if ((ball == 0 || shouldReturnSymbol) && pins[2]
                        && ((pins[0] && pins[1])
                        || pins[3] && pins[4])) {
                    return Constants.BALL_CHOP_OFF;
                } else {
                    return "10";
                }
            case 11:
                if ((ball == 0 || shouldReturnSymbol) && pins[2]) {
                    return Constants.BALL_ACE;
                } else
                    return "11";
            case 13:
                if ((ball == 0 || shouldReturnSymbol) && !pins[0]) {
                    return Constants.BALL_LEFT;
                } else if ((ball == 0 || shouldReturnSymbol) && !pins[4]) {
                    return Constants.BALL_RIGHT;
                } else {
                    return "13";
                }
            case 15:
                if ((ball == 0 || shouldReturnSymbol)) {
                    return Constants.BALL_STRIKE;
                } else if (ball == 1 && !isAfterStrike) {
                    return Constants.BALL_SPARE;
                } else {
                    return "15";
                }
        }
    }

    /**
     * Gets textual value of ball based on surrounding balls.
     *
     * @param ballsOfFrame list of all balls in the frame
     * @param ball the ball to get the value of
     * @param shouldReturnSymbol indicates if a symbol should be returned no matter what
     * @param isAfterStrike indicates if the ball being counted was after a strike
     * @return textual value of the ball
     */
    public static String getValueOfBallDifference(boolean[][] ballsOfFrame,
                                                  int ball,
                                                  boolean shouldReturnSymbol,
                                                  boolean isAfterStrike) {
        boolean[] pinAlreadyKnockedDown = new boolean[5];

        if (ball > 0) {
            for (byte j = 0; j < 5; j++) {
                if (ballsOfFrame[ball - 1][j]) {
                    pinAlreadyKnockedDown[j] = true;
                }
            }
        }

        int ballValue = 0;
        for (byte i = 0; i < 5; i++) {
            if (ballsOfFrame[ball][i] && !pinAlreadyKnockedDown[i]) {
                switch (i) {
                    case 0:
                    case 4:
                        ballValue += 2;
                        break;
                    case 1:
                    case 3:
                        ballValue += 3;
                        break;
                    case 2:
                        ballValue += 5;
                        break;
                    default: //do nothing
                }
            }
        }

        switch (ballValue) {
            default:
                throw new RuntimeException("Invalid value for ball: " + ballValue);
            case 0:
                return Constants.BALL_EMPTY;
            case 2:
            case 3:
            case 4:
            case 6:
            case 9:
            case 12:
                return String.valueOf(ballValue);
            case 5:
                if (((ball == 0 && !isAfterStrike) || shouldReturnSymbol)
                        && ballsOfFrame[ball][2] && !pinAlreadyKnockedDown[2])
                    return Constants.BALL_HEAD_PIN;
                else
                    return "5";
            case 7:
                if (((ball == 0 && !isAfterStrike) || shouldReturnSymbol) && ballsOfFrame[ball][2])
                    return Constants.BALL_HEAD_PIN_2;
                else
                    return "7";
            case 8:
                if (((ball == 0 && !isAfterStrike) || shouldReturnSymbol) && ballsOfFrame[ball][2])
                    return Constants.BALL_SPLIT;
                else
                    return "8";
            case 10:
                if (((ball == 0 && !isAfterStrike) || shouldReturnSymbol) && ballsOfFrame[ball][2]
                        && ((ballsOfFrame[ball][0] && ballsOfFrame[ball][1])
                        || (ballsOfFrame[ball][3] && ballsOfFrame[ball][4])))
                    return Constants.BALL_CHOP_OFF;
                else
                    return "10";
            case 11:
                if (((ball == 0 && !isAfterStrike) || shouldReturnSymbol) && ballsOfFrame[ball][2])
                    return Constants.BALL_ACE;
                else
                    return "11";
            case 13:
                if (((ball == 0 && !isAfterStrike) || shouldReturnSymbol) && !ballsOfFrame[ball][0])
                    return Constants.BALL_LEFT;
                else if (((ball == 0 && !isAfterStrike) || shouldReturnSymbol)
                        && !ballsOfFrame[ball][4])
                    return Constants.BALL_RIGHT;
                else
                    return "13";
            case 15:
                if (((ball == 0 && !isAfterStrike) || shouldReturnSymbol))
                    return Constants.BALL_STRIKE;
                else if (ball == 1 && !isAfterStrike)
                    return Constants.BALL_SPARE;
                else
                    return "15";
        }
    }

    /**
     * Gets a boolean from a char.
     *
     * @param input char to convert to boolean
     * @return true if input is equal to '1', false otherwise
     */
    public static boolean getBoolean(char input) {
        return input == '1';
    }

    /**
     * Creates an int from an array of booleans.
     *
     * @param frame array to convert to int
     * @return a integer representing the booleans if they were binary
     */
    public static int booleanFrameToInt(boolean[] frame) {
        int ball = 0;
        for (int i = 0; i < frame.length; i++) {
            if (frame[i])
                ball += Math.pow(2, -i + 4);
        }
        return ball;
    }

    /**
     * Converts an int to a boolean array.
     *
     * @param ball int from 0-31
     * @return boolean array binary representation of {@code ball}
     */
    public static boolean[] ballIntToBoolean(int ball) {
        if (ball < 0 || ball > 31)
            throw new IllegalArgumentException("cannot convert value: " + ball);
        boolean[] pinState = new boolean[5];
        String ballBinary = String.format("%5s", Integer.toBinaryString(ball)).replace(' ', '0');
        for (int i = 0; i < pinState.length; i++)
            pinState[i] = ballBinary.charAt(i) == '1';
        return pinState;
    }

    /**
     * Gets the String representation of a number of fouls.
     *
     * @param i integer representation of the number of fouls
     * @return string representation of the number of fouls
     */
    public static String foulIntToString(int i) {
        switch (i) {
            default:
                return "0";
            case 24:
                return "3";
            case 25:
                return "2";
            case 26:
                return "23";
            case 27:
                return "1";
            case 28:
                return "13";
            case 29:
                return "12";
            case 30:
                return "123";
        }
    }

    /**
     * Gets the int representation of a String of fouls in a frame.
     *
     * @param s string representation of the number of fouls
     * @return integer representation of the number of fouls
     */
    public static int foulStringToInt(String s) {
        switch (s) {
            default:
                return 0;
            case "3":
                return 24;
            case "2":
                return 25;
            case "23":
                return 26;
            case "1":
                return 27;
            case "13":
                return 28;
            case "12":
                return 29;
            case "123":
                return 30;
        }
    }

    /**
     * Counts the number of pins which are standing in a frame.
     *
     * @param frame frame to count
     * @return number of booleans in the array which are {@code true}
     */
    public static byte countStandingPins(boolean[] frame) {
        byte numberOfPinStanding = 0;
        for (boolean b : frame) {
            if (b)
                numberOfPinStanding++;
        }
        return numberOfPinStanding;
    }

    /**
     * Default private constructor.
     */
    private Score() {
        // does nothing
    }
}
