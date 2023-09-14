// swiftlint:disable all
// Generated using SwiftGen — https://github.com/SwiftGen/SwiftGen

import Foundation

// swiftlint:disable superfluous_disable_command file_length implicit_return prefer_self_in_static_references

// MARK: - Strings

// swiftlint:disable explicit_type_interface function_parameter_count identifier_name line_length
// swiftlint:disable nesting type_body_length type_name vertical_whitespace_opening_braces
public enum Strings {
  /// Copied to clipboard
  public static let copiedToClipboard = Strings.tr("Localizable", "copiedToClipboard", fallback: "Copied to clipboard")
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
  public enum Accessory {
    /// Accessories
    public static let title = Strings.tr("Localizable", "accessory.title", fallback: "Accessory")
    public enum Overview {
      /// Accessories
      public static let title = Strings.tr("Localizable", "accessory.overview.title", fallback: "Accessories")
    }
  }
  public enum Action {
    /// Add
    public static let add = Strings.tr("Localizable", "action.add", fallback: "Add")
    /// Actions
    public static let apply = Strings.tr("Localizable", "action.apply", fallback: "Apply")
    /// Cancel
    public static let cancel = Strings.tr("Localizable", "action.cancel", fallback: "Cancel")
    /// Delete
    public static let delete = Strings.tr("Localizable", "action.delete", fallback: "Delete")
    /// Deselect all
    public static let deselectAll = Strings.tr("Localizable", "action.deselectAll", fallback: "Deselect all")
    /// Discard
    public static let discard = Strings.tr("Localizable", "action.discard", fallback: "Discard")
    /// Dismiss
    public static let dismiss = Strings.tr("Localizable", "action.dismiss", fallback: "Dismiss")
    /// Done
    public static let done = Strings.tr("Localizable", "action.done", fallback: "Done")
    /// Edit
    public static let edit = Strings.tr("Localizable", "action.edit", fallback: "Edit")
    /// Filter
    public static let filter = Strings.tr("Localizable", "action.filter", fallback: "Filter")
    /// Manage
    public static let manage = Strings.tr("Localizable", "action.manage", fallback: "Manage")
    /// Reload
    public static let reload = Strings.tr("Localizable", "action.reload", fallback: "Reload")
    /// Report
    public static let report = Strings.tr("Localizable", "action.report", fallback: "Report")
    /// Reset
    public static let reset = Strings.tr("Localizable", "action.reset", fallback: "Reset")
    /// Save
    public static let save = Strings.tr("Localizable", "action.save", fallback: "Save")
    /// Search
    public static let search = Strings.tr("Localizable", "action.search", fallback: "Search")
    /// Select
    public static let select = Strings.tr("Localizable", "action.select", fallback: "Select")
    /// Other
    public static let shareToOther = Strings.tr("Localizable", "action.shareToOther", fallback: "Other")
    /// Share to Stories
    public static let shareToStories = Strings.tr("Localizable", "action.shareToStories", fallback: "Share to Stories")
    /// Skip
    public static let skip = Strings.tr("Localizable", "action.skip", fallback: "Skip")
    /// Start
    public static let start = Strings.tr("Localizable", "action.start", fallback: "Start")
    /// Try again
    public static let tryAgain = Strings.tr("Localizable", "action.tryAgain", fallback: "Try again")
    /// View All
    public static let viewAll = Strings.tr("Localizable", "action.viewAll", fallback: "View All")
  }
  public enum Address {
    /// Address
    public static let title = Strings.tr("Localizable", "address.title", fallback: "Address")
    public enum Error {
      /// We were unable to gather details for the chosen location. Please try again.
      public static let notFound = Strings.tr("Localizable", "address.error.notFound", fallback: "We were unable to gather details for the chosen location. Please try again.")
      public enum Empty {
        /// No suggestions found
        public static let title = Strings.tr("Localizable", "address.error.empty.title", fallback: "No suggestions found")
      }
    }
  }
  public enum Alley {
    /// Alleys
    public static let title = Strings.tr("Localizable", "alley.title", fallback: "Alley")
    public enum Editor {
      public enum Fields {
        public enum Address {
          /// Alley Address
          public static let editorTitle = Strings.tr("Localizable", "alley.editor.fields.address.editorTitle", fallback: "Alley Address")
        }
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
    public enum Filters {
      /// Filter Alleys
      public static let title = Strings.tr("Localizable", "alley.filters.title", fallback: "Filter Alleys")
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
      /// Properties
      public static let title = Strings.tr("Localizable", "alley.properties.title", fallback: "Properties")
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
    public enum Title {
      /// Bowling Alley
      public static let bowlingAlley = Strings.tr("Localizable", "alley.title.bowlingAlley", fallback: "Bowling Alley")
    }
  }
  public enum App {
    /// App
    public static let name = Strings.tr("Localizable", "app.name", fallback: "Approach")
    public enum Icon {
      /// Bisexual Pride
      public static let bisexual = Strings.tr("Localizable", "app.icon.bisexual", fallback: "Bisexual Pride")
      /// Current icon
      public static let current = Strings.tr("Localizable", "app.icon.current", fallback: "Current icon")
      /// Earth
      public static let earth = Strings.tr("Localizable", "app.icon.earth", fallback: "Earth")
      /// Ember
      public static let ember = Strings.tr("Localizable", "app.icon.ember", fallback: "Ember")
      /// Glacial
      public static let glacial = Strings.tr("Localizable", "app.icon.glacial", fallback: "Glacial")
      /// Hexed
      public static let hexed = Strings.tr("Localizable", "app.icon.hexed", fallback: "Hexed")
      /// Pink
      public static let pink = Strings.tr("Localizable", "app.icon.pink", fallback: "Pink")
      /// Pride
      public static let pride = Strings.tr("Localizable", "app.icon.pride", fallback: "Pride")
      /// Primary
      public static let primary = Strings.tr("Localizable", "app.icon.primary", fallback: "Primary")
      /// Sage
      public static let sage = Strings.tr("Localizable", "app.icon.sage", fallback: "Sage")
      /// Tap to Reset
      public static let tapToReset = Strings.tr("Localizable", "app.icon.tapToReset", fallback: "Tap to Reset")
      /// Trans Pride
      public static let trans = Strings.tr("Localizable", "app.icon.trans", fallback: "Trans Pride")
      public enum Category {
        /// Pride
        public static let pride = Strings.tr("Localizable", "app.icon.category.pride", fallback: "Pride")
        /// Standard
        public static let standard = Strings.tr("Localizable", "app.icon.category.standard", fallback: "Standard")
      }
    }
    public enum Tabs {
      /// Accessories
      public static let accessories = Strings.tr("Localizable", "app.tabs.accessories", fallback: "Accessories")
      /// Overview
      public static let overview = Strings.tr("Localizable", "app.tabs.overview", fallback: "Overview")
      /// Settings
      public static let settings = Strings.tr("Localizable", "app.tabs.settings", fallback: "Settings")
      /// Statistics
      public static let statistics = Strings.tr("Localizable", "app.tabs.statistics", fallback: "Statistics")
    }
  }
  public enum Avatar {
    public enum Editor {
      /// Preview
      public static let preview = Strings.tr("Localizable", "avatar.editor.preview", fallback: "Preview")
      /// Avatar
      public static let title = Strings.tr("Localizable", "avatar.editor.title", fallback: "Edit avatar")
      public enum Properties {
        public enum BackgroundColor {
          /// Background colour
          public static let backgroundColor = Strings.tr("Localizable", "avatar.editor.properties.backgroundColor.backgroundColor", fallback: "Background colour")
          /// Randomize colour
          public static let randomColor = Strings.tr("Localizable", "avatar.editor.properties.backgroundColor.randomColor", fallback: "Randomize colour")
          /// Secondary colour
          public static let secondaryColor = Strings.tr("Localizable", "avatar.editor.properties.backgroundColor.secondaryColor", fallback: "Secondary colour")
          /// Background Colour
          public static let title = Strings.tr("Localizable", "avatar.editor.properties.backgroundColor.title", fallback: "Background Colour")
          public enum Style {
            /// Gradient
            public static let gradient = Strings.tr("Localizable", "avatar.editor.properties.backgroundColor.style.gradient", fallback: "Gradient")
            /// Solid
            public static let solid = Strings.tr("Localizable", "avatar.editor.properties.backgroundColor.style.solid", fallback: "Solid")
            /// Style
            public static let title = Strings.tr("Localizable", "avatar.editor.properties.backgroundColor.style.title", fallback: "Style")
          }
        }
        public enum Label {
          /// Label
          public static let title = Strings.tr("Localizable", "avatar.editor.properties.label.title", fallback: "Label")
        }
      }
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
    /// Current bowler
    public static let current = Strings.tr("Localizable", "bowler.current", fallback: "Current bowler")
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
      /// Preferred Gear
      public static let preferredGear = Strings.tr("Localizable", "bowler.list.preferredGear", fallback: "Preferred Gear")
      /// Bowlers
      public static let title = Strings.tr("Localizable", "bowler.list.title", fallback: "Bowlers")
      public enum PreferredGear {
        /// Pre-select all the gear you typically use, and it will automatically be added to every game. You can remove or add more gear to a game later.
        public static let footer = Strings.tr("Localizable", "bowler.list.preferredGear.footer", fallback: "Pre-select all the gear you typically use, and it will automatically be added to every game. You can remove or add more gear to a game later.")
      }
    }
  }
  public enum BowlingBall {
    /// Bowling Balls
    public static let title = Strings.tr("Localizable", "bowlingBall.title", fallback: "Bowling Ball")
    public enum List {
      /// Bowling Balls
      public static let title = Strings.tr("Localizable", "bowlingBall.list.title", fallback: "Bowling Balls")
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
    public enum Toast {
      /// Data missing
      public static let dataNotFound = Strings.tr("Localizable", "error.toast.dataNotFound", fallback: "Data missing")
      /// Error deleting data
      public static let failedToDelete = Strings.tr("Localizable", "error.toast.failedToDelete", fallback: "Error deleting data")
      /// Error loading data
      public static let failedToLoad = Strings.tr("Localizable", "error.toast.failedToLoad", fallback: "Error loading data")
      /// Error saving data
      public static let failedToSave = Strings.tr("Localizable", "error.toast.failedToSave", fallback: "Error saving data")
      /// %@ not created
      public static func itemNotCreated(_ p1: Any) -> String {
        return Strings.tr("Localizable", "error.toast.itemNotCreated", String(describing: p1), fallback: "%@ not created")
      }
      /// %@ not updated
      public static func itemNotUpdated(_ p1: Any) -> String {
        return Strings.tr("Localizable", "error.toast.itemNotUpdated", String(describing: p1), fallback: "%@ not updated")
      }
    }
  }
  public enum ErrorReport {
    /// Errors:
    /// %@
    public static func emailBody(_ p1: Any) -> String {
      return Strings.tr("Localizable", "errorReport.emailBody", String(describing: p1), fallback: "Errors:\n%@")
    }
    /// Email report
    public static let emailReport = Strings.tr("Localizable", "errorReport.emailReport", fallback: "Email report")
    /// Include device logs?
    public static let includeDeviceLogs = Strings.tr("Localizable", "errorReport.includeDeviceLogs", fallback: "Include device logs?")
    /// Maybe we can help.
    public static let maybeWeCanHelp = Strings.tr("Localizable", "errorReport.maybeWeCanHelp", fallback: "Maybe we can help.")
    /// Reporting an error?
    public static let reportingAnError = Strings.tr("Localizable", "errorReport.reportingAnError", fallback: "Reporting an error?")
    /// Share report
    public static let shareReport = Strings.tr("Localizable", "errorReport.shareReport", fallback: "Share report")
    /// You've encountered the following error a couple times:
    public static let youveEncountered = Strings.tr("Localizable", "errorReport.youveEncountered", fallback: "You've encountered the following error a couple times:")
    public enum IncludeDeviceLogs {
      /// Your logs contain no identifying information, and will only be used to diagnose your issue. Including your device logs makes this process much easier!
      public static let disclaimer = Strings.tr("Localizable", "errorReport.includeDeviceLogs.disclaimer", fallback: "Your logs contain no identifying information, and will only be used to diagnose your issue. Including your device logs makes this process much easier!")
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
    public static let title = Strings.tr("Localizable", "game.title", fallback: "Game")
    /// Game %d
    public static func titleWithOrdinal(_ p1: Int) -> String {
      return Strings.tr("Localizable", "game.titleWithOrdinal", p1, fallback: "Game %d")
    }
    public enum Editor {
      /// Locked
      public static let locked = Strings.tr("Localizable", "game.editor.locked", fallback: "Locked")
      public enum Bowlers {
        /// Drag to reorder
        public static let dragToReorder = Strings.tr("Localizable", "game.editor.bowlers.dragToReorder", fallback: "Drag to reorder")
      }
      public enum Fields {
        public enum Alley {
          public enum Lanes {
            /// Manage lanes
            public static let manageLanes = Strings.tr("Localizable", "game.editor.fields.alley.lanes.manageLanes", fallback: "Manage lanes")
            /// Set specific lanes?
            public static let selectLanes = Strings.tr("Localizable", "game.editor.fields.alley.lanes.selectLanes", fallback: "Set specific lanes?")
          }
        }
        public enum ExcludeFromStatistics {
          /// All of this league's games have been excluded from statistics. You must toggle this setting for the league before this game can be counted towards any statistics.
          public static let excludedWhenLeagueExcluded = Strings.tr("Localizable", "game.editor.fields.excludeFromStatistics.excludedWhenLeagueExcluded", fallback: "All of this league's games have been excluded from statistics. You must toggle this setting for the league before this game can be counted towards any statistics.")
          /// All of this series' games have been excluded from statistics. You must toggle this setting for the series before this game can be counted towards any statistics.
          public static let excludedWhenSeriesExcluded = Strings.tr("Localizable", "game.editor.fields.excludeFromStatistics.excludedWhenSeriesExcluded", fallback: "All of this series' games have been excluded from statistics. You must toggle this setting for the series before this game can be counted towards any statistics.")
          /// This series is a pre-bowl and all of its games are automatically excluded from statistics. You must toggle this setting for the series before this game can be counted towards any statistics.
          public static let excludedWhenSeriesPreBowl = Strings.tr("Localizable", "game.editor.fields.excludeFromStatistics.excludedWhenSeriesPreBowl", fallback: "This series is a pre-bowl and all of its games are automatically excluded from statistics. You must toggle this setting for the series before this game can be counted towards any statistics.")
          /// This game will not count towards any statistics. It will still appear in the app, but won't affect your overall average or other statistics. This can be useful for practice or incomplete games.
          public static let help = Strings.tr("Localizable", "game.editor.fields.excludeFromStatistics.help", fallback: "This game will not count towards any statistics. It will still appear in the app, but won't affect your overall average or other statistics. This can be useful for practice or incomplete games.")
          /// Exclude from statistics?
          public static let label = Strings.tr("Localizable", "game.editor.fields.excludeFromStatistics.label", fallback: "Exclude from statistics?")
          /// Statistics
          public static let title = Strings.tr("Localizable", "game.editor.fields.excludeFromStatistics.title", fallback: "Statistics")
        }
        public enum Gear {
          /// Choose any additional gear you're using this game
          public static let help = Strings.tr("Localizable", "game.editor.fields.gear.help", fallback: "Choose any additional gear you're using this game")
        }
        public enum Lock {
          /// Prevent all future edits to this game. No other changes will be saved. You can edit this game again by unlocking here.
          public static let help = Strings.tr("Localizable", "game.editor.fields.lock.help", fallback: "Prevent all future edits to this game. No other changes will be saved. You can edit this game again by unlocking here.")
          /// Lock for editing
          public static let label = Strings.tr("Localizable", "game.editor.fields.lock.label", fallback: "Lock for editing")
        }
        public enum ManualScore {
          /// Score set manually
          public static let caption = Strings.tr("Localizable", "game.editor.fields.manualScore.caption", fallback: "Score set manually")
          /// Score
          public static let prompt = Strings.tr("Localizable", "game.editor.fields.manualScore.prompt", fallback: "Score")
          /// Set manual score
          public static let title = Strings.tr("Localizable", "game.editor.fields.manualScore.title", fallback: "Set manual score")
        }
        public enum ScoringMethod {
          /// This game's frames will be hidden and ignored in all statistics.
          public static let help = Strings.tr("Localizable", "game.editor.fields.scoringMethod.help", fallback: "This game's frames will be hidden and ignored in all statistics.")
          /// Set manual score?
          public static let label = Strings.tr("Localizable", "game.editor.fields.scoringMethod.label", fallback: "Set manual score?")
        }
      }
      public enum Picker {
        /// Switch game
        public static let `switch` = Strings.tr("Localizable", "game.editor.picker.switch", fallback: "Switch game")
      }
      public enum Preferences {
        /// Flash editor changes?
        public static let flashEditorChanges = Strings.tr("Localizable", "game.editor.preferences.flashEditorChanges", fallback: "Flash editor changes?")
        /// Preferences
        public static let title = Strings.tr("Localizable", "game.editor.preferences.title", fallback: "Preferences")
        public enum FlashEditorChanges {
          /// Quickly highlight changes to the current bowler or game, so you don't lose track.
          public static let footer = Strings.tr("Localizable", "game.editor.preferences.flashEditorChanges.footer", fallback: "Quickly highlight changes to the current bowler or game, so you don't lose track.")
        }
      }
      public enum Toast {
        /// Switched frame
        public static let switchedFrame = Strings.tr("Localizable", "game.editor.toast.switchedFrame", fallback: "Switched frame")
      }
    }
    public enum List {
      /// Games
      public static let title = Strings.tr("Localizable", "game.list.title", fallback: "Games")
      public enum Header {
        /// High of %d
        public static func highGame(_ p1: Int) -> String {
          return Strings.tr("Localizable", "game.list.header.highGame", p1, fallback: "High of %d")
        }
        /// Low of %d
        public static func lowGame(_ p1: Int) -> String {
          return Strings.tr("Localizable", "game.list.header.lowGame", p1, fallback: "Low of %d")
        }
        /// Plural format key: "%#@numberOfGames@"
        public static func numberOfGames(_ p1: Int) -> String {
          return Strings.tr("Localizable", "game.list.header.numberOfGames", p1, fallback: "Plural format key: \"%#@numberOfGames@\"")
        }
        /// Series total
        public static let seriesTotal = Strings.tr("Localizable", "game.list.header.seriesTotal", fallback: "Series total")
        public enum Chart {
          /// Game
          public static let xAxisLabel = Strings.tr("Localizable", "game.list.header.chart.xAxisLabel", fallback: "Game")
          /// Score
          public static let yAxisLabel = Strings.tr("Localizable", "game.list.header.chart.yAxisLabel", fallback: "Score")
        }
      }
    }
    public enum Properties {
      public enum ExcludeFromStatistics {
        /// Exclude from statistics
        public static let exclude = Strings.tr("Localizable", "game.properties.excludeFromStatistics.exclude", fallback: "Exclude from statistics")
        /// Include in statistics
        public static let include = Strings.tr("Localizable", "game.properties.excludeFromStatistics.include", fallback: "Include in statistics")
      }
    }
    public enum Settings {
      /// Currently Editing
      public static let current = Strings.tr("Localizable", "game.settings.current", fallback: "Currently Editing")
      /// Game Settings
      public static let title = Strings.tr("Localizable", "game.settings.title", fallback: "Game Settings")
    }
  }
  public enum Gear {
    /// Owned by %@
    public static func ownedBy(_ p1: Any) -> String {
      return Strings.tr("Localizable", "gear.ownedBy", String(describing: p1), fallback: "Owned by %@")
    }
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
    public enum Filters {
      /// Filter Gear
      public static let title = Strings.tr("Localizable", "gear.filters.title", fallback: "Filter Gear")
    }
    public enum List {
      /// Add Gear
      public static let add = Strings.tr("Localizable", "gear.list.add", fallback: "Add Gear")
      /// Gear
      public static let title = Strings.tr("Localizable", "gear.list.title", fallback: "Gear")
    }
    public enum Properties {
      /// Kind
      public static let kind = Strings.tr("Localizable", "gear.properties.kind", fallback: "Kind")
      /// Owner
      public static let owner = Strings.tr("Localizable", "gear.properties.owner", fallback: "Owner")
      public enum Avatar {
        /// Customize
        public static let customize = Strings.tr("Localizable", "gear.properties.avatar.customize", fallback: "Customize")
        /// Create a unique avatar so you can quickly and easily recognize this item in the app.
        public static let description = Strings.tr("Localizable", "gear.properties.avatar.description", fallback: "Create a unique avatar so you can quickly and easily recognize this item in the app.")
        /// Avatar
        public static let title = Strings.tr("Localizable", "gear.properties.avatar.title", fallback: "Avatar")
      }
      public enum Kind {
        /// Ball
        public static let bowlingBall = Strings.tr("Localizable", "gear.properties.kind.bowlingBall", fallback: "Ball")
        /// Balls
        public static let bowlingBalls = Strings.tr("Localizable", "gear.properties.kind.bowlingBalls", fallback: "Balls")
        /// Shoes
        public static let shoes = Strings.tr("Localizable", "gear.properties.kind.shoes", fallback: "Shoes")
        /// Towel
        public static let towel = Strings.tr("Localizable", "gear.properties.kind.towel", fallback: "Towel")
        /// Towels
        public static let towels = Strings.tr("Localizable", "gear.properties.kind.towels", fallback: "Towels")
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
        public enum Position {
          /// You can mark which lanes have a wall to either the left or right, if this affects your game. Generally, only the first and last lanes in an alley should be marked as against any walls.
          public static let help = Strings.tr("Localizable", "lane.editor.fields.position.help", fallback: "You can mark which lanes have a wall to either the left or right, if this affects your game. Generally, only the first and last lanes in an alley should be marked as against any walls.")
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
      /// Lane
      public static let label = Strings.tr("Localizable", "lane.properties.label", fallback: "Lane")
      /// Position by walls?
      public static let position = Strings.tr("Localizable", "lane.properties.position", fallback: "Position by walls?")
      public enum Position {
        /// Wall on Left
        public static let leftWall = Strings.tr("Localizable", "lane.properties.position.leftWall", fallback: "Wall on Left")
        /// No walls
        public static let noWall = Strings.tr("Localizable", "lane.properties.position.noWall", fallback: "No walls")
        /// Wall on Right
        public static let rightWall = Strings.tr("Localizable", "lane.properties.position.rightWall", fallback: "Wall on Right")
      }
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
          /// This is where you plan on bowling this league or event.
          public static let help = Strings.tr("Localizable", "league.editor.fields.alley.help", fallback: "This is where you plan on bowling this league or event.")
          /// Alley
          public static let title = Strings.tr("Localizable", "league.editor.fields.alley.title", fallback: "Alley")
        }
        public enum ExcludeFromStatistics {
          /// This league and all of its games will not count towards any statistics. They will still appear in the app, but won't affect your overall average or other statistics.
          public static let help = Strings.tr("Localizable", "league.editor.fields.excludeFromStatistics.help", fallback: "This league and all of its games will not count towards any statistics. They will still appear in the app, but won't affect your overall average or other statistics.")
          /// Exclude from statistics?
          public static let label = Strings.tr("Localizable", "league.editor.fields.excludeFromStatistics.label", fallback: "Exclude from statistics?")
          /// Statistics
          public static let title = Strings.tr("Localizable", "league.editor.fields.excludeFromStatistics.title", fallback: "Statistics")
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
    public enum Filters {
      /// Filter Leagues
      public static let title = Strings.tr("Localizable", "league.filters.title", fallback: "Filter Leagues")
    }
    public enum List {
      /// Add League
      public static let add = Strings.tr("Localizable", "league.list.add", fallback: "Add League")
      /// Leagues
      public static let title = Strings.tr("Localizable", "league.list.title", fallback: "Leagues")
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
      public enum ExcludeFromStatistics {
        /// Exclude
        public static let exclude = Strings.tr("Localizable", "league.properties.excludeFromStatistics.exclude", fallback: "Exclude")
        /// Include
        public static let include = Strings.tr("Localizable", "league.properties.excludeFromStatistics.include", fallback: "Include")
      }
      public enum Recurrence {
        /// Never
        public static let never = Strings.tr("Localizable", "league.properties.recurrence.never", fallback: "Never")
        /// Repeats
        public static let repeats = Strings.tr("Localizable", "league.properties.recurrence.repeats", fallback: "Repeats")
      }
    }
  }
  public enum List {
    /// List
    public static let results = Strings.tr("Localizable", "list.results", fallback: "Results")
    public enum Averages {
      /// Show averages
      public static let showAverages = Strings.tr("Localizable", "list..averages.showAverages", fallback: "Show averages")
      /// All bowlers
      public static let allBowlers = Strings.tr("Localizable", "list.averages.allBowlers", fallback: "All bowlers")
    }
  }
  public enum MatchPlay {
    /// Record results?
    public static let record = Strings.tr("Localizable", "matchPlay.record", fallback: "Record results?")
    /// Match Play
    public static let title = Strings.tr("Localizable", "matchPlay.title", fallback: "Match Play")
    public enum Properties {
      /// Opponent's score
      public static let opponentScore = Strings.tr("Localizable", "matchPlay.properties.opponentScore", fallback: "Opponent's score")
      /// Outcome
      public static let result = Strings.tr("Localizable", "matchPlay.properties.result", fallback: "Outcome")
      public enum Result {
        /// Lost
        public static let lost = Strings.tr("Localizable", "matchPlay.properties.result.lost", fallback: "Lost")
        /// None
        public static let `none` = Strings.tr("Localizable", "matchPlay.properties.result.none", fallback: "None")
        /// Tied
        public static let tied = Strings.tr("Localizable", "matchPlay.properties.result.tied", fallback: "Tied")
        /// Won
        public static let won = Strings.tr("Localizable", "matchPlay.properties.result.won", fallback: "Won")
      }
    }
  }
  public enum Onboarding {
    /// Get started
    public static let getStarted = Strings.tr("Localizable", "onboarding.getStarted", fallback: "Get started")
    public enum Header {
      /// Approach
      public static let appName = Strings.tr("Localizable", "onboarding.header.appName", fallback: "Approach")
      /// Onboarding
      public static let welcomeTo = Strings.tr("Localizable", "onboarding.header.welcomeTo", fallback: "Welcome to a new")
    }
    public enum Logbook {
      /// Add Bowler
      public static let addBowler = Strings.tr("Localizable", "onboarding.logbook.addBowler", fallback: "Add Bowler")
      /// This logbook belongs to
      public static let belongsTo = Strings.tr("Localizable", "onboarding.logbook.belongsTo", fallback: "This logbook belongs to")
      /// Your Name
      public static let name = Strings.tr("Localizable", "onboarding.logbook.name", fallback: "Your Name")
    }
    public enum Message {
      /// Your 5 Pin Bowling Companion has arrived, with all the tracking, stats, and charts you could have ever wanted
      /// 
      /// Approach is the 5 Pin Bowling app I always wish existed, and I hope you feel the same
      public static let description = Strings.tr("Localizable", "onboarding.message.description", fallback: "Your 5 Pin Bowling Companion has arrived, with all the tracking, stats, and charts you could have ever wanted\n\nApproach is the 5 Pin Bowling app I always wish existed, and I hope you feel the same")
      /// Lovingly crafted in Vancouver
      public static let lovinglyCrafted = Strings.tr("Localizable", "onboarding.message.lovinglyCrafted", fallback: "Lovingly crafted in Vancouver")
    }
  }
  public enum Opponent {
    /// Matches
    public static let matches = Strings.tr("Localizable", "opponent.matches", fallback: "Matches")
    /// Record
    public static let record = Strings.tr("Localizable", "opponent.record", fallback: "Record")
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
    public enum Matches {
      /// No matches played. Try recording a match against this bowler in the game details.
      public static let `none` = Strings.tr("Localizable", "opponent.matches.none", fallback: "No matches played. Try recording a match against this bowler in the game details.")
    }
    public enum Record {
      /// Matches Lost
      public static let matchesLost = Strings.tr("Localizable", "opponent.record.matchesLost", fallback: "Matches Lost")
      /// Total Matches Played
      public static let matchesPlayed = Strings.tr("Localizable", "opponent.record.matchesPlayed", fallback: "Total Matches Played")
      /// Matches Tied
      public static let matchesTied = Strings.tr("Localizable", "opponent.record.matchesTied", fallback: "Matches Tied")
      /// Matches Won
      public static let matchesWon = Strings.tr("Localizable", "opponent.record.matchesWon", fallback: "Matches Won")
    }
  }
  public enum Ordering {
    /// Ordering
    public static let alphabetical = Strings.tr("Localizable", "ordering.alphabetical", fallback: "Alphabetical")
    /// Highest to Lowest
    public static let highestToLowest = Strings.tr("Localizable", "ordering.highestToLowest", fallback: "Highest to Lowest")
    /// Lowest to Highest
    public static let lowestToHighest = Strings.tr("Localizable", "ordering.lowestToHighest", fallback: "Lowest to Highest")
    /// Most Recently Used
    public static let mostRecentlyUsed = Strings.tr("Localizable", "ordering.mostRecentlyUsed", fallback: "Most Recently Used")
    /// Newest to Oldest
    public static let newestFirst = Strings.tr("Localizable", "ordering.newestFirst", fallback: "Newest to Oldest")
    /// Oldest to Newest
    public static let oldestFirst = Strings.tr("Localizable", "ordering.oldestFirst", fallback: "Oldest to Newest")
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
  public enum Roll {
    /// Roll
    public static func title(_ p1: Int) -> String {
      return Strings.tr("Localizable", "roll.title", p1, fallback: "Ball %d")
    }
    public enum Properties {
      public enum Ball {
        /// None selected
        public static let noneSelected = Strings.tr("Localizable", "roll.properties.ball.noneSelected", fallback: "None selected")
        /// Ball rolled
        public static let title = Strings.tr("Localizable", "roll.properties.ball.title", fallback: "Ball rolled")
      }
      public enum Foul {
        /// Foul?
        public static let title = Strings.tr("Localizable", "roll.properties.foul.title", fallback: "Foul?")
      }
    }
  }
  public enum Series {
    /// Series
    public static let title = Strings.tr("Localizable", "series.title", fallback: "Series")
    public enum Editor {
      public enum Fields {
        /// Plural format key: "%#@numberOfGames@"
        public static func numberOfGames(_ p1: Int) -> String {
          return Strings.tr("Localizable", "series.editor.fields.numberOfGames", p1, fallback: "Plural format key: \"%#@numberOfGames@\"")
        }
        public enum Alley {
          /// This is where you plan on bowling this series.
          public static let help = Strings.tr("Localizable", "series.editor.fields.alley.help", fallback: "This is where you plan on bowling this series.")
          /// Lanes
          public static let lanes = Strings.tr("Localizable", "series.editor.fields.alley.lanes", fallback: "Lanes")
          /// Alley
          public static let title = Strings.tr("Localizable", "series.editor.fields.alley.title", fallback: "Alley")
        }
        public enum ExcludeFromStatistics {
          /// All of this league's series have been excluded from statistics. You must toggle this setting for the league before this series can be counted towards any statistics.
          public static let excludedWhenLeagueExcluded = Strings.tr("Localizable", "series.editor.fields.excludeFromStatistics.excludedWhenLeagueExcluded", fallback: "All of this league's series have been excluded from statistics. You must toggle this setting for the league before this series can be counted towards any statistics.")
          /// Pre-bowls are automatically excluded from all statistics.
          public static let excludedWhenPreBowl = Strings.tr("Localizable", "series.editor.fields.excludeFromStatistics.excludedWhenPreBowl", fallback: "Pre-bowls are automatically excluded from all statistics.")
          /// This series and all of its games will not count towards any statistics. They will still appear in the app, but won't affect your average or other statistics.
          public static let help = Strings.tr("Localizable", "series.editor.fields.excludeFromStatistics.help", fallback: "This series and all of its games will not count towards any statistics. They will still appear in the app, but won't affect your average or other statistics.")
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
      /// Plural format key: "%#@numberOfGames@"
      public static func numberOfGames(_ p1: Int) -> String {
        return Strings.tr("Localizable", "series.list.numberOfGames", p1, fallback: "Plural format key: \"%#@numberOfGames@\"")
      }
      /// Series
      public static let title = Strings.tr("Localizable", "series.list.title", fallback: "Series")
      public enum Scores {
        /// %d — %d
        public static func range(_ p1: Int, _ p2: Int) -> String {
          return Strings.tr("Localizable", "series.list.scores.range", p1, p2, fallback: "%d — %d")
        }
        public enum Chart {
          /// Game
          public static let xAxisLabel = Strings.tr("Localizable", "series.list.scores.chart.xAxisLabel", fallback: "Game")
          /// Score
          public static let yAxisLabel = Strings.tr("Localizable", "series.list.scores.chart.yAxisLabel", fallback: "Score")
        }
      }
    }
    public enum PreBowl {
      /// Pre-Bowls
      public static let title = Strings.tr("Localizable", "series.preBowl.title", fallback: "Pre-Bowls")
    }
    public enum Properties {
      /// Bowling Alley
      public static let alley = Strings.tr("Localizable", "series.properties.alley", fallback: "Bowling Alley")
      /// Date
      public static let date = Strings.tr("Localizable", "series.properties.date", fallback: "Date")
      /// Number of games
      public static let numberOfGames = Strings.tr("Localizable", "series.properties.numberOfGames", fallback: "Number of games")
      public enum ExcludeFromStatistics {
        /// Exclude
        public static let exclude = Strings.tr("Localizable", "series.properties.excludeFromStatistics.exclude", fallback: "Exclude")
        /// Include
        public static let include = Strings.tr("Localizable", "series.properties.excludeFromStatistics.include", fallback: "Include")
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
      /// Acknowledgements
      public static let title = Strings.tr("Localizable", "settings.acknowledgements.title", fallback: "Acknowledgements")
    }
    public enum Analytics {
      /// Share anonymous analytics?
      public static let shareAnonymousAnalytics = Strings.tr("Localizable", "settings.analytics.shareAnonymousAnalytics", fallback: "Share anonymous analytics?")
      /// Settings
      public static let title = Strings.tr("Localizable", "settings.analytics.title", fallback: "Analytics")
      public enum Info {
        /// Approach collects anonymous analytics, such as the number of people using a feature, to improve the app. These analytics are never shared or sold.
        public static let paragraphOne = Strings.tr("Localizable", "settings.analytics.info.paragraphOne", fallback: "Approach collects anonymous analytics, such as the number of people using a feature, to improve the app. These analytics are never shared or sold.")
        /// You can opt in or out of letting us collect these analytics below. This won't affect your usage of the app or any of its features.
        public static let paragraphTwo = Strings.tr("Localizable", "settings.analytics.info.paragraphTwo", fallback: "You can opt in or out of letting us collect these analytics below. This won't affect your usage of the app or any of its features.")
      }
      public enum ShareAnonymousAnalytics {
        /// You can opt out from sharing analytics anytime. When you do, you're anonymized activity stops being shared immediately.
        public static let footer = Strings.tr("Localizable", "settings.analytics.shareAnonymousAnalytics.footer", fallback: "You can opt out from sharing analytics anytime. When you do, you're anonymized activity stops being shared immediately.")
      }
    }
    public enum AppIcon {
      /// App Icon
      public static let title = Strings.tr("Localizable", "settings.appIcon.title", fallback: "App Icon")
      public enum List {
        public enum Error {
          /// Could not change icon. Please try again.
          public static let failedToChange = Strings.tr("Localizable", "settings.appIcon.list.error.failedToChange", fallback: "Could not change icon. Please try again.")
          /// Could not find icon. Please try again.
          public static let notFound = Strings.tr("Localizable", "settings.appIcon.list.error.notFound", fallback: "Could not find icon. Please try again.")
        }
      }
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
      /// https://github.com/autoreleasefool/approach
      public static let openSourceRepositoryUrl = Strings.tr("Localizable", "settings.developer.openSourceRepositoryUrl", fallback: "https://github.com/autoreleasefool/approach")
      /// Developer
      public static let title = Strings.tr("Localizable", "settings.developer.title", fallback: "Developer")
      /// https://josephroque.dev
      public static let website = Strings.tr("Localizable", "settings.developer.website", fallback: "https://josephroque.dev")
      /// Website
      public static let websiteTitle = Strings.tr("Localizable", "settings.developer.websiteTitle", fallback: "Website")
    }
    public enum DeveloperOptions {
      /// Mock database
      public static let populateDatabase = Strings.tr("Localizable", "settings.developerOptions.populateDatabase", fallback: "Mock database")
    }
    public enum FeatureFlags {
      /// Match flags to Development
      public static let matchDevelopment = Strings.tr("Localizable", "settings.featureFlags.matchDevelopment", fallback: "Match flags to Development")
      /// Match flags to Release
      public static let matchRelease = Strings.tr("Localizable", "settings.featureFlags.matchRelease", fallback: "Match flags to Release")
      /// Match flags to Test
      public static let matchTest = Strings.tr("Localizable", "settings.featureFlags.matchTest", fallback: "Match flags to Test")
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
      public enum ReportBug {
        /// approach@josephroque.ca
        public static let email = Strings.tr("Localizable", "settings.help.reportBug.email", fallback: "approach@josephroque.ca")
        /// Approach Bug Report (%@)
        public static func subject(_ p1: Any) -> String {
          return Strings.tr("Localizable", "settings.help.reportBug.subject", String(describing: p1), fallback: "Approach Bug Report (%@)")
        }
      }
      public enum SendFeedback {
        /// approach@josephroque.ca
        public static let email = Strings.tr("Localizable", "settings.help.sendFeedback.email", fallback: "approach@josephroque.ca")
      }
    }
    public enum Opponents {
      /// Logs of all your past opponents and a record of your match play.
      public static let footer = Strings.tr("Localizable", "settings.opponents.footer", fallback: "Logs of all your past opponents and a record of your match play.")
    }
    public enum Statistics {
      /// Fine-tune your preferences when it comes to which statistics to show.
      public static let footer = Strings.tr("Localizable", "settings.statistics.footer", fallback: "Fine-tune your preferences when it comes to which statistics to show.")
      /// Statistics
      public static let title = Strings.tr("Localizable", "settings.statistics.title", fallback: "Statistics")
      public enum Overall {
        /// Hide statistics with a value of zero?
        public static let hideZeroStatistics = Strings.tr("Localizable", "settings.statistics.overall.hideZeroStatistics", fallback: "Hide statistics with a value of zero?")
        /// Overall
        public static let title = Strings.tr("Localizable", "settings.statistics.overall.title", fallback: "Overall")
      }
      public enum PerFrame {
        /// Count H2 as Head Pins?
        public static let countH2AsH = Strings.tr("Localizable", "settings.statistics.perFrame.countH2AsH", fallback: "Count H2 as Head Pins?")
        /// Count S2 as Split?
        public static let countSplitWithBonusAsSplit = Strings.tr("Localizable", "settings.statistics.perFrame.countSplitWithBonusAsSplit", fallback: "Count S2 as Split?")
        /// Per Frame
        public static let title = Strings.tr("Localizable", "settings.statistics.perFrame.title", fallback: "Per Frame")
      }
      public enum Widgets {
        /// Hide widgets for bowlers
        public static let hideInBowlerList = Strings.tr("Localizable", "settings.statistics.widgets.hideInBowlerList", fallback: "Hide widgets for bowlers")
        /// Hide widgets for leagues
        public static let hideInLeagueList = Strings.tr("Localizable", "settings.statistics.widgets.hideInLeagueList", fallback: "Hide widgets for leagues")
        /// Widgets
        public static let title = Strings.tr("Localizable", "settings.statistics.widgets.title", fallback: "Widgets")
      }
    }
  }
  public enum Sharing {
    /// Share Game
    public static let sharingGame = Strings.tr("Localizable", "sharing.sharingGame", fallback: "Share Game")
    /// Share Games
    public static let sharingGames = Strings.tr("Localizable", "sharing.sharingGames", fallback: "Share Games")
    /// Share Series
    public static let sharingSeries = Strings.tr("Localizable", "sharing.sharingSeries", fallback: "Share Series")
    /// Sharing
    public static let title = Strings.tr("Localizable", "sharing.title", fallback: "Share")
    public enum Frames {
      /// Show full game details?
      public static let includeDetails = Strings.tr("Localizable", "sharing.frames.includeDetails", fallback: "Show full game details?")
      /// Include frame labels?
      public static let includeLabels = Strings.tr("Localizable", "sharing.frames.includeLabels", fallback: "Include frame labels?")
      /// Frames
      public static let title = Strings.tr("Localizable", "sharing.frames.title", fallback: "Frames")
    }
    public enum Labels {
      /// Show or hide your name and other details to share.
      public static let footer = Strings.tr("Localizable", "sharing.labels.footer", fallback: "Show or hide your name and other details to share.")
      /// Include alley?
      public static let includeAlley = Strings.tr("Localizable", "sharing.labels.includeAlley", fallback: "Include alley?")
      /// Include bowler?
      public static let includeBowler = Strings.tr("Localizable", "sharing.labels.includeBowler", fallback: "Include bowler?")
      /// Include league?
      public static let includeLeague = Strings.tr("Localizable", "sharing.labels.includeLeague", fallback: "Include league?")
      /// Include series?
      public static let includeSeries = Strings.tr("Localizable", "sharing.labels.includeSeries", fallback: "Include series?")
      /// Labels
      public static let title = Strings.tr("Localizable", "sharing.labels.title", fallback: "Labels")
    }
    public enum Layout {
      /// Label Position
      public static let labelPosition = Strings.tr("Localizable", "sharing.layout.labelPosition", fallback: "Label Position")
      /// Layout
      public static let title = Strings.tr("Localizable", "sharing.layout.title", fallback: "Layout")
      public enum LabelPosition {
        /// Bottom
        public static let bottom = Strings.tr("Localizable", "sharing.layout.labelPosition.bottom", fallback: "Bottom")
        /// Left
        public static let `left` = Strings.tr("Localizable", "sharing.layout.labelPosition.left", fallback: "Left")
        /// Right
        public static let `right` = Strings.tr("Localizable", "sharing.layout.labelPosition.right", fallback: "Right")
        /// Top
        public static let top = Strings.tr("Localizable", "sharing.layout.labelPosition.top", fallback: "Top")
      }
    }
    public enum Preview {
      /// Preview
      public static let title = Strings.tr("Localizable", "sharing.preview.title", fallback: "Preview")
    }
    public enum ScoreSheet {
      /// Score
      public static let score = Strings.tr("Localizable", "sharing.scoreSheet.score", fallback: "Score")
      public enum Style {
        /// Default
        public static let `default` = Strings.tr("Localizable", "sharing.scoreSheet.style.default", fallback: "Default")
        /// Plain
        public static let plain = Strings.tr("Localizable", "sharing.scoreSheet.style.plain", fallback: "Plain")
        /// Pride
        public static let pride = Strings.tr("Localizable", "sharing.scoreSheet.style.pride", fallback: "Pride")
      }
    }
    public enum Style {
      /// Style
      public static let title = Strings.tr("Localizable", "sharing.style.title", fallback: "Style")
    }
  }
  public enum SortOrder {
    /// Sort Order
    public static let title = Strings.tr("Localizable", "sortOrder.title", fallback: "Sort Order")
  }
  public enum Statistics {
    /// Statistics
    public static let title = Strings.tr("Localizable", "statistics.title", fallback: "Statistics")
    public enum Categories {
      public enum Average {
        /// Average
        public static let title = Strings.tr("Localizable", "statistics.categories.average.title", fallback: "Average")
      }
      public enum Fouls {
        /// Fouls
        public static let title = Strings.tr("Localizable", "statistics.categories.fouls.title", fallback: "Fouls")
      }
      public enum MatchPlay {
        /// Match Play
        public static let title = Strings.tr("Localizable", "statistics.categories.matchPlay.title", fallback: "Match Play")
      }
      public enum OnFirstRoll {
        /// On First Roll
        public static let title = Strings.tr("Localizable", "statistics.categories.onFirstRoll.title", fallback: "On First Roll")
      }
      public enum Overall {
        /// Overall
        public static let title = Strings.tr("Localizable", "statistics.categories.overall.title", fallback: "Overall")
      }
      public enum PinsLeftOnDeck {
        /// Pins Left on Deck
        public static let title = Strings.tr("Localizable", "statistics.categories.pinsLeftOnDeck.title", fallback: "Pins Left on Deck")
      }
      public enum Series {
        /// Series
        public static let title = Strings.tr("Localizable", "statistics.categories.series.title", fallback: "Series")
      }
    }
    public enum Charts {
      /// There doesn't seem to be any data available.
      public static let unavailable = Strings.tr("Localizable", "statistics.charts.unavailable", fallback: "There doesn't seem to be any data available.")
      public enum AxesLabels {
        /// Date
        public static let date = Strings.tr("Localizable", "statistics.charts.axesLabels.date", fallback: "Date")
      }
    }
    public enum Details {
      public enum Filter {
        /// Source
        public static let source = Strings.tr("Localizable", "statistics.details.filter.source", fallback: "Source")
      }
    }
    public enum Filter {
      /// Aggregation
      public static let aggregation = Strings.tr("Localizable", "statistics.filter.aggregation", fallback: "Aggregation")
      /// Filter Statistics
      public static let title = Strings.tr("Localizable", "statistics.filter.title", fallback: "Filter Statistics")
      public enum Aggregation {
        /// Accumulate
        public static let accumulate = Strings.tr("Localizable", "statistics.filter.aggregation.accumulate", fallback: "Accumulate")
        /// Periodic
        public static let periodic = Strings.tr("Localizable", "statistics.filter.aggregation.periodic", fallback: "Periodic")
      }
      public enum Frame {
        /// Ball Rolled
        public static let ballRolled = Strings.tr("Localizable", "statistics.filter.frame.ballRolled", fallback: "Ball Rolled")
        public enum BallRolled {
          /// You haven't selected any bowling balls.
          public static let noneSelected = Strings.tr("Localizable", "statistics.filter.frame.ballRolled.noneSelected", fallback: "You haven't selected any bowling balls.")
        }
      }
      public enum Game {
        /// By Lanes
        public static let byLanes = Strings.tr("Localizable", "statistics.filter.game.byLanes", fallback: "By Lanes")
        /// By Position
        public static let byPosition = Strings.tr("Localizable", "statistics.filter.game.byPosition", fallback: "By Position")
        /// Gear Used
        public static let gearUsed = Strings.tr("Localizable", "statistics.filter.game.gearUsed", fallback: "Gear Used")
        /// Lane Filter
        public static let laneFilter = Strings.tr("Localizable", "statistics.filter.game.laneFilter", fallback: "Lane Filter")
        /// Lane Position
        public static let lanePosition = Strings.tr("Localizable", "statistics.filter.game.lanePosition", fallback: "Lane Position")
        /// Lanes
        public static let lanes = Strings.tr("Localizable", "statistics.filter.game.lanes", fallback: "Lanes")
        /// Opponent
        public static let opponent = Strings.tr("Localizable", "statistics.filter.game.opponent", fallback: "Opponent")
        public enum GearUsed {
          /// You haven't selected any gear.
          public static let noneSelected = Strings.tr("Localizable", "statistics.filter.game.gearUsed.noneSelected", fallback: "You haven't selected any gear.")
        }
      }
      public enum Label {
        public enum Game {
          /// Game
          public static let title = Strings.tr("Localizable", "statistics.filter.label.game.title", fallback: "Game")
          public enum Lanes {
            /// Lane Positions
            public static let positions = Strings.tr("Localizable", "statistics.filter.label.game.lanes.positions", fallback: "Lane Positions")
            /// Lanes
            public static let title = Strings.tr("Localizable", "statistics.filter.label.game.lanes.title", fallback: "Lanes")
          }
        }
        public enum Gear {
          /// Balls Rolled
          public static let ballsRolled = Strings.tr("Localizable", "statistics.filter.label.gear.ballsRolled", fallback: "Balls Rolled")
          /// Gear
          public static let title = Strings.tr("Localizable", "statistics.filter.label.gear.title", fallback: "Gear")
        }
        public enum League {
          /// Repeats?
          public static let repeats = Strings.tr("Localizable", "statistics.filter.label.league.repeats", fallback: "Repeats?")
          /// League
          public static let title = Strings.tr("Localizable", "statistics.filter.label.league.title", fallback: "League")
        }
        public enum Opponent {
          /// Opponent
          public static let title = Strings.tr("Localizable", "statistics.filter.label.opponent.title", fallback: "Opponent")
        }
        public enum Series {
          /// Ends
          public static let ends = Strings.tr("Localizable", "statistics.filter.label.series.ends", fallback: "Ends")
          /// Starts
          public static let starts = Strings.tr("Localizable", "statistics.filter.label.series.starts", fallback: "Starts")
          /// Series
          public static let title = Strings.tr("Localizable", "statistics.filter.label.series.title", fallback: "Series")
          public enum Alley {
            /// Material
            public static let material = Strings.tr("Localizable", "statistics.filter.label.series.alley.material", fallback: "Material")
            /// Mechanism
            public static let mechanism = Strings.tr("Localizable", "statistics.filter.label.series.alley.mechanism", fallback: "Mechanism")
            /// Pin Base
            public static let pinBase = Strings.tr("Localizable", "statistics.filter.label.series.alley.pinBase", fallback: "Pin Base")
            /// Pin Fall
            public static let pinFall = Strings.tr("Localizable", "statistics.filter.label.series.alley.pinFall", fallback: "Pin Fall")
            /// Alley
            public static let title = Strings.tr("Localizable", "statistics.filter.label.series.alley.title", fallback: "Alley")
          }
        }
      }
      public enum Series {
        /// Alley
        public static let alley = Strings.tr("Localizable", "statistics.filter.series.alley", fallback: "Alley")
        /// Alley Filter
        public static let alleyFilter = Strings.tr("Localizable", "statistics.filter.series.alleyFilter", fallback: "Alley Filter")
        /// Alley Properties
        public static let alleyProperties = Strings.tr("Localizable", "statistics.filter.series.alleyProperties", fallback: "Alley Properties")
        /// By Alley
        public static let byAlley = Strings.tr("Localizable", "statistics.filter.series.byAlley", fallback: "By Alley")
        /// By Properties
        public static let byProperties = Strings.tr("Localizable", "statistics.filter.series.byProperties", fallback: "By Properties")
        /// End Date
        public static let endDate = Strings.tr("Localizable", "statistics.filter.series.endDate", fallback: "End Date")
        /// Filter by end date?
        public static let filterByEndDate = Strings.tr("Localizable", "statistics.filter.series.filterByEndDate", fallback: "Filter by end date?")
        /// Filter by start date?
        public static let filterByStartDate = Strings.tr("Localizable", "statistics.filter.series.filterByStartDate", fallback: "Filter by start date?")
        /// Start Date
        public static let startDate = Strings.tr("Localizable", "statistics.filter.series.startDate", fallback: "Start Date")
      }
    }
    public enum List {
      /// Hide statistics with a value of zero?
      public static let hideZeroStatistics = Strings.tr("Localizable", "statistics.list.hideZeroStatistics", fallback: "Hide statistics with a value of zero?")
      public enum HideZeroStatistics {
        /// Some statistics have been hidden from the list because they were empty. You can choose to show these statistics to get a better view of your play.
        public static let help = Strings.tr("Localizable", "statistics.list.hideZeroStatistics.help", fallback: "Some statistics have been hidden from the list because they were empty. You can choose to show these statistics to get a better view of your play.")
      }
    }
    public enum Overview {
      /// View Detailed Statistics
      public static let viewDetailedStatistics = Strings.tr("Localizable", "statistics.overview.viewDetailedStatistics", fallback: "View Detailed Statistics")
      public enum GetAnOverviewHint {
        /// You'll see an overview of all your statistics any time you open this screen. We'll show you some key statistics you'll probably want to keep in mind, for all your leagues.
        public static let message = Strings.tr("Localizable", "statistics.overview.getAnOverviewHint.message", fallback: "You'll see an overview of all your statistics any time you open this screen. We'll show you some key statistics you'll probably want to keep in mind, for all your leagues.")
        /// Get an overview
        public static let title = Strings.tr("Localizable", "statistics.overview.getAnOverviewHint.title", fallback: "Get an overview")
      }
      public enum ViewMoreDetailsHint {
        /// You can look up detailed statistics for bowlers, leagues, and more. Select the filters you're interested in exploring and see how your bowling improves over time, and what you need to focus on.
        public static let message = Strings.tr("Localizable", "statistics.overview.viewMoreDetailsHint.message", fallback: "You can look up detailed statistics for bowlers, leagues, and more. Select the filters you're interested in exploring and see how your bowling improves over time, and what you need to focus on.")
        /// View more detailed statistics
        public static let title = Strings.tr("Localizable", "statistics.overview.viewMoreDetailsHint.title", fallback: "View more detailed statistics")
      }
    }
    public enum Placeholder {
      /// Tap here to configure the stats you want to see
      public static let message = Strings.tr("Localizable", "statistics.placeholder.message", fallback: "Tap here to configure the stats you want to see")
      /// Statistics at a glance
      public static let title = Strings.tr("Localizable", "statistics.placeholder.title", fallback: "Statistics at a glance")
    }
    public enum Title {
      /// Aces
      public static let aces = Strings.tr("Localizable", "statistics.title.aces", fallback: "Aces")
      /// Aces Spared
      public static let acesSpared = Strings.tr("Localizable", "statistics.title.acesSpared", fallback: "Aces Spared")
      /// Average Pins Left on Deck
      public static let averagePinsLeftOnDeck = Strings.tr("Localizable", "statistics.title.averagePinsLeftOnDeck", fallback: "Average Pins Left on Deck")
      /// Chop Offs
      public static let chopOffs = Strings.tr("Localizable", "statistics.title.chopOffs", fallback: "Chop Offs")
      /// Chop Offs Spared
      public static let chopOffsSpared = Strings.tr("Localizable", "statistics.title.chopOffsSpared", fallback: "Chop Offs Spared")
      /// Fouls
      public static let fouls = Strings.tr("Localizable", "statistics.title.fouls", fallback: "Fouls")
      /// Average
      public static let gameAverage = Strings.tr("Localizable", "statistics.title.gameAverage", fallback: "Average")
      /// Head Pins
      public static let headPins = Strings.tr("Localizable", "statistics.title.headPins", fallback: "Head Pins")
      /// Head Pins Spared
      public static let headPinsSpared = Strings.tr("Localizable", "statistics.title.headPinsSpared", fallback: "Head Pins Spared")
      /// High Series of 3
      public static let highSeriesOf3 = Strings.tr("Localizable", "statistics.title.highSeriesOf3", fallback: "High Series of 3")
      /// High Single
      public static let highSingle = Strings.tr("Localizable", "statistics.title.highSingle", fallback: "High Single")
      /// Left Chop Offs
      public static let leftChopOffs = Strings.tr("Localizable", "statistics.title.leftChopOffs", fallback: "Left Chop Offs")
      /// Left Chop Offs Spared
      public static let leftChopOffsSpared = Strings.tr("Localizable", "statistics.title.leftChopOffsSpared", fallback: "Left Chop Offs Spared")
      /// Left of Middle Hits
      public static let leftOfMiddleHits = Strings.tr("Localizable", "statistics.title.leftOfMiddleHits", fallback: "Left of Middle Hits")
      /// Lefts
      public static let lefts = Strings.tr("Localizable", "statistics.title.lefts", fallback: "Lefts")
      /// Left Splits
      public static let leftSplits = Strings.tr("Localizable", "statistics.title.leftSplits", fallback: "Left Splits")
      /// Left Splits Spared
      public static let leftSplitsSpared = Strings.tr("Localizable", "statistics.title.leftSplitsSpared", fallback: "Left Splits Spared")
      /// Lefts Spared
      public static let leftsSpared = Strings.tr("Localizable", "statistics.title.leftsSpared", fallback: "Lefts Spared")
      /// Left Twelves
      public static let leftTwelves = Strings.tr("Localizable", "statistics.title.leftTwelves", fallback: "Left Twelves")
      /// Left Twelves Spared
      public static let leftTwelvesSpared = Strings.tr("Localizable", "statistics.title.leftTwelvesSpared", fallback: "Left Twelves Spared")
      /// Matches Lost
      public static let matchesLost = Strings.tr("Localizable", "statistics.title.matchesLost", fallback: "Matches Lost")
      /// Matches Played
      public static let matchesPlayed = Strings.tr("Localizable", "statistics.title.matchesPlayed", fallback: "Matches Played")
      /// Matches Tied
      public static let matchesTied = Strings.tr("Localizable", "statistics.title.matchesTied", fallback: "Matches Tied")
      /// Matches Won
      public static let matchesWon = Strings.tr("Localizable", "statistics.title.matchesWon", fallback: "Matches Won")
      /// Middle Hits
      public static let middleHits = Strings.tr("Localizable", "statistics.title.middleHits", fallback: "Middle Hits")
      /// Number of Games
      public static let numberOfGames = Strings.tr("Localizable", "statistics.title.numberOfGames", fallback: "Number of Games")
      /// Right Chop Offs
      public static let rightChopOffs = Strings.tr("Localizable", "statistics.title.rightChopOffs", fallback: "Right Chop Offs")
      /// Right Chop Offs Spared
      public static let rightChopOffsSpared = Strings.tr("Localizable", "statistics.title.rightChopOffsSpared", fallback: "Right Chop Offs Spared")
      /// Right of Middle Hits
      public static let rightOfMiddleHits = Strings.tr("Localizable", "statistics.title.rightOfMiddleHits", fallback: "Right of Middle Hits")
      /// Rights
      public static let rights = Strings.tr("Localizable", "statistics.title.rights", fallback: "Rights")
      /// Right Splits
      public static let rightSplits = Strings.tr("Localizable", "statistics.title.rightSplits", fallback: "Right Splits")
      /// Right Splits Spared
      public static let rightSplitsSpared = Strings.tr("Localizable", "statistics.title.rightSplitsSpared", fallback: "Right Splits Spared")
      /// Rights Spared
      public static let rightsSpared = Strings.tr("Localizable", "statistics.title.rightsSpared", fallback: "Rights Spared")
      /// Right Twelves
      public static let rightTwelves = Strings.tr("Localizable", "statistics.title.rightTwelves", fallback: "Right Twelves")
      /// Right Twelves Spared
      public static let rightTwelvesSpared = Strings.tr("Localizable", "statistics.title.rightTwelvesSpared", fallback: "Right Twelves Spared")
      /// Opportunities to Spare
      public static let spareChances = Strings.tr("Localizable", "statistics.title.spareChances", fallback: "Opportunities to Spare")
      /// Spare Conversions
      public static let spareConversions = Strings.tr("Localizable", "statistics.title.spareConversions", fallback: "Spare Conversions")
      /// Splits
      public static let splits = Strings.tr("Localizable", "statistics.title.splits", fallback: "Splits")
      /// Splits Spared
      public static let splitsSpared = Strings.tr("Localizable", "statistics.title.splitsSpared", fallback: "Splits Spared")
      /// Middle Hits which were Strikes
      public static let strikeMiddleHits = Strings.tr("Localizable", "statistics.title.strikeMiddleHits", fallback: "Middle Hits which were Strikes")
      /// Total Strikes
      public static let strikes = Strings.tr("Localizable", "statistics.title.strikes", fallback: "Total Strikes")
      /// Total Pinfall
      public static let totalPinfall = Strings.tr("Localizable", "statistics.title.totalPinfall", fallback: "Total Pinfall")
      /// Total Pins Left on Deck
      public static let totalPinsLeftOnDeck = Strings.tr("Localizable", "statistics.title.totalPinsLeftOnDeck", fallback: "Total Pins Left on Deck")
      /// Total Rolls
      public static let totalRolls = Strings.tr("Localizable", "statistics.title.totalRolls", fallback: "Total Rolls")
      /// Twelves
      public static let twelves = Strings.tr("Localizable", "statistics.title.twelves", fallback: "Twelves")
      /// Twelves Spared
      public static let twelvesSpared = Strings.tr("Localizable", "statistics.title.twelvesSpared", fallback: "Twelves Spared")
    }
  }
  public enum Widget {
    public enum Builder {
      /// Widget
      public static let preview = Strings.tr("Localizable", "widget.builder.preview", fallback: "Preview")
      /// Statistic to Display
      public static let statistic = Strings.tr("Localizable", "widget.builder.statistic", fallback: "Statistic to Display")
      /// Timeline
      public static let timeline = Strings.tr("Localizable", "widget.builder.timeline", fallback: "Timeline")
      /// Create a Widget
      public static let title = Strings.tr("Localizable", "widget.builder.title", fallback: "Create a Widget")
    }
    public enum Chart {
      /// Not enough data
      public static let noData = Strings.tr("Localizable", "widget.chart.noData", fallback: "Not enough data")
      /// Error
      public static let unavailable = Strings.tr("Localizable", "widget.chart.unavailable", fallback: "Error")
    }
    public enum LayoutBuilder {
      /// You don't have any widgets yet. Tap the '+' to add
      public static let addNewInstructions = Strings.tr("Localizable", "widget.layoutBuilder.addNewInstructions", fallback: "You don't have any widgets yet. Tap the '+' to add")
      /// Tap and hold widgets to reorder
      public static let reorderInstructions = Strings.tr("Localizable", "widget.layoutBuilder.reorderInstructions", fallback: "Tap and hold widgets to reorder")
      /// Widgets
      public static let title = Strings.tr("Localizable", "widget.layoutBuilder.title", fallback: "Widgets")
    }
    public enum Timeline {
      /// All Time
      public static let allTime = Strings.tr("Localizable", "widget.timeline.allTime", fallback: "All Time")
      /// Past Month
      public static let past1Month = Strings.tr("Localizable", "widget.timeline.past1Month", fallback: "Past Month")
      /// Past 3 Months
      public static let past3Months = Strings.tr("Localizable", "widget.timeline.past3Months", fallback: "Past 3 Months")
      /// Past 6 Months
      public static let past6Months = Strings.tr("Localizable", "widget.timeline.past6Months", fallback: "Past 6 Months")
      /// Past Year
      public static let pastYear = Strings.tr("Localizable", "widget.timeline.pastYear", fallback: "Past Year")
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
