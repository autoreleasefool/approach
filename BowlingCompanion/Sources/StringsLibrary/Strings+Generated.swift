// swiftlint:disable all
// Generated using SwiftGen — https://github.com/SwiftGen/SwiftGen

import Foundation

// swiftlint:disable superfluous_disable_command file_length implicit_return prefer_self_in_static_references

// MARK: - Strings

// swiftlint:disable explicit_type_interface function_parameter_count identifier_name line_length
// swiftlint:disable nesting type_body_length type_name vertical_whitespace_opening_braces
public enum Strings {
  /// No
  public static let no = Strings.tr("Localizable", "no", fallback: "No")
  /// None
  public static let `none` = Strings.tr("Localizable", "none", fallback: "None")
  /// Other
  public static let other = Strings.tr("Localizable", "other", fallback: "Other")
  /// Tab
  public static let tab = Strings.tr("Localizable", "tab", fallback: "Tab")
  /// Other
  public static let unknown = Strings.tr("Localizable", "unknown", fallback: "Unknown")
  /// Yes
  public static let yes = Strings.tr("Localizable", "yes", fallback: "Yes")
  public enum Action {
    /// Add
    public static let add = Strings.tr("Localizable", "action.add", fallback: "Add")
    /// Actions
    public static let apply = Strings.tr("Localizable", "action.apply", fallback: "Apply")
    /// Cancel
    public static let cancel = Strings.tr("Localizable", "action.cancel", fallback: "Cancel")
    /// Delete
    public static let delete = Strings.tr("Localizable", "action.delete", fallback: "Delete")
    /// Discard
    public static let discard = Strings.tr("Localizable", "action.discard", fallback: "Discard")
    /// Dismiss
    public static let dismiss = Strings.tr("Localizable", "action.dismiss", fallback: "Dismiss")
    /// Edit
    public static let edit = Strings.tr("Localizable", "action.edit", fallback: "Edit")
    /// Filter
    public static let filter = Strings.tr("Localizable", "action.filter", fallback: "Filter")
    /// Manage
    public static let manage = Strings.tr("Localizable", "action.manage", fallback: "Manage")
    /// Reload
    public static let reload = Strings.tr("Localizable", "action.reload", fallback: "Reload")
    /// Reset
    public static let reset = Strings.tr("Localizable", "action.reset", fallback: "Reset")
    /// Save
    public static let save = Strings.tr("Localizable", "action.save", fallback: "Save")
    /// Start
    public static let start = Strings.tr("Localizable", "action.start", fallback: "Start")
    /// Try again
    public static let tryAgain = Strings.tr("Localizable", "action.tryAgain", fallback: "Try again")
  }
  public enum Alley {
    /// Alleys
    public static let title = Strings.tr("Localizable", "alley.title", fallback: "Alley")
    public enum Editor {
      public enum Fields {
        public enum Material {
          /// To help tell the difference, wooden lanes tend to show some wear, while synthetic lanes are usually harder and smoother.
          public static let help = Strings.tr("Localizable", "alley.editor.fields.material.help", fallback: "To help tell the difference, wooden lanes tend to show some wear, while synthetic lanes are usually harder and smoother.")
        }
        public enum Mechanism {
          /// Are the lanes interchangeable between multiple types of bowling (5-Pin and 10-Pin), or do they only support one kind?
          public static let help = Strings.tr("Localizable", "alley.editor.fields.mechanism.help", fallback: "Are the lanes interchangeable between multiple types of bowling (5-Pin and 10-Pin), or do they only support one kind?")
        }
        public enum PinBase {
          /// What kind of base do the pins have?
          public static let help = Strings.tr("Localizable", "alley.editor.fields.pinBase.help", fallback: "What kind of base do the pins have?")
        }
        public enum PinFall {
          /// Look at how the pins are set up. Do you notice the pins are pushed off the lane after each ball, or are they attached to strings and pulled up?
          public static let help = Strings.tr("Localizable", "alley.editor.fields.pinFall.help", fallback: "Look at how the pins are set up. Do you notice the pins are pushed off the lane after each ball, or are they attached to strings and pulled up?")
        }
      }
      public enum Help {
        /// Not sure about any of the settings? Ask a staff member! They'll probably be happy to help
        public static let askAStaffMember = Strings.tr("Localizable", "alley.editor.help.askAStaffMember", fallback: "Not sure about any of the settings? Ask a staff member! They'll probably be happy to help")
      }
    }
    public enum Error {
      public enum Empty {
        /// You haven't added any alleys yet.
        public static let message = Strings.tr("Localizable", "alley.error.empty.message", fallback: "You haven't added any alleys yet.")
        /// No alleys found
        public static let title = Strings.tr("Localizable", "alley.error.empty.title", fallback: "No alleys found")
        public enum Filter {
          /// Try changing your filters or adding a new alley.
          public static let message = Strings.tr("Localizable", "alley.error.empty.filter.message", fallback: "Try changing your filters or adding a new alley.")
        }
      }
    }
    public enum List {
      /// Add Alley
      public static let add = Strings.tr("Localizable", "alley.list.add", fallback: "Add Alley")
      /// Alleys
      public static let title = Strings.tr("Localizable", "alley.list.title", fallback: "Alleys")
    }
    public enum Properties {
      /// Material
      public static let material = Strings.tr("Localizable", "alley.properties.material", fallback: "Material")
      /// Mechanism
      public static let mechanism = Strings.tr("Localizable", "alley.properties.mechanism", fallback: "Mechanism")
      /// Pin Base
      public static let pinBase = Strings.tr("Localizable", "alley.properties.pinBase", fallback: "Pin Base")
      /// Pin Fall
      public static let pinFall = Strings.tr("Localizable", "alley.properties.pinFall", fallback: "Pin Fall")
      public enum Lanes {
        /// Manage
        public static let manage = Strings.tr("Localizable", "alley.properties.lanes.manage", fallback: "Manage")
        /// No lanes created
        public static let `none` = Strings.tr("Localizable", "alley.properties.lanes.none", fallback: "No lanes created")
      }
      public enum Material {
        /// Synthetic
        public static let synthetic = Strings.tr("Localizable", "alley.properties.material.synthetic", fallback: "Synthetic")
        /// Wood
        public static let wood = Strings.tr("Localizable", "alley.properties.material.wood", fallback: "Wood")
      }
      public enum Mechanism {
        /// Dedicated
        public static let dedicated = Strings.tr("Localizable", "alley.properties.mechanism.dedicated", fallback: "Dedicated")
        /// Interchangeable
        public static let interchangeable = Strings.tr("Localizable", "alley.properties.mechanism.interchangeable", fallback: "Interchangeable")
      }
      public enum PinBase {
        /// Black
        public static let black = Strings.tr("Localizable", "alley.properties.pinBase.black", fallback: "Black")
        /// White
        public static let white = Strings.tr("Localizable", "alley.properties.pinBase.white", fallback: "White")
      }
      public enum PinFall {
        /// Freefall
        public static let freefall = Strings.tr("Localizable", "alley.properties.pinFall.freefall", fallback: "Freefall")
        /// Strings
        public static let strings = Strings.tr("Localizable", "alley.properties.pinFall.strings", fallback: "Strings")
      }
    }
  }
  public enum App {
    public enum Tabs {
      /// Tabs
      public static let alley = Strings.tr("Localizable", "app.tabs.alley", fallback: "Alleys")
      /// Gear
      public static let gear = Strings.tr("Localizable", "app.tabs.gear", fallback: "Gear")
      /// Scoresheet
      public static let scoresheet = Strings.tr("Localizable", "app.tabs.scoresheet", fallback: "Scoresheet")
      /// Settings
      public static let settings = Strings.tr("Localizable", "app.tabs.settings", fallback: "Settings")
    }
  }
  public enum Ball {
    /// Ball %d
    public static func title(_ p1: Int) -> String {
      return Strings.tr("Localizable", "ball.title", p1, fallback: "Ball %d")
    }
    public enum Properties {
      /// Ball Rolled
      public static let ballRolled = Strings.tr("Localizable", "ball.properties.ballRolled", fallback: "Ball Rolled")
      /// Fouled?
      public static let fouled = Strings.tr("Localizable", "ball.properties.fouled", fallback: "Fouled?")
    }
  }
  public enum Bowler {
    /// Bowlers
    public static let title = Strings.tr("Localizable", "bowler.title", fallback: "Bowler")
    public enum Error {
      public enum Empty {
        /// You haven't added any bowlers yet. Try adding yourself to get started.
        public static let message = Strings.tr("Localizable", "bowler.error.empty.message", fallback: "You haven't added any bowlers yet. Try adding yourself to get started.")
        /// No bowlers found
        public static let title = Strings.tr("Localizable", "bowler.error.empty.title", fallback: "No bowlers found")
      }
    }
    public enum List {
      /// Add Bowler
      public static let add = Strings.tr("Localizable", "bowler.list.add", fallback: "Add Bowler")
      /// Bowlers
      public static let title = Strings.tr("Localizable", "bowler.list.title", fallback: "Bowlers")
      public enum Title {
        /// All Bowlers
        public static let all = Strings.tr("Localizable", "bowler.list.title.all", fallback: "All Bowlers")
      }
    }
  }
  public enum Editor {
    public enum Fields {
      /// Options
      public static let options = Strings.tr("Localizable", "editor.fields.options", fallback: "Options")
      public enum Details {
        /// Address
        public static let address = Strings.tr("Localizable", "editor.fields.details.address", fallback: "Address")
        /// Name
        public static let name = Strings.tr("Localizable", "editor.fields.details.name", fallback: "Name")
        /// Editor & Forms
        public static let title = Strings.tr("Localizable", "editor.fields.details.title", fallback: "Details")
      }
    }
  }
  public enum Error {
    /// We couldn't load your data.
    public static let loadingFailed = Strings.tr("Localizable", "error.loadingFailed", fallback: "We couldn't load your data.")
    public enum Generic {
      /// Errors
      public static let title = Strings.tr("Localizable", "error.generic.title", fallback: "Something went wrong!")
    }
  }
  public enum Form {
    public enum Prompt {
      /// Add %@
      public static func add(_ p1: Any) -> String {
        return Strings.tr("Localizable", "form.prompt.add", String(describing: p1), fallback: "Add %@")
      }
      /// Are you sure you want to delete %@?
      public static func delete(_ p1: Any) -> String {
        return Strings.tr("Localizable", "form.prompt.delete", String(describing: p1), fallback: "Are you sure you want to delete %@?")
      }
      /// Discard your changes?
      public static let discardChanges = Strings.tr("Localizable", "form.prompt.discardChanges", fallback: "Discard your changes?")
      /// Edit %@
      public static func edit(_ p1: Any) -> String {
        return Strings.tr("Localizable", "form.prompt.edit", String(describing: p1), fallback: "Edit %@")
      }
    }
  }
  public enum Frame {
    /// Frame %d
    public static func title(_ p1: Int) -> String {
      return Strings.tr("Localizable", "frame.title", p1, fallback: "Frame %d")
    }
  }
  public enum Game {
    /// Games
    public static func title(_ p1: Any) -> String {
      return Strings.tr("Localizable", "game.title", String(describing: p1), fallback: "Game %@")
    }
  }
  public enum Gear {
    /// Gear
    public static let title = Strings.tr("Localizable", "gear.title", fallback: "Gear")
    public enum Error {
      public enum Empty {
        /// You haven't added any gear yet. Track usage stats for your shoes, balls, or more.
        public static let message = Strings.tr("Localizable", "gear.error.empty.message", fallback: "You haven't added any gear yet. Track usage stats for your shoes, balls, or more.")
        /// No gear found
        public static let title = Strings.tr("Localizable", "gear.error.empty.title", fallback: "No gear found")
      }
    }
    public enum List {
      /// Add Gear
      public static let add = Strings.tr("Localizable", "gear.list.add", fallback: "Add Gear")
    }
    public enum Properties {
      /// Kind
      public static let kind = Strings.tr("Localizable", "gear.properties.kind", fallback: "Kind")
      /// Owner
      public static let owner = Strings.tr("Localizable", "gear.properties.owner", fallback: "Owner")
      public enum Kind {
        /// Ball
        public static let bowlingBall = Strings.tr("Localizable", "gear.properties.kind.bowlingBall", fallback: "Ball")
        /// Shoes
        public static let shoes = Strings.tr("Localizable", "gear.properties.kind.shoes", fallback: "Shoes")
        /// Towel
        public static let towel = Strings.tr("Localizable", "gear.properties.kind.towel", fallback: "Towel")
      }
    }
  }
  public enum Lane {
    /// Lanes
    public static let title = Strings.tr("Localizable", "lane.title", fallback: "Lane")
    public enum Editor {
      public enum Fields {
        /// Plural format key: "%#@lanes@"
        public static func addLanes(_ p1: Int) -> String {
          return Strings.tr("Localizable", "lane.editor.fields.addLanes", p1, fallback: "Plural format key: \"%#@lanes@\"")
        }
        public enum IsAgainstWall {
          /// Generally, only the first and last lanes in an alley should be marked as 'against the wall'. For example, the first lane, but not the second lane, even if you usually bowl on both of them in one game.
          public static let help = Strings.tr("Localizable", "lane.editor.fields.isAgainstWall.help", fallback: "Generally, only the first and last lanes in an alley should be marked as 'against the wall'. For example, the first lane, but not the second lane, even if you usually bowl on both of them in one game.")
        }
      }
    }
    public enum List {
      /// Add Lane
      public static let add = Strings.tr("Localizable", "lane.list.add", fallback: "Add Lane")
      /// Add Muliple Lanes
      public static let addMultiple = Strings.tr("Localizable", "lane.list.addMultiple", fallback: "Add Muliple Lanes")
      /// Lanes
      public static let title = Strings.tr("Localizable", "lane.list.title", fallback: "Lanes")
    }
    public enum Properties {
      /// Against Wall?
      public static let isAgainstWall = Strings.tr("Localizable", "lane.properties.isAgainstWall", fallback: "Against Wall?")
      /// Lane
      public static let label = Strings.tr("Localizable", "lane.properties.label", fallback: "Lane")
    }
  }
  public enum League {
    /// Leagues
    public static let title = Strings.tr("Localizable", "league.title", fallback: "League")
    public enum Editor {
      public enum Fields {
        public enum AdditionalPinfall {
          /// If you're starting recording partway through the season, you can add missing pinfall here to ensure your average in the app matches the average provided by your league.
          public static let help = Strings.tr("Localizable", "league.editor.fields.additionalPinfall.help", fallback: "If you're starting recording partway through the season, you can add missing pinfall here to ensure your average in the app matches the average provided by your league.")
          /// Include additional pinfall?
          public static let title = Strings.tr("Localizable", "league.editor.fields.additionalPinfall.title", fallback: "Include additional pinfall?")
        }
        public enum Alley {
          /// This is where you'll usually bowl this league. You can always change it for specific series later.
          public static let help = Strings.tr("Localizable", "league.editor.fields.alley.help", fallback: "This is where you'll usually bowl this league. You can always change it for specific series later.")
        }
        public enum GamesPerSeries {
          /// Always ask me
          public static let alwaysAskMe = Strings.tr("Localizable", "league.editor.fields.gamesPerSeries.alwaysAskMe", fallback: "Always ask me")
          /// Constant
          public static let constant = Strings.tr("Localizable", "league.editor.fields.gamesPerSeries.constant", fallback: "Constant")
        }
        public enum NumberOfGames {
          /// Choose '%@' if you always play the same number of games each series, or '%@' to choose the number of games each time you bowl.
          public static func help(_ p1: Any, _ p2: Any) -> String {
            return Strings.tr("Localizable", "league.editor.fields.numberOfGames.help", String(describing: p1), String(describing: p2), fallback: "Choose '%@' if you always play the same number of games each series, or '%@' to choose the number of games each time you bowl.")
          }
        }
        public enum Recurrence {
          /// Choose '%@' for leagues that happen semi-frequently, such as once a week, or choose '%@' for tournaments and one-off events.
          public static func help(_ p1: Any, _ p2: Any) -> String {
            return Strings.tr("Localizable", "league.editor.fields.recurrence.help", String(describing: p1), String(describing: p2), fallback: "Choose '%@' for leagues that happen semi-frequently, such as once a week, or choose '%@' for tournaments and one-off events.")
          }
        }
      }
    }
    public enum Error {
      public enum Empty {
        /// You haven't added any leagues or events yet. Track your progress week over week for each league you're in. See how you measure up in tournaments with events.
        public static let message = Strings.tr("Localizable", "league.error.empty.message", fallback: "You haven't added any leagues or events yet. Track your progress week over week for each league you're in. See how you measure up in tournaments with events.")
        /// No leagues found
        public static let title = Strings.tr("Localizable", "league.error.empty.title", fallback: "No leagues found")
      }
    }
    public enum List {
      /// Add League
      public static let add = Strings.tr("Localizable", "league.list.add", fallback: "Add League")
      public enum Title {
        /// All Leagues
        public static let all = Strings.tr("Localizable", "league.list.title.all", fallback: "All Leagues")
      }
    }
    public enum Properties {
      /// Additional Games
      public static let additionalGames = Strings.tr("Localizable", "league.properties.additionalGames", fallback: "Additional Games")
      /// Additional Pinfall
      public static let additionalPinfall = Strings.tr("Localizable", "league.properties.additionalPinfall", fallback: "Additional Pinfall")
      /// Bowling Alley
      public static let alley = Strings.tr("Localizable", "league.properties.alley", fallback: "Bowling Alley")
      /// Number of games
      public static let numberOfGames = Strings.tr("Localizable", "league.properties.numberOfGames", fallback: "Number of games")
      /// Repeat?
      public static let recurrence = Strings.tr("Localizable", "league.properties.recurrence", fallback: "Repeat?")
      public enum Recurrence {
        /// Never
        public static let never = Strings.tr("Localizable", "league.properties.recurrence.never", fallback: "Never")
        /// Repeats
        public static let repeats = Strings.tr("Localizable", "league.properties.recurrence.repeats", fallback: "Repeats")
      }
    }
  }
  public enum Opponent {
    /// Opponents
    public static let title = Strings.tr("Localizable", "opponent.title", fallback: "Opponent")
    public enum Error {
      public enum Empty {
        /// Playing against another bowler, or in a match play setting? Keep track of your record by adding your opponents.
        public static let message = Strings.tr("Localizable", "opponent.error.empty.message", fallback: "Playing against another bowler, or in a match play setting? Keep track of your record by adding your opponents.")
        /// No opponents found
        public static let title = Strings.tr("Localizable", "opponent.error.empty.title", fallback: "No opponents found")
      }
    }
    public enum List {
      /// Add Opponent
      public static let add = Strings.tr("Localizable", "opponent.list.add", fallback: "Add Opponent")
      /// Opponents
      public static let title = Strings.tr("Localizable", "opponent.list.title", fallback: "Opponents")
    }
  }
  public enum Ordering {
    /// Ordering
    public static let alphabetical = Strings.tr("Localizable", "ordering.alphabetical", fallback: "Alphabetical")
    /// Most Recently Used
    public static let mostRecentlyUsed = Strings.tr("Localizable", "ordering.mostRecentlyUsed", fallback: "Most Recently Used")
  }
  public enum Picker {
    /// Picking %@
    public static func title(_ p1: Any) -> String {
      return Strings.tr("Localizable", "picker.title", String(describing: p1), fallback: "Picking %@")
    }
    public enum Empty {
      /// No items found
      public static let title = Strings.tr("Localizable", "picker.empty.title", fallback: "No items found")
    }
  }
  public enum Series {
    /// Series
    public static let title = Strings.tr("Localizable", "series.title", fallback: "Series")
    public enum Editor {
      public enum Fields {
        public enum ExcludeFromStatistics {
          /// Pre-bowls are automatically excluded from all statistics.
          public static let excludedWhenPreBowl = Strings.tr("Localizable", "series.editor.fields.excludeFromStatistics.excludedWhenPreBowl", fallback: "Pre-bowls are automatically excluded from all statistics.")
          /// You can choose to exclude this series and all of its games from all statistics. They will still appear in the app, but won't affect your average or other statistics.
          public static let help = Strings.tr("Localizable", "series.editor.fields.excludeFromStatistics.help", fallback: "You can choose to exclude this series and all of its games from all statistics. They will still appear in the app, but won't affect your average or other statistics.")
          /// Exclude from all statistics?
          public static let label = Strings.tr("Localizable", "series.editor.fields.excludeFromStatistics.label", fallback: "Exclude from all statistics?")
          /// Statistics
          public static let title = Strings.tr("Localizable", "series.editor.fields.excludeFromStatistics.title", fallback: "Statistics")
        }
        public enum PreBowl {
          /// Pre-bowls are excluded from statistics until you use them. You can easily find your recorded pre-bowls in the series list, and modify their date for when you plan to use them.
          public static let help = Strings.tr("Localizable", "series.editor.fields.preBowl.help", fallback: "Pre-bowls are excluded from statistics until you use them. You can easily find your recorded pre-bowls in the series list, and modify their date for when you plan to use them.")
          /// This is a pre-bowl
          public static let label = Strings.tr("Localizable", "series.editor.fields.preBowl.label", fallback: "This is a pre-bowl")
          /// Pre-Bowl
          public static let title = Strings.tr("Localizable", "series.editor.fields.preBowl.title", fallback: "Pre-Bowl")
        }
      }
    }
    public enum Error {
      public enum Empty {
        /// You haven't added a series yet. Create a new series every time you bowl to see your stats mapped accurately over time.
        public static let message = Strings.tr("Localizable", "series.error.empty.message", fallback: "You haven't added a series yet. Create a new series every time you bowl to see your stats mapped accurately over time.")
        /// No series found
        public static let title = Strings.tr("Localizable", "series.error.empty.title", fallback: "No series found")
      }
      public enum FailedToCreate {
        /// We couldn't create a new series
        public static let message = Strings.tr("Localizable", "series.error.failedToCreate.message", fallback: "We couldn't create a new series")
        /// Failed to create series
        public static let title = Strings.tr("Localizable", "series.error.failedToCreate.title", fallback: "Failed to create series")
      }
    }
    public enum List {
      /// Add Series
      public static let add = Strings.tr("Localizable", "series.list.add", fallback: "Add Series")
      public enum Title {
        /// All Series
        public static let all = Strings.tr("Localizable", "series.list.title.all", fallback: "All Series")
      }
    }
    public enum Properties {
      /// Bowling Alley
      public static let alley = Strings.tr("Localizable", "series.properties.alley", fallback: "Bowling Alley")
      /// Date
      public static let date = Strings.tr("Localizable", "series.properties.date", fallback: "Date")
      /// Number of games
      public static let numberOfGames = Strings.tr("Localizable", "series.properties.numberOfGames", fallback: "Number of games")
      public enum ExcludeFromStatistics {
        /// Exclude from statistics
        public static let exclude = Strings.tr("Localizable", "series.properties.excludeFromStatistics.exclude", fallback: "Exclude from statistics")
        /// Include in statistics
        public static let include = Strings.tr("Localizable", "series.properties.excludeFromStatistics.include", fallback: "Include in statistics")
      }
      public enum PreBowl {
        /// Pre-Bowl
        public static let preBowl = Strings.tr("Localizable", "series.properties.preBowl.preBowl", fallback: "Pre-Bowl")
        /// Regular
        public static let regular = Strings.tr("Localizable", "series.properties.preBowl.regular", fallback: "Regular")
      }
    }
  }
  public enum Settings {
    /// Settings
    public static let title = Strings.tr("Localizable", "settings.title", fallback: "Settings")
    public enum Acknowledgements {
      /// Settings
      public static let title = Strings.tr("Localizable", "settings.acknowledgements.title", fallback: "Acknowledgements")
    }
    public enum AppInfo {
      /// %@ (%@)
      public static func appVersion(_ p1: Any, _ p2: Any) -> String {
        return Strings.tr("Localizable", "settings.appInfo.appVersion", String(describing: p1), String(describing: p2), fallback: "%@ (%@)")
      }
      /// ©2023, Joseph Roque
      public static let copyright = Strings.tr("Localizable", "settings.appInfo.copyright", fallback: "©2023, Joseph Roque")
      /// App Info
      public static let title = Strings.tr("Localizable", "settings.appInfo.title", fallback: "App Info")
      /// Version
      public static let version = Strings.tr("Localizable", "settings.appInfo.version", fallback: "Version")
    }
    public enum Developer {
      /// https://runcode.blog
      public static let blog = Strings.tr("Localizable", "settings.developer.blog", fallback: "https://runcode.blog")
      /// Blog
      public static let blogTitle = Strings.tr("Localizable", "settings.developer.blogTitle", fallback: "Blog")
      /// Contact
      public static let contact = Strings.tr("Localizable", "settings.developer.contact", fallback: "Contact")
      /// Learn More
      public static let learnMore = Strings.tr("Localizable", "settings.developer.learnMore", fallback: "Learn More")
      /// @autoreleasefool@iosdev.space
      public static let mastodonHandle = Strings.tr("Localizable", "settings.developer.mastodonHandle", fallback: "@autoreleasefool@iosdev.space")
      /// https://iosdev.space/@autoreleasefool
      public static let mastodonUrl = Strings.tr("Localizable", "settings.developer.mastodonUrl", fallback: "https://iosdev.space/@autoreleasefool")
      /// Joseph Roque
      public static let name = Strings.tr("Localizable", "settings.developer.name", fallback: "Joseph Roque")
      /// Developer
      public static let title = Strings.tr("Localizable", "settings.developer.title", fallback: "Developer")
      /// @autoreleasefool
      public static let twitterHandle = Strings.tr("Localizable", "settings.developer.twitterHandle", fallback: "@autoreleasefool")
      /// https://twitter.com/@autoreleasefool
      public static let twitterUrl = Strings.tr("Localizable", "settings.developer.twitterUrl", fallback: "https://twitter.com/@autoreleasefool")
      /// https://josephroque.dev
      public static let website = Strings.tr("Localizable", "settings.developer.website", fallback: "https://josephroque.dev")
      /// Website
      public static let websiteTitle = Strings.tr("Localizable", "settings.developer.websiteTitle", fallback: "Website")
    }
    public enum FeatureFlags {
      /// Features
      public static let title = Strings.tr("Localizable", "settings.featureFlags.title", fallback: "Features")
    }
    public enum Help {
      /// Acknowledgements
      public static let acknowledgements = Strings.tr("Localizable", "settings.help.acknowledgements", fallback: "Acknowledgements")
      /// Developer
      public static let developer = Strings.tr("Localizable", "settings.help.developer", fallback: "Developer")
      /// Report Bug
      public static let reportBug = Strings.tr("Localizable", "settings.help.reportBug", fallback: "Report Bug")
      /// Send Feedback
      public static let sendFeedback = Strings.tr("Localizable", "settings.help.sendFeedback", fallback: "Send Feedback")
      /// Help
      public static let title = Strings.tr("Localizable", "settings.help.title", fallback: "Help")
      /// View Source
      public static let viewSource = Strings.tr("Localizable", "settings.help.viewSource", fallback: "View Source")
      public enum Development {
        /// %@ is an open source project you can aid in the development of by using the links above
        public static func help(_ p1: Any) -> String {
          return Strings.tr("Localizable", "settings.help.development.help", String(describing: p1), fallback: "%@ is an open source project you can aid in the development of by using the links above")
        }
        /// Development
        public static let title = Strings.tr("Localizable", "settings.help.development.title", fallback: "Development")
      }
    }
  }
  public enum SortOrder {
    /// Sort Order
    public static let title = Strings.tr("Localizable", "sortOrder.title", fallback: "Sort Order")
  }
  public enum Statistics {
    public enum Placeholder {
      /// Tap here to configure the stats you want to see
      public static let message = Strings.tr("Localizable", "statistics.placeholder.message", fallback: "Tap here to configure the stats you want to see")
      /// Statistics
      public static let title = Strings.tr("Localizable", "statistics.placeholder.title", fallback: "Statistics at a glance")
    }
  }
  public enum Team {
    /// Teams
    public static let title = Strings.tr("Localizable", "team.title", fallback: "Team")
    public enum Error {
      public enum Empty {
        /// You haven't create a team yet.
        public static let message = Strings.tr("Localizable", "team.error.empty.message", fallback: "You haven't create a team yet.")
        /// No teams found
        public static let title = Strings.tr("Localizable", "team.error.empty.title", fallback: "No teams found")
      }
    }
    public enum List {
      /// Add Team
      public static let add = Strings.tr("Localizable", "team.list.add", fallback: "Add Team")
      /// Teams
      public static let title = Strings.tr("Localizable", "team.list.title", fallback: "Teams")
      public enum Title {
        /// All Teams
        public static let all = Strings.tr("Localizable", "team.list.title.all", fallback: "All Teams")
      }
    }
    public enum Properties {
      public enum Bowlers {
        /// No bowlers added
        public static let `none` = Strings.tr("Localizable", "team.properties.bowlers.none", fallback: "No bowlers added")
        /// Members
        public static let title = Strings.tr("Localizable", "team.properties.bowlers.title", fallback: "Members")
      }
    }
  }
}
// swiftlint:enable explicit_type_interface function_parameter_count identifier_name line_length
// swiftlint:enable nesting type_body_length type_name vertical_whitespace_opening_braces

// MARK: - Implementation Details

extension Strings {
  private static func tr(_ table: String, _ key: String, _ args: CVarArg..., fallback value: String) -> String {
    let format = Bundle.module.localizedString(forKey: key, value: value, table: table)
    return String(format: format, locale: Locale.current, arguments: args)
  }
}
