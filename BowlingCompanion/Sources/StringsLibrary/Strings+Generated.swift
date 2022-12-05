// swiftlint:disable all
// Generated using SwiftGen — https://github.com/SwiftGen/SwiftGen

import Foundation

// swiftlint:disable superfluous_disable_command file_length implicit_return prefer_self_in_static_references

// MARK: - Strings

// swiftlint:disable explicit_type_interface function_parameter_count identifier_name line_length
// swiftlint:disable nesting type_body_length type_name vertical_whitespace_opening_braces
public enum Strings {
  public enum Actions {
    /// Add
    public static let add = Strings.tr("Localizable", "actions.add", fallback: "Add")
    /// Delete
    public static let delete = Strings.tr("Localizable", "actions.delete", fallback: "Delete")
    /// Edit
    public static let edit = Strings.tr("Localizable", "actions.edit", fallback: "Edit")
  }
  public enum Alleys {
    public enum Editor {
      public enum Fields {
        public enum Details {
          /// Address
          public static let address = Strings.tr("Localizable", "alleys.editor.fields.details.address", fallback: "Address")
          /// Name
          public static let name = Strings.tr("Localizable", "alleys.editor.fields.details.name", fallback: "Name")
          /// Details
          public static let title = Strings.tr("Localizable", "alleys.editor.fields.details.title", fallback: "Details")
        }
        public enum Material {
          /// To help tell the difference, wooden lanes tend to show some wear, while synthetic lanes are usually harder and smoother.
          public static let footer = Strings.tr("Localizable", "alleys.editor.fields.material.footer", fallback: "To help tell the difference, wooden lanes tend to show some wear, while synthetic lanes are usually harder and smoother.")
          /// Material
          public static let title = Strings.tr("Localizable", "alleys.editor.fields.material.title", fallback: "Material")
        }
        public enum Mechanism {
          /// Are the lanes interchangeable between multiple types of bowling (5-Pin and 10-Pin), or do they only support one kind?
          public static let footer = Strings.tr("Localizable", "alleys.editor.fields.mechanism.footer", fallback: "Are the lanes interchangeable between multiple types of bowling (5-Pin and 10-Pin), or do they only support one kind?")
          /// Mechanism
          public static let title = Strings.tr("Localizable", "alleys.editor.fields.mechanism.title", fallback: "Mechanism")
        }
        public enum PinBase {
          /// What kind of base do the pins have?
          public static let footer = Strings.tr("Localizable", "alleys.editor.fields.pinBase.footer", fallback: "What kind of base do the pins have?")
          /// Pin Base
          public static let title = Strings.tr("Localizable", "alleys.editor.fields.pinBase.title", fallback: "Pin Base")
        }
        public enum PinFall {
          /// Look at how the pins are set up. Do you notice the pins are pushed off the lane after each ball, or are they attached to strings and pulled up?
          public static let footer = Strings.tr("Localizable", "alleys.editor.fields.pinFall.footer", fallback: "Look at how the pins are set up. Do you notice the pins are pushed off the lane after each ball, or are they attached to strings and pulled up?")
          /// Pin Fall
          public static let title = Strings.tr("Localizable", "alleys.editor.fields.pinFall.title", fallback: "Pin Fall")
        }
      }
      public enum Help {
        /// Not sure about any of the settings? Ask a staff member! They'll probably be happy to help
        public static let askAStaffMember = Strings.tr("Localizable", "alleys.editor.help.askAStaffMember", fallback: "Not sure about any of the settings? Ask a staff member! They'll probably be happy to help")
      }
    }
    public enum Errors {
      public enum Empty {
        /// You haven't added any alleys yet.
        public static let message = Strings.tr("Localizable", "alleys.errors.empty.message", fallback: "You haven't added any alleys yet.")
        /// No alleys found
        public static let title = Strings.tr("Localizable", "alleys.errors.empty.title", fallback: "No alleys found")
        public enum Filter {
          /// Try changing your filters or adding a new alley.
          public static let message = Strings.tr("Localizable", "alleys.errors.empty.filter.message", fallback: "Try changing your filters or adding a new alley.")
        }
      }
    }
    public enum Filter {
      /// Material
      public static let material = Strings.tr("Localizable", "alleys.filter.material", fallback: "Material")
      /// Mechanism
      public static let mechanism = Strings.tr("Localizable", "alleys.filter.mechanism", fallback: "Mechanism")
      /// Pin Base
      public static let pinBase = Strings.tr("Localizable", "alleys.filter.pinBase", fallback: "Pin Base")
      /// Pin Fall
      public static let pinFall = Strings.tr("Localizable", "alleys.filter.pinFall", fallback: "Pin Fall")
    }
    public enum List {
      /// Add Alley
      public static let add = Strings.tr("Localizable", "alleys.list.add", fallback: "Add Alley")
      /// Alleys
      public static let title = Strings.tr("Localizable", "alleys.list.title", fallback: "Alleys")
      public enum Delete {
        /// Delete
        public static let action = Strings.tr("Localizable", "alleys.list.delete.action", fallback: "Delete")
        /// Cancel
        public static let cancel = Strings.tr("Localizable", "alleys.list.delete.cancel", fallback: "Cancel")
        /// Are you sure you want to delete %@?
        public static func title(_ p1: Any) -> String {
          return Strings.tr("Localizable", "alleys.list.delete.title", String(describing: p1), fallback: "Are you sure you want to delete %@?")
        }
      }
    }
    public enum Material {
      /// Synthetic
      public static let synthetic = Strings.tr("Localizable", "alleys.material.synthetic", fallback: "Synthetic")
      /// Unknown
      public static let unknown = Strings.tr("Localizable", "alleys.material.unknown", fallback: "Unknown")
      /// Wood
      public static let wood = Strings.tr("Localizable", "alleys.material.wood", fallback: "Wood")
    }
    public enum Mechanism {
      /// Dedicated
      public static let dedicated = Strings.tr("Localizable", "alleys.mechanism.dedicated", fallback: "Dedicated")
      /// Interchangeable
      public static let interchangeable = Strings.tr("Localizable", "alleys.mechanism.interchangeable", fallback: "Interchangeable")
      /// Unknown
      public static let unknown = Strings.tr("Localizable", "alleys.mechanism.unknown", fallback: "Unknown")
    }
    public enum Model {
      /// Alley
      public static let name = Strings.tr("Localizable", "alleys.model.name", fallback: "Alley")
    }
    public enum Picker {
      public enum Errors {
        /// Dismiss
        public static let dismiss = Strings.tr("Localizable", "alleys.picker.errors.dismiss", fallback: "Dismiss")
      }
    }
    public enum PinBase {
      /// Black
      public static let black = Strings.tr("Localizable", "alleys.pinBase.black", fallback: "Black")
      /// Other
      public static let other = Strings.tr("Localizable", "alleys.pinBase.other", fallback: "Other")
      /// Unknown
      public static let unknown = Strings.tr("Localizable", "alleys.pinBase.unknown", fallback: "Unknown")
      /// White
      public static let white = Strings.tr("Localizable", "alleys.pinBase.white", fallback: "White")
    }
    public enum PinFall {
      /// Freefall
      public static let freefall = Strings.tr("Localizable", "alleys.pinFall.freefall", fallback: "Freefall")
      /// Strings
      public static let strings = Strings.tr("Localizable", "alleys.pinFall.strings", fallback: "Strings")
      /// Unknown
      public static let unknown = Strings.tr("Localizable", "alleys.pinFall.unknown", fallback: "Unknown")
    }
  }
  public enum App {
    public enum Tabs {
      /// Alleys
      public static let alley = Strings.tr("Localizable", "app.tabs.alley", fallback: "Alleys")
      /// Gear
      public static let gear = Strings.tr("Localizable", "app.tabs.gear", fallback: "Gear")
      /// Scoresheet
      public static let scoresheet = Strings.tr("Localizable", "app.tabs.scoresheet", fallback: "Scoresheet")
      /// Settings
      public static let settings = Strings.tr("Localizable", "app.tabs.settings", fallback: "Settings")
    }
  }
  public enum Bowlers {
    public enum Editor {
      public enum Fields {
        public enum Details {
          /// Name
          public static let name = Strings.tr("Localizable", "bowlers.editor.fields.details.name", fallback: "Name")
          /// Details
          public static let title = Strings.tr("Localizable", "bowlers.editor.fields.details.title", fallback: "Details")
        }
      }
    }
    public enum Errors {
      public enum Empty {
        /// You haven't added any bowlers yet. Try adding yourself to get started.
        public static let message = Strings.tr("Localizable", "bowlers.errors.empty.message", fallback: "You haven't added any bowlers yet. Try adding yourself to get started.")
        /// No bowlers found
        public static let title = Strings.tr("Localizable", "bowlers.errors.empty.title", fallback: "No bowlers found")
      }
    }
    public enum List {
      /// Add Bowler
      public static let add = Strings.tr("Localizable", "bowlers.list.add", fallback: "Add Bowler")
      /// All Bowlers
      public static let sectionTitle = Strings.tr("Localizable", "bowlers.list.sectionTitle", fallback: "All Bowlers")
      /// Bowlers
      public static let title = Strings.tr("Localizable", "bowlers.list.title", fallback: "Bowlers")
      public enum Delete {
        /// Delete
        public static let action = Strings.tr("Localizable", "bowlers.list.delete.action", fallback: "Delete")
        /// Cancel
        public static let cancel = Strings.tr("Localizable", "bowlers.list.delete.cancel", fallback: "Cancel")
        /// Are you sure you want to delete %@?
        public static func title(_ p1: Any) -> String {
          return Strings.tr("Localizable", "bowlers.list.delete.title", String(describing: p1), fallback: "Are you sure you want to delete %@?")
        }
      }
    }
    public enum Model {
      /// Bowler
      public static let name = Strings.tr("Localizable", "bowlers.model.name", fallback: "Bowler")
    }
  }
  public enum Errors {
    public enum DeleteFailed {
      /// Reload
      public static let action = Strings.tr("Localizable", "errors.deleteFailed.action", fallback: "Reload")
      /// Something went wrong!
      public static let title = Strings.tr("Localizable", "errors.deleteFailed.title", fallback: "Something went wrong!")
    }
    public enum Generic {
      /// Something went wrong!
      public static let title = Strings.tr("Localizable", "errors.generic.title", fallback: "Something went wrong!")
      /// Try again
      public static let tryAgain = Strings.tr("Localizable", "errors.generic.tryAgain", fallback: "Try again")
    }
    public enum LoadingFailed {
      /// We couldn't load your data.
      public static let message = Strings.tr("Localizable", "errors.loadingFailed.message", fallback: "We couldn't load your data.")
    }
  }
  public enum Form {
    /// Cancel
    public static let cancel = Strings.tr("Localizable", "form.cancel", fallback: "Cancel")
    /// Options
    public static let options = Strings.tr("Localizable", "form.options", fallback: "Options")
    /// Save
    public static let save = Strings.tr("Localizable", "form.save", fallback: "Save")
    public enum Add {
      /// Add %@
      public static func title(_ p1: Any) -> String {
        return Strings.tr("Localizable", "form.add.title", String(describing: p1), fallback: "Add %@")
      }
    }
    public enum Delete {
      /// Delete
      public static let action = Strings.tr("Localizable", "form.delete.action", fallback: "Delete")
      /// Are you sure you want to delete %@?
      public static func title(_ p1: Any) -> String {
        return Strings.tr("Localizable", "form.delete.title", String(describing: p1), fallback: "Are you sure you want to delete %@?")
      }
    }
    public enum Discard {
      /// Discard
      public static let action = Strings.tr("Localizable", "form.discard.action", fallback: "Discard")
      /// Discard your changes?
      public static let title = Strings.tr("Localizable", "form.discard.title", fallback: "Discard your changes?")
    }
    public enum Edit {
      /// Edit %@
      public static func title(_ p1: Any) -> String {
        return Strings.tr("Localizable", "form.edit.title", String(describing: p1), fallback: "Edit %@")
      }
    }
  }
  public enum Game {
    public enum Editor {
      /// Game %d
      public static func title(_ p1: Int) -> String {
        return Strings.tr("Localizable", "game.editor.title", p1, fallback: "Game %d")
      }
      public enum BallDetails {
        /// Ball %d
        public static func ball(_ p1: Int) -> String {
          return Strings.tr("Localizable", "game.editor.ballDetails.ball", p1, fallback: "Ball %d")
        }
        /// Frame %d
        public static func frame(_ p1: Int) -> String {
          return Strings.tr("Localizable", "game.editor.ballDetails.frame", p1, fallback: "Frame %d")
        }
        public enum BallRolled {
          /// None
          public static let `none` = Strings.tr("Localizable", "game.editor.ballDetails.ballRolled.none", fallback: "None")
          /// Ball Rolled
          public static let title = Strings.tr("Localizable", "game.editor.ballDetails.ballRolled.title", fallback: "Ball Rolled")
        }
        public enum Fouled {
          /// No
          public static let no = Strings.tr("Localizable", "game.editor.ballDetails.fouled.no", fallback: "No")
          /// Fouled?
          public static let title = Strings.tr("Localizable", "game.editor.ballDetails.fouled.title", fallback: "Fouled?")
          /// Yes
          public static let yes = Strings.tr("Localizable", "game.editor.ballDetails.fouled.yes", fallback: "Yes")
        }
      }
    }
  }
  public enum Gear {
    public enum Editor {
      public enum Fields {
        public enum Details {
          /// Kind
          public static let kind = Strings.tr("Localizable", "gear.editor.fields.details.kind", fallback: "Kind")
          /// Name
          public static let name = Strings.tr("Localizable", "gear.editor.fields.details.name", fallback: "Name")
          /// Details
          public static let title = Strings.tr("Localizable", "gear.editor.fields.details.title", fallback: "Details")
        }
        public enum Owner {
          /// Owner
          public static let title = Strings.tr("Localizable", "gear.editor.fields.owner.title", fallback: "Owner")
          public enum Bowler {
            /// None
            public static let `none` = Strings.tr("Localizable", "gear.editor.fields.owner.bowler.none", fallback: "None")
            /// Bowler
            public static let title = Strings.tr("Localizable", "gear.editor.fields.owner.bowler.title", fallback: "Bowler")
          }
        }
      }
    }
    public enum Errors {
      public enum Empty {
        /// You haven't added any gear yet. Track usage stats for your shoes, balls, or more.
        public static let message = Strings.tr("Localizable", "gear.errors.empty.message", fallback: "You haven't added any gear yet. Track usage stats for your shoes, balls, or more.")
        /// No gear found
        public static let title = Strings.tr("Localizable", "gear.errors.empty.title", fallback: "No gear found")
      }
    }
    public enum Kind {
      /// Ball
      public static let bowlingBall = Strings.tr("Localizable", "gear.kind.bowlingBall", fallback: "Ball")
      /// Other
      public static let other = Strings.tr("Localizable", "gear.kind.other", fallback: "Other")
      /// Shoes
      public static let shoes = Strings.tr("Localizable", "gear.kind.shoes", fallback: "Shoes")
      /// Towel
      public static let towel = Strings.tr("Localizable", "gear.kind.towel", fallback: "Towel")
    }
    public enum List {
      /// Add Gear
      public static let add = Strings.tr("Localizable", "gear.list.add", fallback: "Add Gear")
      /// Gear
      public static let title = Strings.tr("Localizable", "gear.list.title", fallback: "Gear")
    }
    public enum Model {
      /// Gear
      public static let name = Strings.tr("Localizable", "gear.model.name", fallback: "Gear")
    }
  }
  public enum Lanes {
    public enum Editor {
      /// Alley Lanes
      public static let title = Strings.tr("Localizable", "lanes.editor.title", fallback: "Alley Lanes")
      public enum Delete {
        /// Delete
        public static let action = Strings.tr("Localizable", "lanes.editor.delete.action", fallback: "Delete")
        /// Cancel
        public static let cancel = Strings.tr("Localizable", "lanes.editor.delete.cancel", fallback: "Cancel")
        /// Are you sure you want to delete %@?
        public static func title(_ p1: Any) -> String {
          return Strings.tr("Localizable", "lanes.editor.delete.title", String(describing: p1), fallback: "Are you sure you want to delete %@?")
        }
      }
      public enum Fields {
        /// Lane
        public static let label = Strings.tr("Localizable", "lanes.editor.fields.label", fallback: "Lane")
        public enum IsAgainstWall {
          /// Generally, only the first and last lanes in an alley should be marked as 'against the wall'. For example, the first lane, but not the second lane, even if you usually bowl on both of them in one game.
          public static let help = Strings.tr("Localizable", "lanes.editor.fields.isAgainstWall.help", fallback: "Generally, only the first and last lanes in an alley should be marked as 'against the wall'. For example, the first lane, but not the second lane, even if you usually bowl on both of them in one game.")
          /// Against Wall?
          public static let title = Strings.tr("Localizable", "lanes.editor.fields.isAgainstWall.title", fallback: "Against Wall?")
        }
      }
    }
  }
  public enum Leagues {
    public enum Editor {
      public enum Fields {
        public enum AdditionalPinfall {
          /// Additional Games
          public static let games = Strings.tr("Localizable", "leagues.editor.fields.additionalPinfall.games", fallback: "Additional Games")
          /// If you're starting recording partway through the season, you can add missing pinfall here to ensure your average in the app matches the average provided by your league.
          public static let help = Strings.tr("Localizable", "leagues.editor.fields.additionalPinfall.help", fallback: "If you're starting recording partway through the season, you can add missing pinfall here to ensure your average in the app matches the average provided by your league.")
          /// Additional Pinfall
          public static let pinfall = Strings.tr("Localizable", "leagues.editor.fields.additionalPinfall.pinfall", fallback: "Additional Pinfall")
          /// Include additional pinfall?
          public static let title = Strings.tr("Localizable", "leagues.editor.fields.additionalPinfall.title", fallback: "Include additional pinfall?")
        }
        public enum Details {
          /// Name
          public static let name = Strings.tr("Localizable", "leagues.editor.fields.details.name", fallback: "Name")
          /// Details
          public static let title = Strings.tr("Localizable", "leagues.editor.fields.details.title", fallback: "Details")
          public enum BowlingAlley {
            /// This is where you'll usually bowl this league. You can always change it for specific series later.
            public static let help = Strings.tr("Localizable", "leagues.editor.fields.details.bowlingAlley.help", fallback: "This is where you'll usually bowl this league. You can always change it for specific series later.")
            /// None
            public static let `none` = Strings.tr("Localizable", "leagues.editor.fields.details.bowlingAlley.none", fallback: "None")
            /// Bowling Alley
            public static let title = Strings.tr("Localizable", "leagues.editor.fields.details.bowlingAlley.title", fallback: "Bowling Alley")
          }
        }
        public enum GamesPerSeries {
          /// Always ask me
          public static let alwaysAskMe = Strings.tr("Localizable", "leagues.editor.fields.gamesPerSeries.alwaysAskMe", fallback: "Always ask me")
          /// Constant
          public static let constant = Strings.tr("Localizable", "leagues.editor.fields.gamesPerSeries.constant", fallback: "Constant")
        }
        public enum NumberOfGames {
          /// Choose '%@' if you always play the same number of games each series, or '%@' to choose the number of games each time you bowl.
          public static func help(_ p1: Any, _ p2: Any) -> String {
            return Strings.tr("Localizable", "leagues.editor.fields.numberOfGames.help", String(describing: p1), String(describing: p2), fallback: "Choose '%@' if you always play the same number of games each series, or '%@' to choose the number of games each time you bowl.")
          }
          /// Number of games
          public static let title = Strings.tr("Localizable", "leagues.editor.fields.numberOfGames.title", fallback: "Number of games")
        }
        public enum Recurrence {
          /// Choose '%@' for leagues that happen semi-frequently, such as once a week, or choose '%@' for tournaments and one-off events.
          public static func help(_ p1: Any, _ p2: Any) -> String {
            return Strings.tr("Localizable", "leagues.editor.fields.recurrence.help", String(describing: p1), String(describing: p2), fallback: "Choose '%@' for leagues that happen semi-frequently, such as once a week, or choose '%@' for tournaments and one-off events.")
          }
          /// Repeat?
          public static let title = Strings.tr("Localizable", "leagues.editor.fields.recurrence.title", fallback: "Repeat?")
        }
      }
    }
    public enum Errors {
      public enum Empty {
        /// You haven't added any leagues or events yet. Track your progress week over week for each league you're in. See how you measure up in tournaments with events.
        public static let message = Strings.tr("Localizable", "leagues.errors.empty.message", fallback: "You haven't added any leagues or events yet. Track your progress week over week for each league you're in. See how you measure up in tournaments with events.")
        /// No leagues found
        public static let title = Strings.tr("Localizable", "leagues.errors.empty.title", fallback: "No leagues found")
      }
    }
    public enum List {
      /// Add League
      public static let add = Strings.tr("Localizable", "leagues.list.add", fallback: "Add League")
      /// All Leagues
      public static let sectionTitle = Strings.tr("Localizable", "leagues.list.sectionTitle", fallback: "All Leagues")
      public enum Delete {
        /// Delete
        public static let action = Strings.tr("Localizable", "leagues.list.delete.action", fallback: "Delete")
        /// Cancel
        public static let cancel = Strings.tr("Localizable", "leagues.list.delete.cancel", fallback: "Cancel")
        /// Are you sure you want to delete %@?
        public static func title(_ p1: Any) -> String {
          return Strings.tr("Localizable", "leagues.list.delete.title", String(describing: p1), fallback: "Are you sure you want to delete %@?")
        }
      }
    }
    public enum Model {
      /// League
      public static let name = Strings.tr("Localizable", "leagues.model.name", fallback: "League")
    }
    public enum Recurrence {
      /// Never
      public static let never = Strings.tr("Localizable", "leagues.recurrence.never", fallback: "Never")
      /// Repeats
      public static let repeats = Strings.tr("Localizable", "leagues.recurrence.repeats", fallback: "Repeats")
    }
  }
  public enum Picker {
    /// Picking %@
    public static func title(_ p1: Any) -> String {
      return Strings.tr("Localizable", "picker.title", String(describing: p1), fallback: "Picking %@")
    }
    public enum Empty {
      /// Cancel
      public static let cancel = Strings.tr("Localizable", "picker.empty.cancel", fallback: "Cancel")
      /// No items found
      public static let title = Strings.tr("Localizable", "picker.empty.title", fallback: "No items found")
    }
  }
  public enum Series {
    public enum Editor {
      /// Cancel
      public static let cancel = Strings.tr("Localizable", "series.editor.cancel", fallback: "Cancel")
      /// New Series
      public static let new = Strings.tr("Localizable", "series.editor.new", fallback: "New Series")
      /// Start
      public static let start = Strings.tr("Localizable", "series.editor.start", fallback: "Start")
      public enum Fields {
        public enum Alley {
          /// Lanes
          public static let lanes = Strings.tr("Localizable", "series.editor.fields.alley.lanes", fallback: "Lanes")
          /// Starting Lane
          public static let startingLane = Strings.tr("Localizable", "series.editor.fields.alley.startingLane", fallback: "Starting Lane")
          /// Alley
          public static let title = Strings.tr("Localizable", "series.editor.fields.alley.title", fallback: "Alley")
          public enum BowlingAlley {
            /// None
            public static let `none` = Strings.tr("Localizable", "series.editor.fields.alley.bowlingAlley.none", fallback: "None")
            /// Bowling Alley
            public static let title = Strings.tr("Localizable", "series.editor.fields.alley.bowlingAlley.title", fallback: "Bowling Alley")
          }
        }
        public enum Details {
          /// Date
          public static let date = Strings.tr("Localizable", "series.editor.fields.details.date", fallback: "Date")
          /// Details
          public static let title = Strings.tr("Localizable", "series.editor.fields.details.title", fallback: "Details")
        }
        public enum NumberOfGames {
          /// Number of Games
          public static let title = Strings.tr("Localizable", "series.editor.fields.numberOfGames.title", fallback: "Number of Games")
        }
      }
    }
    public enum Errors {
      public enum Create {
        /// We couldn't create a new series
        public static let message = Strings.tr("Localizable", "series.errors.create.message", fallback: "We couldn't create a new series")
        /// Failed to create series
        public static let title = Strings.tr("Localizable", "series.errors.create.title", fallback: "Failed to create series")
        /// Try again
        public static let tryAgain = Strings.tr("Localizable", "series.errors.create.tryAgain", fallback: "Try again")
      }
      public enum Empty {
        /// You haven't added a series yet. Create a new series every time you bowl to see your stats mapped accurately over time.
        public static let message = Strings.tr("Localizable", "series.errors.empty.message", fallback: "You haven't added a series yet. Create a new series every time you bowl to see your stats mapped accurately over time.")
        /// No series found
        public static let title = Strings.tr("Localizable", "series.errors.empty.title", fallback: "No series found")
      }
    }
    public enum List {
      /// Add Series
      public static let add = Strings.tr("Localizable", "series.list.add", fallback: "Add Series")
      /// All Series
      public static let sectionTitle = Strings.tr("Localizable", "series.list.sectionTitle", fallback: "All Series")
      public enum Delete {
        /// Delete
        public static let action = Strings.tr("Localizable", "series.list.delete.action", fallback: "Delete")
        /// Cancel
        public static let cancel = Strings.tr("Localizable", "series.list.delete.cancel", fallback: "Cancel")
        /// Are you sure you want to delete %@?
        public static func title(_ p1: Any) -> String {
          return Strings.tr("Localizable", "series.list.delete.title", String(describing: p1), fallback: "Are you sure you want to delete %@?")
        }
      }
    }
    public enum Model {
      /// Series
      public static let name = Strings.tr("Localizable", "series.model.name", fallback: "Series")
    }
  }
  public enum Settings {
    /// Settings
    public static let title = Strings.tr("Localizable", "settings.title", fallback: "Settings")
    public enum Acknowledgements {
      /// Acknowledgements
      public static let title = Strings.tr("Localizable", "settings.acknowledgements.title", fallback: "Acknowledgements")
    }
    public enum Developer {
      /// https://runcode.blog
      public static let blog = Strings.tr("Localizable", "settings.developer.blog", fallback: "https://runcode.blog")
      /// Blog
      public static let blogTitle = Strings.tr("Localizable", "settings.developer.blogTitle", fallback: "Blog")
      /// Contact
      public static let contact = Strings.tr("Localizable", "settings.developer.contact", fallback: "Contact")
      /// @autoreleasefool@mastodon.social
      public static let mastodonHandle = Strings.tr("Localizable", "settings.developer.mastodonHandle", fallback: "@autoreleasefool@mastodon.social")
      /// https://mastodon.social/@autoreleasefool
      public static let mastodonUrl = Strings.tr("Localizable", "settings.developer.mastodonUrl", fallback: "https://mastodon.social/@autoreleasefool")
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
      /// Reset
      public static let reset = Strings.tr("Localizable", "settings.featureFlags.reset", fallback: "Reset")
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
  public enum Statistics {
    public enum Placeholder {
      /// Tap here to configure the stats you want to see
      public static let message = Strings.tr("Localizable", "statistics.placeholder.message", fallback: "Tap here to configure the stats you want to see")
      /// Statistics at a glance
      public static let title = Strings.tr("Localizable", "statistics.placeholder.title", fallback: "Statistics at a glance")
    }
  }
}
// swiftlint:enable explicit_type_interface function_parameter_count identifier_name line_length
// swiftlint:enable nesting type_body_length type_name vertical_whitespace_opening_braces

// MARK: - Implementation Details

extension Strings {
  private static func tr(_ table: String, _ key: String, _ args: CVarArg..., fallback value: String) -> String {
    let format = Bundle.main.localizedString(forKey: key, value: value, table: table)
    return String(format: format, locale: Locale.current, arguments: args)
  }
}
