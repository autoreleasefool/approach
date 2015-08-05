package ca.josephroque.bowlingcompanion.utilities;

import ca.josephroque.bowlingcompanion.Constants;

/**
 * Created by Joseph Roque on 15-03-19. Provides methods for determining bowling scores based on
 * user input
 */
public final class Score
{

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "Score";

    /**
     * Default private constructor.
     */
    private Score()
    {
        // does nothing
    }

    /**
     * Gets the score value of the frame from the balls.
     *
     * @param frame the frame to get score of
     * @return score of the frame, in a 5 pin game
     */
    public static int getValueOfFrame(boolean[] frame)
    {
        int frameValue = 0;
        for (byte i = 0; i < frame.length; i++)
        {
            if (frame[i])
            {
                switch (i)
                {
                    case 0:
                    case 4:
                        frameValue += 2;
                        break;
                    case 1:
                    case 3:
                        frameValue += 3;
                        break;
                    case 2:
                        frameValue += 5;
                        break;
                    default: //do nothing
                }
            }
        }
        return frameValue;
    }

    /**
     * Gets the score value of the frame from the balls, depending on which balls were already
     * knocked down in the previous frame.
     *
     * @param prevFrame state of the pins in the previous frame
     * @param frameToGet state of the pins the current frame
     * @return score of the frame, in a 5 pin game
     */
    public static int getValueOfFrameDifference(boolean[] prevFrame, boolean[] frameToGet)
    {
        int frameValue = 0;
        for (byte i = 0; i < frameToGet.length; i++)
        {
            if (frameToGet[i] && !prevFrame[i])
            {
                switch (i)
                {
                    case 0:
                    case 4:
                        frameValue += 2;
                        break;
                    case 1:
                    case 3:
                        frameValue += 3;
                        break;
                    case 2:
                        frameValue += 5;
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
                                        boolean isAfterStrike)
    {
        int ballValue = 0;
        for (byte i = 0; i < 5; i++)
        {
            if (pins[i])
            {
                switch (i)
                {
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

        switch (ballValue)
        {
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
                if ((ball == 0 || shouldReturnSymbol) && pins[2])
                {
                    return Constants.BALL_HEAD_PIN;
                }
                else
                {
                    return "5";
                }
            case 7:
                if ((ball == 0 || shouldReturnSymbol) && pins[2])
                {
                    return Constants.BALL_HEAD_PIN_2;
                }
                else
                {
                    return "7";
                }
            case 8:
                if ((ball == 0 || shouldReturnSymbol) && pins[2])
                {
                    return Constants.BALL_SPLIT;
                }
                else
                    return "8";
            case 10:
                if ((ball == 0 || shouldReturnSymbol) && pins[2]
                        && ((pins[0] && pins[1])
                        || pins[3] && pins[4]))
                {
                    return Constants.BALL_CHOP_OFF;
                }
                else
                {
                    return "10";
                }
            case 11:
                if ((ball == 0 || shouldReturnSymbol) && pins[2])
                {
                    return Constants.BALL_ACE;
                }
                else
                    return "11";
            case 13:
                if ((ball == 0 || shouldReturnSymbol) && !pins[0])
                {
                    return Constants.BALL_LEFT;
                }
                else if ((ball == 0 || shouldReturnSymbol) && !pins[4])
                {
                    return Constants.BALL_RIGHT;
                }
                else
                {
                    return "13";
                }
            case 15:
                if ((ball == 0 || shouldReturnSymbol))
                {
                    return Constants.BALL_STRIKE;
                }
                else if (ball == 1 && !isAfterStrike)
                {
                    return Constants.BALL_SPARE;
                }
                else
                {
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
                                                  boolean isAfterStrike)
    {
        boolean[] pinAlreadyKnockedDown = new boolean[5];

        if (ball > 0)
        {
            for (byte j = 0; j < 5; j++)
            {
                if (ballsOfFrame[ball - 1][j])
                {
                    pinAlreadyKnockedDown[j] = true;
                }
            }
        }

        int ballValue = 0;
        for (byte i = 0; i < 5; i++)
        {
            if (ballsOfFrame[ball][i] && !pinAlreadyKnockedDown[i])
            {
                switch (i)
                {
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

        switch (ballValue)
        {
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
    public static boolean getBoolean(char input)
    {
        return input == '1';
    }

    /**
     * Creates a string from an array of booleans.
     *
     * @param frame array to convert to string
     * @return A String of 1's and 0's, where a 1 represents true in the array
     */
    public static int booleanFrameToInt(boolean[] frame)
    {
        int ball = 0;
        for (int i = 0; i < frame.length; i++)
        {
            if (frame[i])
                ball += Math.pow(2, -i + 4);
        }
        return ball;
    }

    public static boolean[] ballIntToBoolean(int ball)
    {
        boolean[] pinState = new boolean[5];
        String ballBinary = String.format("%5s", Integer.toBinaryString(ball)).replace(' ', '0');
        for (int i = 0; i < pinState.length; i++)
            pinState[i] = ballBinary.charAt(i) == '1';
        return pinState;
    }
}
