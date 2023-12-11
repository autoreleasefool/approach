# Most recent version

## iOS 1.4.0b19

```
- Fix: Frames with a single roll no longer count towards statistics
- Chore: New launch service colocates all launch initialization code
```

## Android v3.2.0

```
A lot of users have been having issues with the current transfer functionality, so I've created a new process which will let you export and save the data wherever you'd like, and then import it on a new device. Hopefully this helps those with longstanding issues of being unable to move to new devices.

This update also comes with some quality of life fixes:
- No more ads
- No more analytics
```

---

# Legacy versions

## iOS 1.3.1b18

```
- Feat: Track game duration
- Feat: Add Christmas icon
```

## iOS 1.3.0b17

```
- Feat: Quick Play button to create a new series in last played game
- Chore: Set up breadcrumbs for Sentry
- Chore: Prompt for app reviews after certain conditions met
```

## iOS 1.2.2b16

```
- Feat: Add Sentry SDK
```

## iOS 1.2.1b15

```
- Fix: 10th frame will show the correct score at all times, not only at the end of a game
- Fix: A strike or spare in the 10th frame will no longer cause the "Next" button to open the next game
```

## iOS 1.2.0b14

```
- Feat: Taps + Taps Spared statistics
- Fix: First Roll statistics after strikes are no longer incorrectly counted as spares
- Fix: ArchivedList is now sorted by date archived on
- Fix: Series Add button is now visible without Sharing feature enabled
```

## iOS 1.2.0b13

```
- Feat: Add ArchiveList
- Feat: Archive bowlers, leagues, series, and games
- Feat: Add new games to series after they're created
- Feat: Better empty state for GamesList
- Fix: Ensure game details are loaded before all other properties
```

## iOS 1.1.2b12

```
- Fix: Prevent score calculation from being applied to wrong game and overwriting score
```

## iOS 1.1.1b11

```
- Feat: More varied charts in series list
- Fix: Statistics not loading when a all games of a series are excluded
- Fix: Marking a pre-bowl as a regular series now updates the games in the series to be included in stats
```

## iOS 1.1.0b10

```
- Fix: Resolve issue with iPad icon picking
```

## iOS 1.1.0b9

```
- Feat: New statistics: Fives and Threes
- Feat: Descriptions of statistics in list
```

## iOS 1.0.4b8

```
- Feat: Show series and game statistics from GameDetails
- Feat: Render statistics in charts per game for series
- Fix: Pins left of deck stat now only calculated for frames with at least 1 roll
- Fix: Next button in GamesEditor is larger
- Fix: League # of games is dynamic by default
- Fix: Strikes are now calculated properly when followed by incomplete frames
```

## iOS 1.0.3b7

```
- Feat: Edit buttons in header for league and series
- Feat: When setting lanes for a game, prompts to copy to all other games
- Fix: Game details on iOS 17 no longer glitch and block screen
- Fix: Score is properly saved when locking game
- Fix: Gear no longer reorders in game editor when picking a ball
- Fix: Refactored how pin swiping works to reduce mistaps
```

## iOS 1.0.2b6

```
- Feat: More choices for statistics for widgets
- Feat: Widgets appearing on a line alone now take the full width
- Fix: Better language throughout the app to clarify when data is missing or ambiguous
- Fix: Populate bowler by default for stats when only one bowler exists
- Fix: Gear is no longer deleted when owner is deleted
```

## iOS 1.0.1b5

```
- Allows exporting user data in a single file for backup or sharing
```

## iOS 1.0.0b4

```
First official release of Approach for iOS!
```

## v3.1.2

```
- Fixed a crash some users experienced when trying to open a game
```

## v3.1.1

```
This version contains a bunch of bug fixes! Here's just a few:
- Creating or opening an event should no longer crash the app
- Fixed sharing games through the game overview
- Prevented scores from dropping below zero when there are too many fouls
```

## v3.1.0

```
Thank you for using the app while some of the kinks in the new version are worked out!
- See an overview of your games by tapping the list icon in the top right, or the overview menu item
- Share games and series from the game overview (saving to external storage required)

Bug fixes
- Lefts and rights are now reported correctly
- Return to the first incomplete game when editing a series (not just the first game)
- Fix some more rotation crashes and returning to the app from the background
```

## v3.0.2

```
This update contains a tonne of stability fixes for the new version.

- Fix pins not appearing for some users
- Fix crash when rotating the device
- "Middle hits which were strikes" now counts denominator accurately
- Some other stability bug fixes
```

## v3.0.0

```
Welcome to the new 5 Pin Bowling Companion. We completely rebuilt the app from scratch with all your favourite features and plenty of new ones.

- Teams
    - Create a team of bowlers to track together
    - Pick your team and select a league for each bowler
    - Change their order
    - Pressing the next ball button will take you to the next bowler! Keep an eye on whose name is at the top of the screen
- Highlight settings are now per league — long press to edit
- New statistics
```

## v2.1.9

```
- Stability fixes for Android Oreo
- Fix bugs with "Combine Series"
- Better font scaling for accessibility
- Auto lock games after final frame is complete
- List rows are larger for easier clicking
- H+2 is now counted as a Headpin, and Split + 2 is a split
- Allow more characters in bowler and league names
- Redesign series screen to avoid overlapping scores
```

## v2.1.8

```
- See your average with up to 1 decimal place by going to Settings > Statistics.
```

## v2.1.7

```
- Transfer your data to a new device! By accessing the "Transfer data" option in the menu, you can transfer your data to a new device by uploading it to our servers and downloading it elsewhere with your unique code.
- Adds missing attribution to Google Material Icons
```

## v2.1.6

```
- Added an option in the settings under "Editing Games" that allows users to disable the new change that moved the pins above the floating buttons.
```

## v2.1.5

```
- Fixed a bug that prevented new events from being made.
```

## v2.1.4

```
What's new
- Added THIS popup, to inform you of the newest changes each release.
- Leagues now have a "base average". Start recording a new league in the middle of a season? Long click a league, select "Change properties" and you can set your average so far and the number of games you've played, so your average in the app is more accurate.
- You can now duplicate a series. Duplicating a series will create a new series for the current date, with the same number of games and scores as the series you duplicate. This new series will use manually set games, and will affect your average, but no other stats.
- New statistics: percentage of time that your first ball is left or right of the headpin. Find them under "General"

Other changes
- Added an option to move the pins above the "next" button, instead of behind it, to prevent misclicks. This option is enabled by default.
- Fixed an issue where changing the name of a league or event sometimes added an "L" or "E" to the start. If this happened to you, you'll receive a pop-up asking if you'd like the names to be fixed.
```

## v2.1.3

```
- Fixed a bug introduced in v2.1.2 that caused some bowlers to not appear.
```

## v2.1.2

```
- H2 and H32 (not chop off) are no longer counted as "spare chances".
```

## v2.1.1

```
- Fixed a crash when checking the hi score possible
```

## v2.1

```
- Games with a score of 0 are now excluded from your average and statistics
- Strikes and spares are now highlighted in a game to be spotted more easily (you can disable this in the settings)
- Series now show their total and are highlighted over a certain value (can be changed/disable in settings)
- Bug fixes
```

## v2.0.5

```
- Bug fixes
```

## v2.0.4

```
- Bug fixes
```

## v2.0.3

```
- Long click a bowler or league to edit its name
- Improved match play recording (add your opponent's name and their score)
- Match play results now appear in the series list (you can disable this in the settings under "Interface")
- Bug fixes
```

## v2.0

```
Features
- Added a new tutorial on first startup
- Adding graphs for stats
- Support for tablets and landscape
- Swipe to knock over pins
- Series in the "Open" league can have 1-5 games
- You can now combine series in the "Open" league
- Swipe to delete bowlers/leagues/series

Design
- Material design
- New app icon
- Improved navigation
```

## v1.2.1

```
- Bug fixes
```

## v1.2

```
- Larger button to move to next frame
- Option to have frame auto advance after an interval of inactivity
- Bug fixes
```

## v1.1.3

```
- Added option to change header/stat fonts to black, for better readability
- Bug fixes
```

## v1.1.2

```
- Now returns to same game when phone comes off standby mode
- Changed display method of future frames in a game
- Bug fixes
```

## v1.1.1

```
- Added link in menu to settings
- Simplified settings screen
- Bug fixes
```

## v1.1

```
- Added new option to set results of match play
- Added match play statistics to the list of stats
- Added averages by game to the statistics
- Changed appearance of stats, now in expandable groups
- Fixed bug where "Grey" theme was not working
- Bug fixes
```

## v1.0.4

```
- Decreased memory when saving images, should reduce crashes when trying to save or share a series.
```

## v1.0.3

```
- Bug fixing
- Decreased memory used by background images.
- Decreased overall size of the app.
```

## v1.0.2

```
- Fixing issues with user authentication
```

## v1.0.1

```
- Fixing issues with user authentication
```
