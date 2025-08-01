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
  /// Loading
  public static let loading = Strings.tr("Localizable", "loading", fallback: "Loading")
  /// No
  public static let no = Strings.tr("Localizable", "no", fallback: "No")
  /// None
  public static let `none` = Strings.tr("Localizable", "none", fallback: "None")
  /// Other
  public static let other = Strings.tr("Localizable", "other", fallback: "Other")
  /// approach@josephroque.ca
  public static let supportEmail = Strings.tr("Localizable", "supportEmail", fallback: "approach@josephroque.ca")
  /// Tab
  public static let tab = Strings.tr("Localizable", "tab", fallback: "Tab")
  /// Unknown
  public static let unknown = Strings.tr("Localizable", "unknown", fallback: "Unknown")
  /// Yes
  public static let yes = Strings.tr("Localizable", "yes", fallback: "Yes")
  public enum Accessory {
    /// Accessory
    public static let title = Strings.tr("Localizable", "accessory.title", fallback: "Accessory")
    public enum Overview {
      /// Showing only %d most recent
      public static func showingLimit(_ p1: Int) -> String {
        return Strings.tr("Localizable", "accessory.overview.showingLimit", p1, fallback: "Showing only %d most recent")
      }
      /// Accessories
      public static let title = Strings.tr("Localizable", "accessory.overview.title", fallback: "Accessories")
    }
  }
  public enum Achievements {
    public enum Earnable {
      public enum Iconista {
        /// Iconista
        public static let title = Strings.tr("Localizable", "achievements.earnable.iconista.title", fallback: "Iconista")
      }
      public enum TenYear {
        /// Ten Years
        public static let title = Strings.tr("Localizable", "achievements.earnable.tenYear.title", fallback: "Ten Years")
      }
    }
    public enum List {
      /// Plural format key: "%#@earnedCount@"
      public static func earnedCount(_ p1: Int) -> String {
        return Strings.tr("Localizable", "achievements.list.earnedCount", p1, fallback: "Plural format key: \"%#@earnedCount@\"")
      }
      /// Badges
      public static let title = Strings.tr("Localizable", "achievements.list.title", fallback: "Badges")
      public enum Header {
        /// Check back later for more details!
        public static let checkBack = Strings.tr("Localizable", "achievements.list.header.checkBack", fallback: "Check back later for more details!")
        /// Soon you'll be able to earn badges while improving your bowling game.
        public static let soon = Strings.tr("Localizable", "achievements.list.header.soon", fallback: "Soon you'll be able to earn badges while improving your bowling game.")
      }
    }
  }
  public enum Action {
    /// Add
    public static let add = Strings.tr("Localizable", "action.add", fallback: "Add")
    /// Apply
    public static let apply = Strings.tr("Localizable", "action.apply", fallback: "Apply")
    /// Archive
    public static let archive = Strings.tr("Localizable", "action.archive", fallback: "Archive")
    /// Cancel
    public static let cancel = Strings.tr("Localizable", "action.cancel", fallback: "Cancel")
    /// Continue
    public static let `continue` = Strings.tr("Localizable", "action.continue", fallback: "Continue")
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
    /// Finish
    public static let finish = Strings.tr("Localizable", "action.finish", fallback: "Finish")
    /// Manage
    public static let manage = Strings.tr("Localizable", "action.manage", fallback: "Manage")
    /// Reload
    public static let reload = Strings.tr("Localizable", "action.reload", fallback: "Reload")
    /// Reorder
    public static let reorder = Strings.tr("Localizable", "action.reorder", fallback: "Reorder")
    /// Report
    public static let report = Strings.tr("Localizable", "action.report", fallback: "Report")
    /// Reset
    public static let reset = Strings.tr("Localizable", "action.reset", fallback: "Reset")
    /// Restore
    public static let restore = Strings.tr("Localizable", "action.restore", fallback: "Restore")
    /// Save
    public static let save = Strings.tr("Localizable", "action.save", fallback: "Save")
    /// Search
    public static let search = Strings.tr("Localizable", "action.search", fallback: "Search")
    /// Select
    public static let select = Strings.tr("Localizable", "action.select", fallback: "Select")
    /// Share
    public static let share = Strings.tr("Localizable", "action.share", fallback: "Share")
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
    /// Alley
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
      /// Pinfall
      public static let pinFall = Strings.tr("Localizable", "alley.properties.pinFall", fallback: "Pinfall")
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
  public enum Announcement {
    public enum StartingBackups {
      /// Automatic Backups
      public static let title = Strings.tr("Localizable", "announcement.startingBackups.title", fallback: "Automatic Backups")
      public enum Description {
        public enum AutomaticSyncing {
          /// Your data will automatically begin syncing in case of data or device loss.
          public static let description = Strings.tr("Localizable", "announcement.startingBackups.description.automaticSyncing.description", fallback: "Your data will automatically begin syncing in case of data or device loss.")
          /// Approach now supports iCloud
          public static let title = Strings.tr("Localizable", "announcement.startingBackups.description.automaticSyncing.title", fallback: "Approach now supports iCloud")
        }
        public enum ToRestore {
          /// To disable sync, or restore data, go to the settings.
          public static let description = Strings.tr("Localizable", "announcement.startingBackups.description.toRestore.description", fallback: "To disable sync, or restore data, go to the settings.")
          /// See more in Settings
          public static let title = Strings.tr("Localizable", "announcement.startingBackups.description.toRestore.title", fallback: "See more in Settings")
        }
      }
    }
    public enum TenYears {
      /// Approach is turning 10!
      public static let title = Strings.tr("Localizable", "announcement.tenYears.title", fallback: "Approach is turning 10!")
      public enum Action {
        /// Claim Badge
        public static let claimBadge = Strings.tr("Localizable", "announcement.tenYears.action.claimBadge", fallback: "Claim Badge")
      }
      public enum Description {
        /// From Bowling Companion to Approach, it's been 10 years since its launch.
        public static let fromBowlingCompanionToApproach = Strings.tr("Localizable", "announcement.tenYears.description.fromBowlingCompanionToApproach", fallback: "From Bowling Companion to Approach, it's been 10 years since its launch.")
        /// I hope you've enjoyed using Approach as much as I've enjoyed building it.
        public static let hopeYouveEnjoyed = Strings.tr("Localizable", "announcement.tenYears.description.hopeYouveEnjoyed", fallback: "I hope you've enjoyed using Approach as much as I've enjoyed building it.")
      }
    }
    public enum WhatsNew {
      /// What's New in
      /// Approach
      public static let title = Strings.tr("Localizable", "announcement.whatsNew.title", fallback: "What's New in\nApproach")
      public enum V1 {
        public enum ShareToSocialMedia {
          /// A new option to share games with a narrower layout so they fit better on the screen when sharing to social media.
          public static let description = Strings.tr("Localizable", "announcement.whatsNew.v1.shareToSocialMedia.description", fallback: "A new option to share games with a narrower layout so they fit better on the screen when sharing to social media.")
          /// Share to Social Media
          public static let title = Strings.tr("Localizable", "announcement.whatsNew.v1.shareToSocialMedia.title", fallback: "Share to Social Media")
        }
      }
    }
  }
  public enum App {
    /// Approach
    public static let name = Strings.tr("Localizable", "app.name", fallback: "Approach")
    public enum Icon {
      /// Bisexual Pride
      public static let bisexual = Strings.tr("Localizable", "app.icon.bisexual", fallback: "Bisexual Pride")
      /// Candy Corn
      public static let candyCorn = Strings.tr("Localizable", "app.icon.candyCorn", fallback: "Candy Corn")
      /// Christmas
      public static let christmas = Strings.tr("Localizable", "app.icon.christmas", fallback: "Christmas")
      /// Current icon
      public static let current = Strings.tr("Localizable", "app.icon.current", fallback: "Current icon")
      /// Dark
      public static let dark = Strings.tr("Localizable", "app.icon.dark", fallback: "Dark")
      /// Devil Horns
      public static let devilHorns = Strings.tr("Localizable", "app.icon.devilHorns", fallback: "Devil Horns")
      /// Fabric
      public static let fabric = Strings.tr("Localizable", "app.icon.fabric", fallback: "Fabric")
      /// Pride
      public static let pride = Strings.tr("Localizable", "app.icon.pride", fallback: "Pride")
      /// Primary
      public static let primary = Strings.tr("Localizable", "app.icon.primary", fallback: "Primary")
      /// Purple
      public static let purple = Strings.tr("Localizable", "app.icon.purple", fallback: "Purple")
      /// Tap to Reset
      public static let tapToReset = Strings.tr("Localizable", "app.icon.tapToReset", fallback: "Tap to Reset")
      /// Trans Pride
      public static let trans = Strings.tr("Localizable", "app.icon.trans", fallback: "Trans Pride")
      /// Witch's Hat
      public static let witchHat = Strings.tr("Localizable", "app.icon.witchHat", fallback: "Witch's Hat")
      public enum Category {
        /// Christmas
        public static let christmas = Strings.tr("Localizable", "app.icon.category.christmas", fallback: "Christmas")
        /// Halloween
        public static let halloween = Strings.tr("Localizable", "app.icon.category.halloween", fallback: "Halloween")
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
  public enum Archive {
    /// Archive
    public static let title = Strings.tr("Localizable", "archive.title", fallback: "Archive")
    public enum Alert {
      /// Your bowler's data has been restored
      public static let restoredBowler = Strings.tr("Localizable", "archive.alert.restoredBowler", fallback: "Your bowler's data has been restored")
      /// Your game's data has been restored
      public static let restoredGame = Strings.tr("Localizable", "archive.alert.restoredGame", fallback: "Your game's data has been restored")
      /// Your league's data has been restored
      public static let restoredLeague = Strings.tr("Localizable", "archive.alert.restoredLeague", fallback: "Your league's data has been restored")
      /// Your series' data has been restored
      public static let restoredSeries = Strings.tr("Localizable", "archive.alert.restoredSeries", fallback: "Your series' data has been restored")
      /// Unarchived %@
      public static func unarchived(_ p1: Any) -> String {
        return Strings.tr("Localizable", "archive.alert.unarchived", String(describing: p1), fallback: "Unarchived %@")
      }
    }
    public enum List {
      /// Archived on %@
      public static func archivedOn(_ p1: Any) -> String {
        return Strings.tr("Localizable", "archive.list.archivedOn", String(describing: p1), fallback: "Archived on %@")
      }
      /// Here you will find all the bowlers, leagues, series, and games you've previously archived. They're never deleted, just hidden. You can always recover this data by swiping the items below.
      public static let description = Strings.tr("Localizable", "archive.list.description", fallback: "Here you will find all the bowlers, leagues, series, and games you've previously archived. They're never deleted, just hidden. You can always recover this data by swiping the items below.")
      /// No items archived
      public static let `none` = Strings.tr("Localizable", "archive.list.none", fallback: "No items archived")
      public enum Bowler {
        /// Archived with %@ leagues, %@ series, %@ games
        public static func description(_ p1: Any, _ p2: Any, _ p3: Any) -> String {
          return Strings.tr("Localizable", "archive.list.bowler.description", String(describing: p1), String(describing: p2), String(describing: p3), fallback: "Archived with %@ leagues, %@ series, %@ games")
        }
      }
      public enum Game {
        /// Belongs to %@, in %@, bowled on %@
        public static func description(_ p1: Any, _ p2: Any, _ p3: Any) -> String {
          return Strings.tr("Localizable", "archive.list.game.description", String(describing: p1), String(describing: p2), String(describing: p3), fallback: "Belongs to %@, in %@, bowled on %@")
        }
        /// %@ Game with score of %@
        public static func title(_ p1: Any, _ p2: Any) -> String {
          return Strings.tr("Localizable", "archive.list.game.title", String(describing: p1), String(describing: p2), fallback: "%@ Game with score of %@")
        }
      }
      public enum League {
        /// Belongs to %@, archived with %@ series, %@ games
        public static func description(_ p1: Any, _ p2: Any, _ p3: Any) -> String {
          return Strings.tr("Localizable", "archive.list.league.description", String(describing: p1), String(describing: p2), String(describing: p3), fallback: "Belongs to %@, archived with %@ series, %@ games")
        }
      }
      public enum Series {
        /// Belongs to %@, in %@, archived with %@ games
        public static func description(_ p1: Any, _ p2: Any, _ p3: Any) -> String {
          return Strings.tr("Localizable", "archive.list.series.description", String(describing: p1), String(describing: p2), String(describing: p3), fallback: "Belongs to %@, in %@, archived with %@ games")
        }
      }
    }
  }
  public enum Avatar {
    public enum Editor {
      /// Preview
      public static let preview = Strings.tr("Localizable", "avatar.editor.preview", fallback: "Preview")
      /// Edit avatar
      public static let title = Strings.tr("Localizable", "avatar.editor.title", fallback: "Edit avatar")
      public enum Kind {
        /// Photo
        public static let photo = Strings.tr("Localizable", "avatar.editor.kind.photo", fallback: "Photo")
        /// Text
        public static let text = Strings.tr("Localizable", "avatar.editor.kind.text", fallback: "Text")
        /// Kind
        public static let title = Strings.tr("Localizable", "avatar.editor.kind.title", fallback: "Kind")
      }
      public enum Photo {
        /// Select photo
        public static let choosePhoto = Strings.tr("Localizable", "avatar.editor.photo.choosePhoto", fallback: "Select photo")
      }
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
  public enum Backups {
    public enum Delete {
      /// Are you sure you want to delete this backup? It can't be restored later.
      public static let message = Strings.tr("Localizable", "backups.delete.message", fallback: "Are you sure you want to delete this backup? It can't be restored later.")
      /// Delete backup?
      public static let title = Strings.tr("Localizable", "backups.delete.title", fallback: "Delete backup?")
    }
    public enum Error {
      public enum FailedToBackup {
        /// Approach will attempt another backup later. You can ignore this message, or manually perform a sync from the settings.
        public static let instructions = Strings.tr("Localizable", "backups.error.failedToBackup.instructions", fallback: "Approach will attempt another backup later. You can ignore this message, or manually perform a sync from the settings.")
        /// Open Settings
        public static let openSettings = Strings.tr("Localizable", "backups.error.failedToBackup.openSettings", fallback: "Open Settings")
        /// Something went wrong!
        public static let subtitle = Strings.tr("Localizable", "backups.error.failedToBackup.subtitle", fallback: "Something went wrong!")
        /// Backup failed
        public static let title = Strings.tr("Localizable", "backups.error.failedToBackup.title", fallback: "Backup failed")
        public enum SyncStatus {
          /// Your data has never successfully been backed up!
          public static let neverBackedUp = Strings.tr("Localizable", "backups.error.failedToBackup.syncStatus.neverBackedUp", fallback: "Your data has never successfully been backed up!")
          /// You've never exported your data!
          public static let neverExported = Strings.tr("Localizable", "backups.error.failedToBackup.syncStatus.neverExported", fallback: "You've never exported your data!")
          /// Plural format key: "%#@days@"
          public static func timeSinceLastBackup(_ p1: Int) -> String {
            return Strings.tr("Localizable", "backups.error.failedToBackup.syncStatus.timeSinceLastBackup", p1, fallback: "Plural format key: \"%#@days@\"")
          }
          /// Plural format key: "%#@days@"
          public static func timeSinceLastExport(_ p1: Int) -> String {
            return Strings.tr("Localizable", "backups.error.failedToBackup.syncStatus.timeSinceLastExport", p1, fallback: "Plural format key: \"%#@days@\"")
          }
          /// Backup Status
          public static let title = Strings.tr("Localizable", "backups.error.failedToBackup.syncStatus.title", fallback: "Backup Status")
        }
      }
    }
    public enum List {
      /// %@ mb
      public static func fileSize(_ p1: Any) -> String {
        return Strings.tr("Localizable", "backups.list.fileSize", String(describing: p1), fallback: "%@ mb")
      }
      /// It's been more than 2 weeks since your data was last synced
      public static let lastSyncNotWithinTwoWeeks = Strings.tr("Localizable", "backups.list.lastSyncNotWithinTwoWeeks", fallback: "It's been more than 2 weeks since your data was last synced")
      /// Your data was synced in the last 2 weeks
      public static let lastSyncWithinTwoWeeks = Strings.tr("Localizable", "backups.list.lastSyncWithinTwoWeeks", fallback: "Your data was synced in the last 2 weeks")
      /// Latest
      public static let latest = Strings.tr("Localizable", "backups.list.latest", fallback: "Latest")
      /// Sync now
      public static let manualSync = Strings.tr("Localizable", "backups.list.manualSync", fallback: "Sync now")
      /// Most recent backups
      public static let mostRecent = Strings.tr("Localizable", "backups.list.mostRecent", fallback: "Most recent backups")
      /// Backups
      public static let title = Strings.tr("Localizable", "backups.list.title", fallback: "Backups")
      public enum EnableAutomaticBackups {
        /// Backups will be synced to your iCloud account
        public static let description = Strings.tr("Localizable", "backups.list.enableAutomaticBackups.description", fallback: "Backups will be synced to your iCloud account")
        /// Enable automatic backups?
        public static let title = Strings.tr("Localizable", "backups.list.enableAutomaticBackups.title", fallback: "Enable automatic backups?")
      }
      public enum Error {
        /// You previously disabled cloud backups. You can still return to the Settings and export your data to save it elsewhere.
        public static let backupsDisabled = Strings.tr("Localizable", "backups.list.error.backupsDisabled", fallback: "You previously disabled cloud backups. You can still return to the Settings and export your data to save it elsewhere.")
        /// iCloud doesn't seem to be enabled or Approach cannot access it.
        public static let icloudUnavailable = Strings.tr("Localizable", "backups.list.error.icloudUnavailable", fallback: "iCloud doesn't seem to be enabled or Approach cannot access it.")
      }
      public enum MostRecent {
        /// You have no recent backups
        public static let `none` = Strings.tr("Localizable", "backups.list.mostRecent.none", fallback: "You have no recent backups")
      }
      public enum NeverBackedUp {
        /// If you don't want to use iCloud, you can export your data file from the Settings to any service you'd like
        public static let description = Strings.tr("Localizable", "backups.list.neverBackedUp.description", fallback: "If you don't want to use iCloud, you can export your data file from the Settings to any service you'd like")
        /// You've never backed up your data!
        public static let title = Strings.tr("Localizable", "backups.list.neverBackedUp.title", fallback: "You've never backed up your data!")
      }
      public enum WhyEnable {
        /// Approach can automatically backup your data to your iCloud account in case of lost data or a lost device. Backups will enable you to recover your Approach data any time from the Settings.
        public static let description = Strings.tr("Localizable", "backups.list.whyEnable.description", fallback: "Approach can automatically backup your data to your iCloud account in case of lost data or a lost device. Backups will enable you to recover your Approach data any time from the Settings.")
        /// Why enable backups?
        public static let title = Strings.tr("Localizable", "backups.list.whyEnable.title", fallback: "Why enable backups?")
      }
    }
    public enum Restore {
      /// Restoring your data from a backup will overwrite any new data you've recorded since this backup. Are you sure about this?
      public static let message = Strings.tr("Localizable", "backups.restore.message", fallback: "Restoring your data from a backup will overwrite any new data you've recorded since this backup. Are you sure about this?")
      /// Data successfully restored!
      public static let successRestoring = Strings.tr("Localizable", "backups.restore.successRestoring", fallback: "Data successfully restored!")
      /// Restore from backup?
      public static let title = Strings.tr("Localizable", "backups.restore.title", fallback: "Restore from backup?")
      public enum Action {
        /// Restore
        public static let restore = Strings.tr("Localizable", "backups.restore.action.restore", fallback: "Restore")
      }
    }
    public enum Toast {
      public enum Success {
        /// Backup successful
        public static let message = Strings.tr("Localizable", "backups.toast.success.message", fallback: "Backup successful")
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
    /// Bowler
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
    /// Bowling Ball
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
        /// Details
        public static let title = Strings.tr("Localizable", "editor.fields.details.title", fallback: "Details")
      }
    }
  }
  public enum Error {
    /// We couldn't load your data.
    public static let loadingFailed = Strings.tr("Localizable", "error.loadingFailed", fallback: "We couldn't load your data.")
    public enum Generic {
      /// Something went wrong!
      public static let title = Strings.tr("Localizable", "error.generic.title", fallback: "Something went wrong!")
    }
    public enum Toast {
      /// Data missing
      public static let dataNotFound = Strings.tr("Localizable", "error.toast.dataNotFound", fallback: "Data missing")
      /// Error archiving data
      public static let failedToArchive = Strings.tr("Localizable", "error.toast.failedToArchive", fallback: "Error archiving data")
      /// Error deleting data
      public static let failedToDelete = Strings.tr("Localizable", "error.toast.failedToDelete", fallback: "Error deleting data")
      /// Error importing data
      public static let failedToImport = Strings.tr("Localizable", "error.toast.failedToImport", fallback: "Error importing data")
      /// Error loading data
      public static let failedToLoad = Strings.tr("Localizable", "error.toast.failedToLoad", fallback: "Error loading data")
      /// Error restoring data
      public static let failedToRestore = Strings.tr("Localizable", "error.toast.failedToRestore", fallback: "Error restoring data")
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
      /// Tap to Report
      public static let tapToReport = Strings.tr("Localizable", "error.toast.tapToReport", fallback: "Tap to Report")
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
  public enum Export {
    /// An error occurred: %@
    public static func errorMessage(_ p1: Any) -> String {
      return Strings.tr("Localizable", "export.errorMessage", String(describing: p1), fallback: "An error occurred: %@")
    }
    /// At any time you can export your Approach data, to backup or transfer devices. Save the file somewhere you can always find it later.
    public static let exportAnytime = Strings.tr("Localizable", "export.exportAnytime", fallback: "At any time you can export your Approach data, to backup or transfer devices. Save the file somewhere you can always find it later.")
    /// Export data
    public static let exportData = Strings.tr("Localizable", "export.exportData", fallback: "Export data")
    /// You last exported your data on %@
    public static func lastExportedAt(_ p1: Any) -> String {
      return Strings.tr("Localizable", "export.lastExportedAt", String(describing: p1), fallback: "You last exported your data on %@")
    }
    /// You've never exported your data before.
    public static let neverExported = Strings.tr("Localizable", "export.neverExported", fallback: "You've never exported your data before.")
    /// Exporting
    public static let title = Strings.tr("Localizable", "export.title", fallback: "Exporting")
    /// We recommend exporting your data regularly, in case of any unexpected circumstances causing you to lose your phone or data!
    public static let weRecommend = Strings.tr("Localizable", "export.weRecommend", fallback: "We recommend exporting your data regularly, in case of any unexpected circumstances causing you to lose your phone or data!")
    /// Your data is always yours.
    public static let yourData = Strings.tr("Localizable", "export.yourData", fallback: "Your data is always yours.")
  }
  public enum Form {
    public enum Prompt {
      /// Add %@
      public static func add(_ p1: Any) -> String {
        return Strings.tr("Localizable", "form.prompt.add", String(describing: p1), fallback: "Add %@")
      }
      /// Are you sure you want to archive %@?
      public static func archive(_ p1: Any) -> String {
        return Strings.tr("Localizable", "form.prompt.archive", String(describing: p1), fallback: "Are you sure you want to archive %@?")
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
      public enum Archive {
        /// It will be hidden and no longer affect your statistics. It can be recovered from the Archive in Settings.
        public static let message = Strings.tr("Localizable", "form.prompt.archive.message", fallback: "It will be hidden and no longer affect your statistics. It can be recovered from the Archive in Settings.")
      }
    }
  }
  public enum Frame {
    /// Frame %d
    public static func title(_ p1: Int) -> String {
      return Strings.tr("Localizable", "frame.title", p1, fallback: "Frame %d")
    }
    public enum Editor {
      public enum DragHint {
        /// Drag your finger across the pins to mark them as knocked down, or to reset them.
        public static let message = Strings.tr("Localizable", "frame.editor.dragHint.message", fallback: "Drag your finger across the pins to mark them as knocked down, or to reset them.")
      }
    }
  }
  public enum Game {
    /// Game
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
          /// No alley was selected for this series. To add one, you can navigate back and use the edit button at the top of the screen.
          public static let noneSelected = Strings.tr("Localizable", "game.editor.fields.alley.noneSelected", fallback: "No alley was selected for this series. To add one, you can navigate back and use the edit button at the top of the screen.")
          public enum Lanes {
            /// Record which lanes you bowled on for more detailed stats.
            public static let help = Strings.tr("Localizable", "game.editor.fields.alley.lanes.help", fallback: "Record which lanes you bowled on for more detailed stats.")
            /// Manage lanes
            public static let manageLanes = Strings.tr("Localizable", "game.editor.fields.alley.lanes.manageLanes", fallback: "Manage lanes")
            /// Set specific lanes?
            public static let selectLanes = Strings.tr("Localizable", "game.editor.fields.alley.lanes.selectLanes", fallback: "Set specific lanes?")
            public enum Duplicate {
              /// Yes, copy to all
              public static let copyToAll = Strings.tr("Localizable", "game.editor.fields.alley.lanes.duplicate.copyToAll", fallback: "Yes, copy to all")
              /// No, I might change lanes
              public static let dismiss = Strings.tr("Localizable", "game.editor.fields.alley.lanes.duplicate.dismiss", fallback: "No, I might change lanes")
              /// If you're bowling all your games on the same lanes tonight, easily copy the lanes you just set to the rest of the games. You can change these later.
              public static let message = Strings.tr("Localizable", "game.editor.fields.alley.lanes.duplicate.message", fallback: "If you're bowling all your games on the same lanes tonight, easily copy the lanes you just set to the rest of the games. You can change these later.")
              /// Copy lanes to all games?
              public static let title = Strings.tr("Localizable", "game.editor.fields.alley.lanes.duplicate.title", fallback: "Copy lanes to all games?")
            }
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
          /// Frame-by-Frame
          public static let byFrame = Strings.tr("Localizable", "game.editor.fields.scoringMethod.byFrame", fallback: "Frame-by-Frame")
          /// Tap to set a manual score
          public static let help = Strings.tr("Localizable", "game.editor.fields.scoringMethod.help", fallback: "Tap to set a manual score")
          /// Manual
          public static let manual = Strings.tr("Localizable", "game.editor.fields.scoringMethod.manual", fallback: "Manual")
          /// Scoring
          public static let title = Strings.tr("Localizable", "game.editor.fields.scoringMethod.title", fallback: "Scoring")
        }
        public enum Statistics {
          /// View stats
          public static let viewStatistics = Strings.tr("Localizable", "game.editor.fields.statistics.viewStatistics", fallback: "View stats")
        }
        public enum StrikeOut {
          /// Clean up, strike out, and you could bowl %d
          public static func ifYouStrikeOut(_ p1: Int) -> String {
            return Strings.tr("Localizable", "game.editor.fields.strikeOut.ifYouStrikeOut", p1, fallback: "Clean up, strike out, and you could bowl %d")
          }
          /// Tap to see your highest possible score
          public static let subtitle = Strings.tr("Localizable", "game.editor.fields.strikeOut.subtitle", fallback: "Tap to see your highest possible score")
          /// What if I strike out?
          public static let title = Strings.tr("Localizable", "game.editor.fields.strikeOut.title", fallback: "What if I strike out?")
        }
      }
      public enum Locked {
        /// This game is locked and cannot be edited.
        public static let message = Strings.tr("Localizable", "game.editor.locked.message", fallback: "This game is locked and cannot be edited.")
        /// Unlock
        public static let unlock = Strings.tr("Localizable", "game.editor.locked.unlock", fallback: "Unlock")
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
      public enum Footer {
        public enum ArchiveTip {
          /// Tap the '+' to add more games to any series, and swipe to hide any unwanted games.
          public static let message = Strings.tr("Localizable", "game.list.footer.archiveTip.message", fallback: "Tap the '+' to add more games to any series, and swipe to hide any unwanted games.")
          /// You can now add and archive games to series
          public static let title = Strings.tr("Localizable", "game.list.footer.archiveTip.title", fallback: "You can now add and archive games to series")
        }
      }
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
        /// Pre-bowl
        public static let preBowl = Strings.tr("Localizable", "game.list.header.preBowl", fallback: "Pre-bowl")
        /// This series was originally bowled on %@
        public static func preBowledOn(_ p1: Any) -> String {
          return Strings.tr("Localizable", "game.list.header.preBowledOn", String(describing: p1), fallback: "This series was originally bowled on %@")
        }
        /// See your scores
        public static let seeYourScores = Strings.tr("Localizable", "game.list.header.seeYourScores", fallback: "See your scores")
        /// Series total
        public static let seriesTotal = Strings.tr("Localizable", "game.list.header.seriesTotal", fallback: "Series total")
        /// This pre-bowl has not been used yet
        public static let unusedPreBowl = Strings.tr("Localizable", "game.list.header.unusedPreBowl", fallback: "This pre-bowl has not been used yet")
        /// Start bowling to see them charted here
        public static let whenYouStartBowling = Strings.tr("Localizable", "game.list.header.whenYouStartBowling", fallback: "Start bowling to see them charted here")
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
  public enum Import {
    /// Importing
    public static let title = Strings.tr("Localizable", "import.title", fallback: "Importing")
    public enum Action {
      /// Import data
      public static let `import` = Strings.tr("Localizable", "import.action.import", fallback: "Import data")
      /// Restore
      public static let restore = Strings.tr("Localizable", "import.action.restore", fallback: "Restore")
      /// Restore previous data
      public static let restorePreviousData = Strings.tr("Localizable", "import.action.restorePreviousData", fallback: "Restore previous data")
    }
    public enum Description {
      /// Approach allows to you export your data to a back up file for safety and security of your data.
      public static let approachAllowsExport = Strings.tr("Localizable", "import.description.approachAllowsExport", fallback: "Approach allows to you export your data to a back up file for safety and security of your data.")
      /// If you lose your data, you can restore your data from a back up file here.
      public static let importBackupFiles = Strings.tr("Localizable", "import.description.importBackupFiles", fallback: "If you lose your data, you can restore your data from a back up file here.")
      /// Something not looking quite right? You can restore your data to before your last import.
      public static let restore = Strings.tr("Localizable", "import.description.restore", fallback: "Something not looking quite right? You can restore your data to before your last import.")
    }
    public enum Error {
      /// Could not find latest back up. Restart Approach to try again.
      public static let failedToFetchBackup = Strings.tr("Localizable", "import.error.failedToFetchBackup", fallback: "Could not find latest back up. Restart Approach to try again.")
      /// Approach couldn't find the file you imported. Please try again.
      public static let failedToImport = Strings.tr("Localizable", "import.error.failedToImport", fallback: "Approach couldn't find the file you imported. Please try again.")
      /// Approach couldn't restore to the latest backup. Please try again.
      public static let failedToRestore = Strings.tr("Localizable", "import.error.failedToRestore", fallback: "Approach couldn't restore to the latest backup. Please try again.")
    }
    public enum Importing {
      /// The file you selected may be from too new a version of Approach.
      public static let databaseTooNew = Strings.tr("Localizable", "import.importing.databaseTooNew", fallback: "The file you selected may be from too new a version of Approach.")
      /// The file you selected may be from too old a version of Approach.
      public static let databaseTooOld = Strings.tr("Localizable", "import.importing.databaseTooOld", fallback: "The file you selected may be from too old a version of Approach.")
      /// There was an error importing your data
      public static let error = Strings.tr("Localizable", "import.importing.error", fallback: "There was an error importing your data")
      /// Importing...
      public static let inProgress = Strings.tr("Localizable", "import.importing.inProgress", fallback: "Importing...")
      /// Import not started...
      public static let notStarted = Strings.tr("Localizable", "import.importing.notStarted", fallback: "Import not started...")
      /// If you're having trouble importing your data, try sending us an email at 
      public static let report = Strings.tr("Localizable", "import.importing.report", fallback: "If you're having trouble importing your data, try sending us an email at ")
      /// Data successfully imported!
      public static let successImporting = Strings.tr("Localizable", "import.importing.successImporting", fallback: "Data successfully imported!")
      /// Data successfully restored!
      public static let successRestoring = Strings.tr("Localizable", "import.importing.successRestoring", fallback: "Data successfully restored!")
      /// The file you selected was not recognized.
      public static let unrecognized = Strings.tr("Localizable", "import.importing.unrecognized", fallback: "The file you selected was not recognized.")
      public enum Report {
        /// Approach Import Error (%@)
        public static func emailSubject(_ p1: Any) -> String {
          return Strings.tr("Localizable", "import.importing.report.emailSubject", String(describing: p1), fallback: "Approach Import Error (%@)")
        }
        /// Send email
        public static let sendEmail = Strings.tr("Localizable", "import.importing.report.sendEmail", fallback: "Send email")
      }
    }
    public enum Instructions {
      /// You may not be able to recover it later.
      public static let notRecover = Strings.tr("Localizable", "import.instructions.notRecover", fallback: "You may not be able to recover it later.")
      /// This will overwrite any existing data on this device.
      public static let overwrite = Strings.tr("Localizable", "import.instructions.overwrite", fallback: "This will overwrite any existing data on this device.")
    }
    public enum Restore {
      /// You last imported your data on %@. If you choose to restore your data, you will lose any new data you've recorded since this import, and your records will be reset to before the imported data was applied. Are you sure about this?
      public static func message(_ p1: Any) -> String {
        return Strings.tr("Localizable", "import.restore.message", String(describing: p1), fallback: "You last imported your data on %@. If you choose to restore your data, you will lose any new data you've recorded since this import, and your records will be reset to before the imported data was applied. Are you sure about this?")
      }
      /// Restore to before last import?
      public static let title = Strings.tr("Localizable", "import.restore.title", fallback: "Restore to before last import?")
    }
  }
  public enum Lane {
    /// Lane
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
    /// League
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
        public enum NumberOfGames {
          /// Set the default number of games you plan on bowling in this league or event. You can always add or remove games later.
          public static let help = Strings.tr("Localizable", "league.editor.fields.numberOfGames.help", fallback: "Set the default number of games you plan on bowling in this league or event. You can always add or remove games later.")
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
      public enum Once {
        /// Events
        public static let title = Strings.tr("Localizable", "league.list.once.title", fallback: "Events")
      }
      public enum Repeating {
        /// Leagues
        public static let title = Strings.tr("Localizable", "league.list.repeating.title", fallback: "Leagues")
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
    /// Results
    public static let results = Strings.tr("Localizable", "list.results", fallback: "Results")
    public enum Averages {
      /// Show averages
      public static let showAverages = Strings.tr("Localizable", "list..averages.showAverages", fallback: "Show averages")
      /// All bowlers
      public static let allBowlers = Strings.tr("Localizable", "list.averages.allBowlers", fallback: "All bowlers")
    }
  }
  public enum MatchPlay {
    /// Match Play
    public static let title = Strings.tr("Localizable", "matchPlay.title", fallback: "Match Play")
    public enum Editor {
      public enum Fields {
        public enum Opponent {
          /// Score
          public static let score = Strings.tr("Localizable", "matchPlay.editor.fields.opponent.score", fallback: "Score")
          /// Opponent
          public static let title = Strings.tr("Localizable", "matchPlay.editor.fields.opponent.title", fallback: "Opponent")
        }
        public enum Result {
          /// Indicate your personal result. If you won, select '%@'
          public static func footer(_ p1: Any) -> String {
            return Strings.tr("Localizable", "matchPlay.editor.fields.result.footer", String(describing: p1), fallback: "Indicate your personal result. If you won, select '%@'")
          }
          /// Outcome
          public static let title = Strings.tr("Localizable", "matchPlay.editor.fields.result.title", fallback: "Outcome")
        }
      }
    }
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
    public enum Summary {
      public enum Opponent {
        /// No opponent
        public static let `none` = Strings.tr("Localizable", "matchPlay.summary.opponent.none", fallback: "No opponent")
      }
      public enum Score {
        /// Scored %d
        public static func label(_ p1: Int) -> String {
          return Strings.tr("Localizable", "matchPlay.summary.score.label", p1, fallback: "Scored %d")
        }
        /// Score not recorded
        public static let `none` = Strings.tr("Localizable", "matchPlay.summary.score.none", fallback: "Score not recorded")
      }
    }
  }
  public enum Onboarding {
    /// Get started
    public static let getStarted = Strings.tr("Localizable", "onboarding.getStarted", fallback: "Get started")
    public enum Header {
      /// Approach
      public static let appName = Strings.tr("Localizable", "onboarding.header.appName", fallback: "Approach")
      /// Welcome to a new
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
    /// Opponent
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
      /// Playing against another bowler, or in a match play setting? Keep track of your record by adding your opponents.
      public static let description = Strings.tr("Localizable", "opponent.list.description", fallback: "Playing against another bowler, or in a match play setting? Keep track of your record by adding your opponents.")
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
    /// Alphabetical
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
  public enum QuickLaunch {
    /// New Series
    public static let newSeries = Strings.tr("Localizable", "quickLaunch.newSeries", fallback: "New Series")
    /// Play Now
    public static let playNow = Strings.tr("Localizable", "quickLaunch.playNow", fallback: "Play Now")
    /// Quick Play
    public static let title = Strings.tr("Localizable", "quickLaunch.title", fallback: "Quick Play")
    public enum BowlersList {
      public enum Tip {
        /// Use the button above to start a new series in your last played league.
        public static let message = Strings.tr("Localizable", "quickLaunch.bowlersList.tip.message", fallback: "Use the button above to start a new series in your last played league.")
        /// Quickly start a new series
        public static let title = Strings.tr("Localizable", "quickLaunch.bowlersList.tip.title", fallback: "Quickly start a new series")
      }
    }
    public enum Bowlerslist {
      /// Start bowling
      public static let subtitle = Strings.tr("Localizable", "quickLaunch.bowlerslist.subtitle", fallback: "Start bowling")
      /// Quick Play
      public static let title = Strings.tr("Localizable", "quickLaunch.bowlerslist.title", fallback: "Quick Play")
    }
  }
  public enum Roll {
    /// Ball %d
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
  public enum Scoring {
    /// Scoring
    public static let title = Strings.tr("Localizable", "scoring.title", fallback: "Scoring")
    public enum Editor {
      public enum Fields {
        public enum ManualScore {
          /// By setting a manual score, all the frames for this game will be ignored in all statistics, but the score can be used for your averages and high series.
          public static let help = Strings.tr("Localizable", "scoring.editor.fields.manualScore.help", fallback: "By setting a manual score, all the frames for this game will be ignored in all statistics, but the score can be used for your averages and high series.")
          /// Score
          public static let label = Strings.tr("Localizable", "scoring.editor.fields.manualScore.label", fallback: "Score")
          /// Set manual score?
          public static let title = Strings.tr("Localizable", "scoring.editor.fields.manualScore.title", fallback: "Set manual score?")
        }
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
          /// Pre-bowls are automatically excluded from all statistics until you use them.
          public static let excludedWhenPreBowl = Strings.tr("Localizable", "series.editor.fields.excludeFromStatistics.excludedWhenPreBowl", fallback: "Pre-bowls are automatically excluded from all statistics until you use them.")
          /// This series and all of its games will not count towards any statistics. They will still appear in the app, but won't affect your average or other statistics.
          public static let help = Strings.tr("Localizable", "series.editor.fields.excludeFromStatistics.help", fallback: "This series and all of its games will not count towards any statistics. They will still appear in the app, but won't affect your average or other statistics.")
          /// Exclude from all statistics?
          public static let label = Strings.tr("Localizable", "series.editor.fields.excludeFromStatistics.label", fallback: "Exclude from all statistics?")
          /// Statistics
          public static let title = Strings.tr("Localizable", "series.editor.fields.excludeFromStatistics.title", fallback: "Statistics")
        }
        public enum League {
          /// You can move this series and all of its games and statistics to another league by changing it here. You can always move it back.
          public static let help = Strings.tr("Localizable", "series.editor.fields.league.help", fallback: "You can move this series and all of its games and statistics to another league by changing it here. You can always move it back.")
          /// Move this series to another league?
          public static let label = Strings.tr("Localizable", "series.editor.fields.league.label", fallback: "Move this series to another league?")
          /// When moving a series to a new league, no other properties can be edited.
          public static let otherFieldsDisabled = Strings.tr("Localizable", "series.editor.fields.league.otherFieldsDisabled", fallback: "When moving a series to a new league, no other properties can be edited.")
          /// By moving a series, your average and statistics may change.
          public static let statistics = Strings.tr("Localizable", "series.editor.fields.league.statistics", fallback: "By moving a series, your average and statistics may change.")
          /// League
          public static let title = Strings.tr("Localizable", "series.editor.fields.league.title", fallback: "League")
        }
        public enum Manual {
          /// Add a series without recording the frames. The scores will count towards your average and high series. You can still edit the game details later.
          public static let footer = Strings.tr("Localizable", "series.editor.fields.manual.footer", fallback: "Add a series without recording the frames. The scores will count towards your average and high series. You can still edit the game details later.")
          /// Game %d score
          public static func scoreForGameOrdinal(_ p1: Int) -> String {
            return Strings.tr("Localizable", "series.editor.fields.manual.scoreForGameOrdinal", p1, fallback: "Game %d score")
          }
          /// Set scores manually?
          public static let setScoresManually = Strings.tr("Localizable", "series.editor.fields.manual.setScoresManually", fallback: "Set scores manually?")
          /// Manual Recording
          public static let title = Strings.tr("Localizable", "series.editor.fields.manual.title", fallback: "Manual Recording")
        }
        public enum PreBowl {
          /// Date to apply
          public static let date = Strings.tr("Localizable", "series.editor.fields.preBowl.date", fallback: "Date to apply")
          /// Pre-bowls are excluded from statistics until you use them. You can easily find your recorded pre-bowls in the series list, and modify their date for when you plan to use them.
          public static let help = Strings.tr("Localizable", "series.editor.fields.preBowl.help", fallback: "Pre-bowls are excluded from statistics until you use them. You can easily find your recorded pre-bowls in the series list, and modify their date for when you plan to use them.")
          /// Is this a pre-bowl?
          public static let label = Strings.tr("Localizable", "series.editor.fields.preBowl.label", fallback: "Is this a pre-bowl?")
          /// Pre-Bowl
          public static let title = Strings.tr("Localizable", "series.editor.fields.preBowl.title", fallback: "Pre-Bowl")
          /// Use pre-bowl on date?
          public static let usePreBowl = Strings.tr("Localizable", "series.editor.fields.preBowl.usePreBowl", fallback: "Use pre-bowl on date?")
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
      public enum PreBowl {
        /// Pre-bowled on %@
        public static func preBowledOn(_ p1: Any) -> String {
          return Strings.tr("Localizable", "series.list.preBowl.preBowledOn", String(describing: p1), fallback: "Pre-bowled on %@")
        }
        /// Use a pre-bowl?
        public static let usedAPreBowl = Strings.tr("Localizable", "series.list.preBowl.usedAPreBowl", fallback: "Use a pre-bowl?")
      }
      public enum Scores {
        /// %d — %d
        public static func range(_ p1: Int, _ p2: Int) -> String {
          return Strings.tr("Localizable", "series.list.scores.range", p1, p2, fallback: "%d — %d")
        }
        /// Total
        public static let total = Strings.tr("Localizable", "series.list.scores.total", fallback: "Total")
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
    public enum PreBowlEditor {
      /// Use a pre-bowl
      public static let title = Strings.tr("Localizable", "series.preBowlEditor.title", fallback: "Use a pre-bowl")
      public enum Description {
        /// But it will affect your stats as a bowler as if you had bowled it on the original date, because you did!
        public static let affectsBowlerAverage = Strings.tr("Localizable", "series.preBowlEditor.description.affectsBowlerAverage", fallback: "But it will affect your stats as a bowler as if you had bowled it on the original date, because you did!")
        /// A pre-bowl will appear in your league as if you had bowled it on the latter date, to match your league stats and average.
        public static let affectsLeagueAverage = Strings.tr("Localizable", "series.preBowlEditor.description.affectsLeagueAverage", fallback: "A pre-bowl will appear in your league as if you had bowled it on the latter date, to match your league stats and average.")
        /// You can choose to use a pre-bowl on a date different than when you first bowled it.
        public static let chooseToApply = Strings.tr("Localizable", "series.preBowlEditor.description.chooseToApply", fallback: "You can choose to use a pre-bowl on a date different than when you first bowled it.")
      }
      public enum Fields {
        /// Date to apply pre-bowl
        public static let appliedDate = Strings.tr("Localizable", "series.preBowlEditor.fields.appliedDate", fallback: "Date to apply pre-bowl")
        /// Date originally bowled
        public static let originalDate = Strings.tr("Localizable", "series.preBowlEditor.fields.originalDate", fallback: "Date originally bowled")
        /// Pre-Bowled Series
        public static let series = Strings.tr("Localizable", "series.preBowlEditor.fields.series", fallback: "Pre-Bowled Series")
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
    public enum Achievements {
      /// Earn badges while you improve your bowling game.
      public static let footer = Strings.tr("Localizable", "settings.achievements.footer", fallback: "Earn badges while you improve your bowling game.")
      /// Badges
      public static let title = Strings.tr("Localizable", "settings.achievements.title", fallback: "Badges")
    }
    public enum Acknowledgements {
      /// Acknowledgements
      public static let title = Strings.tr("Localizable", "settings.acknowledgements.title", fallback: "Acknowledgements")
    }
    public enum Analytics {
      /// Privacy Policy
      public static let privacyPolicy = Strings.tr("Localizable", "settings.analytics.privacyPolicy", fallback: "Privacy Policy")
      /// Share anonymous analytics?
      public static let shareAnonymousAnalytics = Strings.tr("Localizable", "settings.analytics.shareAnonymousAnalytics", fallback: "Share anonymous analytics?")
      /// Analytics
      public static let title = Strings.tr("Localizable", "settings.analytics.title", fallback: "Analytics")
      public enum Info {
        /// Approach collects anonymous analytics, such as the number of people using a feature, to improve the app. These analytics are never shared or sold.
        public static let paragraphOne = Strings.tr("Localizable", "settings.analytics.info.paragraphOne", fallback: "Approach collects anonymous analytics, such as the number of people using a feature, to improve the app. These analytics are never shared or sold.")
        /// You can opt in or out of letting us collect these analytics below. This won't affect your usage of the app or any of its features.
        public static let paragraphTwo = Strings.tr("Localizable", "settings.analytics.info.paragraphTwo", fallback: "You can opt in or out of letting us collect these analytics below. This won't affect your usage of the app or any of its features.")
      }
      public enum PrivacyPolicy {
        /// https://tryapproach.app/privacy
        public static let url = Strings.tr("Localizable", "settings.analytics.privacyPolicy.url", fallback: "https://tryapproach.app/privacy")
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
      /// © 2022-2025, Joseph Roque
      public static let copyright = Strings.tr("Localizable", "settings.appInfo.copyright", fallback: "© 2022-2025, Joseph Roque")
      /// App Info
      public static let title = Strings.tr("Localizable", "settings.appInfo.title", fallback: "App Info")
      /// Version
      public static let version = Strings.tr("Localizable", "settings.appInfo.version", fallback: "Version")
    }
    public enum Archive {
      /// An archive of your old data, hidden from the rest of the app.
      public static let footer = Strings.tr("Localizable", "settings.archive.footer", fallback: "An archive of your old data, hidden from the rest of the app.")
      /// Archive
      public static let title = Strings.tr("Localizable", "settings.archive.title", fallback: "Archive")
    }
    public enum Data {
      /// Automatic Backups
      public static let automaticBackups = Strings.tr("Localizable", "settings.data.automaticBackups", fallback: "Automatic Backups")
      /// Export Data
      public static let export = Strings.tr("Localizable", "settings.data.export", fallback: "Export Data")
      /// Import Data
      public static let `import` = Strings.tr("Localizable", "settings.data.import", fallback: "Import Data")
      /// Data
      public static let title = Strings.tr("Localizable", "settings.data.title", fallback: "Data")
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
      /// https://tryapproach.app/view-source
      public static let openSourceRepositoryUrl = Strings.tr("Localizable", "settings.developer.openSourceRepositoryUrl", fallback: "https://tryapproach.app/view-source")
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
      /// Force Crash
      public static let forceCrash = Strings.tr("Localizable", "settings.help.forceCrash", fallback: "Force Crash")
      /// Report Bug
      public static let reportBug = Strings.tr("Localizable", "settings.help.reportBug", fallback: "Report Bug")
      /// Send Feedback
      public static let sendFeedback = Strings.tr("Localizable", "settings.help.sendFeedback", fallback: "Send Feedback")
      /// Help
      public static let title = Strings.tr("Localizable", "settings.help.title", fallback: "Help")
      /// View Source
      public static let viewSource = Strings.tr("Localizable", "settings.help.viewSource", fallback: "View Source")
      public enum Development {
        /// You can view the source for %@ using the links above, to report issues or watch the development of new features
        public static func help(_ p1: Any) -> String {
          return Strings.tr("Localizable", "settings.help.development.help", String(describing: p1), fallback: "You can view the source for %@ using the links above, to report issues or watch the development of new features")
        }
        /// Development
        public static let title = Strings.tr("Localizable", "settings.help.development.title", fallback: "Development")
      }
      public enum ReportBug {
        /// Approach Bug Report (%@)
        public static func subject(_ p1: Any) -> String {
          return Strings.tr("Localizable", "settings.help.reportBug.subject", String(describing: p1), fallback: "Approach Bug Report (%@)")
        }
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
        /// Hide descriptions of statistics in list?
        public static let hideStatisticsDescriptions = Strings.tr("Localizable", "settings.statistics.overall.hideStatisticsDescriptions", fallback: "Hide descriptions of statistics in list?")
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
    /// Share
    public static let title = Strings.tr("Localizable", "sharing.title", fallback: "Share")
    public enum Common {
      public enum ColorScheme {
        /// Dark
        public static let dark = Strings.tr("Localizable", "sharing.common.colorScheme.dark", fallback: "Dark")
        /// Light
        public static let light = Strings.tr("Localizable", "sharing.common.colorScheme.light", fallback: "Light")
        /// Appearance
        public static let title = Strings.tr("Localizable", "sharing.common.colorScheme.title", fallback: "Appearance")
      }
      public enum Watermark {
        /// Made with tryapproach.app
        public static let madeWithApproach = Strings.tr("Localizable", "sharing.common.watermark.madeWithApproach", fallback: "Made with tryapproach.app")
      }
    }
    public enum Game {
      public enum Details {
        /// Bowler
        public static let bowlerName = Strings.tr("Localizable", "sharing.game.details.bowlerName", fallback: "Bowler")
        /// Date
        public static let date = Strings.tr("Localizable", "sharing.game.details.date", fallback: "Date")
        /// %d HIGH
        public static func highScoreLabel(_ p1: Int) -> String {
          return Strings.tr("Localizable", "sharing.game.details.highScoreLabel", p1, fallback: "%d HIGH")
        }
        /// League
        public static let leagueName = Strings.tr("Localizable", "sharing.game.details.leagueName", fallback: "League")
        /// %d LOW
        public static func lowScoreLabel(_ p1: Int) -> String {
          return Strings.tr("Localizable", "sharing.game.details.lowScoreLabel", p1, fallback: "%d LOW")
        }
        /// Summary
        public static let scoreSummary = Strings.tr("Localizable", "sharing.game.details.scoreSummary", fallback: "Summary")
        /// %d TOTAL
        public static func totalLabel(_ p1: Int) -> String {
          return Strings.tr("Localizable", "sharing.game.details.totalLabel", p1, fallback: "%d TOTAL")
        }
        public enum Appearance {
          /// Appearance
          public static let title = Strings.tr("Localizable", "sharing.game.details.appearance.title", fallback: "Appearance")
        }
        public enum ColorPalette {
          /// Grayscale
          public static let grayscale = Strings.tr("Localizable", "sharing.game.details.colorPalette.grayscale", fallback: "Grayscale")
          /// Standard
          public static let plain = Strings.tr("Localizable", "sharing.game.details.colorPalette.plain", fallback: "Standard")
          /// Color Palette
          public static let title = Strings.tr("Localizable", "sharing.game.details.colorPalette.title", fallback: "Color Palette")
        }
        public enum Games {
          /// Games
          public static let title = Strings.tr("Localizable", "sharing.game.details.games.title", fallback: "Games")
        }
        public enum Layout {
          /// Choose a better shaped layout for sharing on social media
          public static let description = Strings.tr("Localizable", "sharing.game.details.layout.description", fallback: "Choose a better shaped layout for sharing on social media")
          /// Horizontal
          public static let horizontal = Strings.tr("Localizable", "sharing.game.details.layout.horizontal", fallback: "Horizontal")
          /// Rectangular
          public static let rectangular = Strings.tr("Localizable", "sharing.game.details.layout.rectangular", fallback: "Rectangular")
          /// Layout
          public static let title = Strings.tr("Localizable", "sharing.game.details.layout.title", fallback: "Layout")
        }
        public enum SelectedGame {
          /// Game
          public static let title = Strings.tr("Localizable", "sharing.game.details.selectedGame.title", fallback: "Game")
        }
      }
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
    public enum Series {
      public enum Chart {
        public enum Range {
          /// %d to %d
          public static func description(_ p1: Int, _ p2: Int) -> String {
            return Strings.tr("Localizable", "sharing.series.chart.range.description", p1, p2, fallback: "%d to %d")
          }
          /// %d Maximum
          public static func maximum(_ p1: Int) -> String {
            return Strings.tr("Localizable", "sharing.series.chart.range.maximum", p1, fallback: "%d Maximum")
          }
          /// %d Minimum
          public static func minimum(_ p1: Int) -> String {
            return Strings.tr("Localizable", "sharing.series.chart.range.minimum", p1, fallback: "%d Minimum")
          }
          /// Chart Range
          public static let title = Strings.tr("Localizable", "sharing.series.chart.range.title", fallback: "Chart Range")
        }
      }
      public enum Details {
        /// Bowler
        public static let bowlerName = Strings.tr("Localizable", "sharing.series.details.bowlerName", fallback: "Bowler")
        /// Date
        public static let date = Strings.tr("Localizable", "sharing.series.details.date", fallback: "Date")
        /// High
        public static let highScore = Strings.tr("Localizable", "sharing.series.details.highScore", fallback: "High")
        /// %d HIGH
        public static func highScoreLabel(_ p1: Int) -> String {
          return Strings.tr("Localizable", "sharing.series.details.highScoreLabel", p1, fallback: "%d HIGH")
        }
        /// League
        public static let leagueName = Strings.tr("Localizable", "sharing.series.details.leagueName", fallback: "League")
        /// Low
        public static let lowScore = Strings.tr("Localizable", "sharing.series.details.lowScore", fallback: "Low")
        /// %d LOW
        public static func lowScoreLabel(_ p1: Int) -> String {
          return Strings.tr("Localizable", "sharing.series.details.lowScoreLabel", p1, fallback: "%d LOW")
        }
        /// Summary
        public static let scoreSummary = Strings.tr("Localizable", "sharing.series.details.scoreSummary", fallback: "Summary")
        /// %d TOTAL
        public static func totalLabel(_ p1: Int) -> String {
          return Strings.tr("Localizable", "sharing.series.details.totalLabel", p1, fallback: "%d TOTAL")
        }
      }
    }
    public enum Tabs {
      /// Games
      public static let games = Strings.tr("Localizable", "sharing.tabs.games", fallback: "Games")
      /// Series
      public static let series = Strings.tr("Localizable", "sharing.tabs.series", fallback: "Series")
      /// Statistic
      public static let statistic = Strings.tr("Localizable", "sharing.tabs.statistic", fallback: "Statistic")
      /// Share Format
      public static let title = Strings.tr("Localizable", "sharing.tabs.title", fallback: "Share Format")
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
      public enum Aces {
        /// With your first roll, knock over only the head pin, and the left and right 3-pins
        public static let description = Strings.tr("Localizable", "statistics.categories.aces.description", fallback: "With your first roll, knock over only the head pin, and the left and right 3-pins")
        /// Aces
        public static let title = Strings.tr("Localizable", "statistics.categories.aces.title", fallback: "Aces")
      }
      public enum ChopOffs {
        /// With your first roll, knock over only the head pin, and the 2- and 3-pins on the same side
        public static let description = Strings.tr("Localizable", "statistics.categories.chopOffs.description", fallback: "With your first roll, knock over only the head pin, and the 2- and 3-pins on the same side")
        /// Chop Offs
        public static let title = Strings.tr("Localizable", "statistics.categories.chopOffs.title", fallback: "Chop Offs")
      }
      public enum FirstRoll {
        /// The first roll is any time you throw a ball at a full deck of pins, either on the first ball of a frame, or after a strike or spare in the 10th frame
        public static let description = Strings.tr("Localizable", "statistics.categories.firstRoll.description", fallback: "The first roll is any time you throw a ball at a full deck of pins, either on the first ball of a frame, or after a strike or spare in the 10th frame")
        /// First Roll
        public static let title = Strings.tr("Localizable", "statistics.categories.firstRoll.title", fallback: "First Roll")
      }
      public enum Fives {
        /// With your first roll, knock over only the 2- and 3-pins on the same side
        public static let description = Strings.tr("Localizable", "statistics.categories.fives.description", fallback: "With your first roll, knock over only the 2- and 3-pins on the same side")
        /// Fives
        public static let title = Strings.tr("Localizable", "statistics.categories.fives.title", fallback: "Fives")
      }
      public enum Fouls {
        /// Fouls
        public static let title = Strings.tr("Localizable", "statistics.categories.fouls.title", fallback: "Fouls")
      }
      public enum HeadPins {
        /// Head Pins
        public static let title = Strings.tr("Localizable", "statistics.categories.headPins.title", fallback: "Head Pins")
        public enum Description {
          /// With your first roll, knock over only the head pin, or the head pin and exactly one of the 2-pins
          public static let withH2 = Strings.tr("Localizable", "statistics.categories.headPins.description.withH2", fallback: "With your first roll, knock over only the head pin, or the head pin and exactly one of the 2-pins")
          /// With your first roll, knock over only the head pin
          public static let withoutH2 = Strings.tr("Localizable", "statistics.categories.headPins.description.withoutH2", fallback: "With your first roll, knock over only the head pin")
        }
      }
      public enum MatchPlay {
        /// Match Play
        public static let title = Strings.tr("Localizable", "statistics.categories.matchPlay.title", fallback: "Match Play")
      }
      public enum MiddleHits {
        /// A middle hit is any time you throw the first roll of a frame and you knock over at least the head pin
        public static let description = Strings.tr("Localizable", "statistics.categories.middleHits.description", fallback: "A middle hit is any time you throw the first roll of a frame and you knock over at least the head pin")
        /// Middle Hits
        public static let title = Strings.tr("Localizable", "statistics.categories.middleHits.title", fallback: "Middle Hits")
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
      public enum Splits {
        /// Splits
        public static let title = Strings.tr("Localizable", "statistics.categories.splits.title", fallback: "Splits")
        public enum Description {
          /// With your first roll, knock over the head pin and exactly one of the 3-pins, or the head pin, exactly one of the 3-pins and the 2-pin on the opposite side
          public static let withBonus = Strings.tr("Localizable", "statistics.categories.splits.description.withBonus", fallback: "With your first roll, knock over the head pin and exactly one of the 3-pins, or the head pin, exactly one of the 3-pins and the 2-pin on the opposite side")
          /// With your first roll, knock over the head pin and exactly one of the 3-pins
          public static let withoutBonus = Strings.tr("Localizable", "statistics.categories.splits.description.withoutBonus", fallback: "With your first roll, knock over the head pin and exactly one of the 3-pins")
        }
      }
      public enum StrikesAndSpares {
        /// Any time you clear all of the remaining pins in one or two rolls
        public static let description = Strings.tr("Localizable", "statistics.categories.strikesAndSpares.description", fallback: "Any time you clear all of the remaining pins in one or two rolls")
        /// Strikes and Spares
        public static let title = Strings.tr("Localizable", "statistics.categories.strikesAndSpares.title", fallback: "Strikes and Spares")
      }
      public enum Taps {
        /// With your first roll, knock over every pin except one of the 2-pins for a total of 13. Also known as a Left or a Right
        public static let description = Strings.tr("Localizable", "statistics.categories.taps.description", fallback: "With your first roll, knock over every pin except one of the 2-pins for a total of 13. Also known as a Left or a Right")
        /// Taps
        public static let title = Strings.tr("Localizable", "statistics.categories.taps.title", fallback: "Taps")
      }
      public enum Threes {
        /// With your first roll, knock over exactly one of the 3-pins
        public static let description = Strings.tr("Localizable", "statistics.categories.threes.description", fallback: "With your first roll, knock over exactly one of the 3-pins")
        /// Threes
        public static let title = Strings.tr("Localizable", "statistics.categories.threes.title", fallback: "Threes")
      }
      public enum Twelves {
        /// With your first roll, knock over every pin except one of the 3-pins for a total of 12
        public static let description = Strings.tr("Localizable", "statistics.categories.twelves.description", fallback: "With your first roll, knock over every pin except one of the 3-pins for a total of 12")
        /// Twelves
        public static let title = Strings.tr("Localizable", "statistics.categories.twelves.title", fallback: "Twelves")
      }
    }
    public enum Charts {
      /// You may have picked too narrow a window. We can't show useful charts for games. Try examining the statistics below instead.
      public static let filterTooNarrow = Strings.tr("Localizable", "statistics.charts.filterTooNarrow", fallback: "You may have picked too narrow a window. We can't show useful charts for games. Try examining the statistics below instead.")
      /// There doesn't seem to be any data available.
      public static let unavailable = Strings.tr("Localizable", "statistics.charts.unavailable", fallback: "There doesn't seem to be any data available.")
      public enum AxesLabels {
        /// Date
        public static let date = Strings.tr("Localizable", "statistics.charts.axesLabels.date", fallback: "Date")
        /// Game
        public static let game = Strings.tr("Localizable", "statistics.charts.axesLabels.game", fallback: "Game")
      }
      public enum Unavailable {
        /// You may not have bowled enough games yet, or your filters are excluding too many games.
        public static let description = Strings.tr("Localizable", "statistics.charts.unavailable.description", fallback: "You may not have bowled enough games yet, or your filters are excluding too many games.")
      }
    }
    public enum Description {
      /// Average value of pins you've left standing at the end of frames per game
      public static let averagePinsLeftOnDeck = Strings.tr("Localizable", "statistics.description.averagePinsLeftOnDeck", fallback: "Average value of pins you've left standing at the end of frames per game")
      /// Average value of pins knocked down on the first roll of a frame
      public static let firstRollAverage = Strings.tr("Localizable", "statistics.description.firstRollAverage", fallback: "Average value of pins knocked down on the first roll of a frame")
      /// Number of times you marked a roll as a foul
      public static let fouls = Strings.tr("Localizable", "statistics.description.fouls", fallback: "Number of times you marked a roll as a foul")
      /// Average score across all counted games
      public static let gameAverage = Strings.tr("Localizable", "statistics.description.gameAverage", fallback: "Average score across all counted games")
      /// Highest total for series of 10 games
      public static let highSeriesOf10 = Strings.tr("Localizable", "statistics.description.highSeriesOf10", fallback: "Highest total for series of 10 games")
      /// Highest total for series of 12 games
      public static let highSeriesOf12 = Strings.tr("Localizable", "statistics.description.highSeriesOf12", fallback: "Highest total for series of 12 games")
      /// Highest total for series of 15 games
      public static let highSeriesOf15 = Strings.tr("Localizable", "statistics.description.highSeriesOf15", fallback: "Highest total for series of 15 games")
      /// Highest total for series of 2 games
      public static let highSeriesOf2 = Strings.tr("Localizable", "statistics.description.highSeriesOf2", fallback: "Highest total for series of 2 games")
      /// Highest total for series of 20 games
      public static let highSeriesOf20 = Strings.tr("Localizable", "statistics.description.highSeriesOf20", fallback: "Highest total for series of 20 games")
      /// Highest total for series of 3 games
      public static let highSeriesOf3 = Strings.tr("Localizable", "statistics.description.highSeriesOf3", fallback: "Highest total for series of 3 games")
      /// Highest total for series of 4 games
      public static let highSeriesOf4 = Strings.tr("Localizable", "statistics.description.highSeriesOf4", fallback: "Highest total for series of 4 games")
      /// Highest total for series of 5 games
      public static let highSeriesOf5 = Strings.tr("Localizable", "statistics.description.highSeriesOf5", fallback: "Highest total for series of 5 games")
      /// Highest total for series of 8 games
      public static let highSeriesOf8 = Strings.tr("Localizable", "statistics.description.highSeriesOf8", fallback: "Highest total for series of 8 games")
      /// Highest score across all counted games
      public static let highSingle = Strings.tr("Localizable", "statistics.description.highSingle", fallback: "Highest score across all counted games")
      /// Number of match plays you've marked as a 'Loss'
      public static let matchesLost = Strings.tr("Localizable", "statistics.description.matchesLost", fallback: "Number of match plays you've marked as a 'Loss'")
      /// Number of match plays you've recorded
      public static let matchesPlayed = Strings.tr("Localizable", "statistics.description.matchesPlayed", fallback: "Number of match plays you've recorded")
      /// Number of match plays you've marked as a 'Tie'
      public static let matchesTied = Strings.tr("Localizable", "statistics.description.matchesTied", fallback: "Number of match plays you've marked as a 'Tie'")
      /// Number of match plays you've marked as a 'Win'
      public static let matchesWon = Strings.tr("Localizable", "statistics.description.matchesWon", fallback: "Number of match plays you've marked as a 'Win'")
      /// Total number of games you've recorded
      public static let numberOfGames = Strings.tr("Localizable", "statistics.description.numberOfGames", fallback: "Total number of games you've recorded")
      /// Number of times you've successfully converted a spare opportunity. Failing to spare a split and other difficult shots do not affect this statistic
      public static let spareChances = Strings.tr("Localizable", "statistics.description.spareChances", fallback: "Number of times you've successfully converted a spare opportunity. Failing to spare a split and other difficult shots do not affect this statistic")
      /// Percent of the time a hit on the middle was a strike
      public static let strikeMiddleHits = Strings.tr("Localizable", "statistics.description.strikeMiddleHits", fallback: "Percent of the time a hit on the middle was a strike")
      /// Total sum of the scores across all counted games
      public static let totalPinfall = Strings.tr("Localizable", "statistics.description.totalPinfall", fallback: "Total sum of the scores across all counted games")
      /// Total value of pins you've left standing at the end of frames across all counted games
      public static let totalPinsLeftOnDeck = Strings.tr("Localizable", "statistics.description.totalPinsLeftOnDeck", fallback: "Total value of pins you've left standing at the end of frames across all counted games")
      /// Total number of times you've rolled a ball
      public static let totalRolls = Strings.tr("Localizable", "statistics.description.totalRolls", fallback: "Total number of times you've rolled a ball")
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
      /// All games
      public static let allGames = Strings.tr("Localizable", "statistics.filter.allGames", fallback: "All games")
      /// Filter by game
      public static let filterByGame = Strings.tr("Localizable", "statistics.filter.filterByGame", fallback: "Filter by game")
      /// Filter Statistics
      public static let title = Strings.tr("Localizable", "statistics.filter.title", fallback: "Filter Statistics")
      public enum Aggregation {
        /// Accumulate
        public static let accumulate = Strings.tr("Localizable", "statistics.filter.aggregation.accumulate", fallback: "Accumulate")
        /// By Game
        public static let byGame = Strings.tr("Localizable", "statistics.filter.aggregation.byGame", fallback: "By Game")
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
          /// Gear used
          public static let title = Strings.tr("Localizable", "statistics.filter.label.gear.title", fallback: "Gear used")
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
          /// Ending on
          public static let ends = Strings.tr("Localizable", "statistics.filter.label.series.ends", fallback: "Ending on")
          /// Beginning from
          public static let starts = Strings.tr("Localizable", "statistics.filter.label.series.starts", fallback: "Beginning from")
          /// Series Date
          public static let title = Strings.tr("Localizable", "statistics.filter.label.series.title", fallback: "Series Date")
          public enum Alley {
            /// Material
            public static let material = Strings.tr("Localizable", "statistics.filter.label.series.alley.material", fallback: "Material")
            /// Mechanism
            public static let mechanism = Strings.tr("Localizable", "statistics.filter.label.series.alley.mechanism", fallback: "Mechanism")
            /// Pin Base
            public static let pinBase = Strings.tr("Localizable", "statistics.filter.label.series.alley.pinBase", fallback: "Pin Base")
            /// Pinfall
            public static let pinFall = Strings.tr("Localizable", "statistics.filter.label.series.alley.pinFall", fallback: "Pinfall")
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
      /// New
      public static let new = Strings.tr("Localizable", "statistics.list.new", fallback: "New")
      /// Hide descriptions?
      public static let statisticsDescription = Strings.tr("Localizable", "statistics.list.statisticsDescription", fallback: "Hide descriptions?")
      public enum HideZeroStatistics {
        /// Some statistics have been hidden from the list because they were empty. You can choose to show these statistics to get a better view of your play.
        public static let help = Strings.tr("Localizable", "statistics.list.hideZeroStatistics.help", fallback: "Some statistics have been hidden from the list because they were empty. You can choose to show these statistics to get a better view of your play.")
      }
      public enum StatisticsDescription {
        /// You can hide the descriptions to see more stats at once.
        public static let help = Strings.tr("Localizable", "statistics.list.statisticsDescription.help", fallback: "You can hide the descriptions to see more stats at once.")
        public enum Tip {
          /// All the statistics come with clarifying descriptions. When you've learned them and no longer need the help, at the bottom of this list is a setting to hide these descriptions.
          public static let message = Strings.tr("Localizable", "statistics.list.statisticsDescription.tip.message", fallback: "All the statistics come with clarifying descriptions. When you've learned them and no longer need the help, at the bottom of this list is a setting to hide these descriptions.")
          /// Helpful descriptions
          public static let title = Strings.tr("Localizable", "statistics.list.statisticsDescription.tip.title", fallback: "Helpful descriptions")
        }
      }
    }
    public enum Overview {
      /// Recents
      public static let recents = Strings.tr("Localizable", "statistics.overview.recents", fallback: "Recents")
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
    public enum Picker {
      /// Statistic
      public static let title = Strings.tr("Localizable", "statistics.picker.title", fallback: "Statistic")
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
      /// Average First Roll Pinfall
      public static let firstRollAverage = Strings.tr("Localizable", "statistics.title.firstRollAverage", fallback: "Average First Roll Pinfall")
      /// Fives
      public static let fives = Strings.tr("Localizable", "statistics.title.fives", fallback: "Fives")
      /// Fives Spared
      public static let fivesSpared = Strings.tr("Localizable", "statistics.title.fivesSpared", fallback: "Fives Spared")
      /// Fouls
      public static let fouls = Strings.tr("Localizable", "statistics.title.fouls", fallback: "Fouls")
      /// Average
      public static let gameAverage = Strings.tr("Localizable", "statistics.title.gameAverage", fallback: "Average")
      /// Head Pins
      public static let headPins = Strings.tr("Localizable", "statistics.title.headPins", fallback: "Head Pins")
      /// Head Pins Spared
      public static let headPinsSpared = Strings.tr("Localizable", "statistics.title.headPinsSpared", fallback: "Head Pins Spared")
      /// High Series of 10
      public static let highSeriesOf10 = Strings.tr("Localizable", "statistics.title.highSeriesOf10", fallback: "High Series of 10")
      /// High Series of 12
      public static let highSeriesOf12 = Strings.tr("Localizable", "statistics.title.highSeriesOf12", fallback: "High Series of 12")
      /// High Series of 15
      public static let highSeriesOf15 = Strings.tr("Localizable", "statistics.title.highSeriesOf15", fallback: "High Series of 15")
      /// High Series of 2
      public static let highSeriesOf2 = Strings.tr("Localizable", "statistics.title.highSeriesOf2", fallback: "High Series of 2")
      /// High Series of 20
      public static let highSeriesOf20 = Strings.tr("Localizable", "statistics.title.highSeriesOf20", fallback: "High Series of 20")
      /// High Series of 3
      public static let highSeriesOf3 = Strings.tr("Localizable", "statistics.title.highSeriesOf3", fallback: "High Series of 3")
      /// High Series of 4
      public static let highSeriesOf4 = Strings.tr("Localizable", "statistics.title.highSeriesOf4", fallback: "High Series of 4")
      /// High Series of 5
      public static let highSeriesOf5 = Strings.tr("Localizable", "statistics.title.highSeriesOf5", fallback: "High Series of 5")
      /// High Series of 8
      public static let highSeriesOf8 = Strings.tr("Localizable", "statistics.title.highSeriesOf8", fallback: "High Series of 8")
      /// High Single
      public static let highSingle = Strings.tr("Localizable", "statistics.title.highSingle", fallback: "High Single")
      /// Left Chop Offs
      public static let leftChopOffs = Strings.tr("Localizable", "statistics.title.leftChopOffs", fallback: "Left Chop Offs")
      /// Left Chop Offs Spared
      public static let leftChopOffsSpared = Strings.tr("Localizable", "statistics.title.leftChopOffsSpared", fallback: "Left Chop Offs Spared")
      /// Left Fives
      public static let leftFives = Strings.tr("Localizable", "statistics.title.leftFives", fallback: "Left Fives")
      /// Left Fives Spared
      public static let leftFivesSpared = Strings.tr("Localizable", "statistics.title.leftFivesSpared", fallback: "Left Fives Spared")
      /// Left of Middle Hits
      public static let leftOfMiddleHits = Strings.tr("Localizable", "statistics.title.leftOfMiddleHits", fallback: "Left of Middle Hits")
      /// Left Splits
      public static let leftSplits = Strings.tr("Localizable", "statistics.title.leftSplits", fallback: "Left Splits")
      /// Left Splits Spared
      public static let leftSplitsSpared = Strings.tr("Localizable", "statistics.title.leftSplitsSpared", fallback: "Left Splits Spared")
      /// Lefts
      public static let leftTaps = Strings.tr("Localizable", "statistics.title.leftTaps", fallback: "Lefts")
      /// Lefts Spared
      public static let leftTapsSpared = Strings.tr("Localizable", "statistics.title.leftTapsSpared", fallback: "Lefts Spared")
      /// Left Threes
      public static let leftThrees = Strings.tr("Localizable", "statistics.title.leftThrees", fallback: "Left Threes")
      /// Left Threes Spared
      public static let leftThreesSpared = Strings.tr("Localizable", "statistics.title.leftThreesSpared", fallback: "Left Threes Spared")
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
      /// Right Fives
      public static let rightFives = Strings.tr("Localizable", "statistics.title.rightFives", fallback: "Right Fives")
      /// Right Fives Spared
      public static let rightFivesSpared = Strings.tr("Localizable", "statistics.title.rightFivesSpared", fallback: "Right Fives Spared")
      /// Right of Middle Hits
      public static let rightOfMiddleHits = Strings.tr("Localizable", "statistics.title.rightOfMiddleHits", fallback: "Right of Middle Hits")
      /// Right Splits
      public static let rightSplits = Strings.tr("Localizable", "statistics.title.rightSplits", fallback: "Right Splits")
      /// Right Splits Spared
      public static let rightSplitsSpared = Strings.tr("Localizable", "statistics.title.rightSplitsSpared", fallback: "Right Splits Spared")
      /// Rights
      public static let rightTaps = Strings.tr("Localizable", "statistics.title.rightTaps", fallback: "Rights")
      /// Rights Spared
      public static let rightTapsSpared = Strings.tr("Localizable", "statistics.title.rightTapsSpared", fallback: "Rights Spared")
      /// Right Threes
      public static let rightThrees = Strings.tr("Localizable", "statistics.title.rightThrees", fallback: "Right Threes")
      /// Right Threes Spared
      public static let rightThreesSpared = Strings.tr("Localizable", "statistics.title.rightThreesSpared", fallback: "Right Threes Spared")
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
      /// Taps
      public static let taps = Strings.tr("Localizable", "statistics.title.taps", fallback: "Taps")
      /// Taps Spared
      public static let tapsSpared = Strings.tr("Localizable", "statistics.title.tapsSpared", fallback: "Taps Spared")
      /// Threes
      public static let threes = Strings.tr("Localizable", "statistics.title.threes", fallback: "Threes")
      /// Threes Spared
      public static let threesSpared = Strings.tr("Localizable", "statistics.title.threesSpared", fallback: "Threes Spared")
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
      /// Preview
      public static let preview = Strings.tr("Localizable", "widget.builder.preview", fallback: "Preview")
      /// Statistic to Display
      public static let statistic = Strings.tr("Localizable", "widget.builder.statistic", fallback: "Statistic to Display")
      /// Timeline
      public static let timeline = Strings.tr("Localizable", "widget.builder.timeline", fallback: "Timeline")
      /// Create a Widget
      public static let title = Strings.tr("Localizable", "widget.builder.title", fallback: "Create a Widget")
      public enum Filter {
        /// Filter to a specific league for a better look at how you perform in one setting.
        public static let description = Strings.tr("Localizable", "widget.builder.filter.description", fallback: "Filter to a specific league for a better look at how you perform in one setting.")
      }
      public enum TapThrough {
        /// When you're done adding widgets, try tapping them from the home screen and see your statistics in full view.
        public static let message = Strings.tr("Localizable", "widget.builder.tapThrough.message", fallback: "When you're done adding widgets, try tapping them from the home screen and see your statistics in full view.")
        /// Widgets are a convenient way to access your favourite stats!
        public static let title = Strings.tr("Localizable", "widget.builder.tapThrough.title", fallback: "Widgets are a convenient way to access your favourite stats!")
      }
      public enum Timeline {
        /// Choose a shorter timeline to only be shown recent stats, or a longer timeline to see measurements over multiple years.
        public static let description = Strings.tr("Localizable", "widget.builder.timeline.description", fallback: "Choose a shorter timeline to only be shown recent stats, or a longer timeline to see measurements over multiple years.")
      }
    }
    public enum Chart {
      /// Not enough data
      public static let noData = Strings.tr("Localizable", "widget.chart.noData", fallback: "Not enough data")
      /// Error
      public static let unavailable = Strings.tr("Localizable", "widget.chart.unavailable", fallback: "Error")
      public enum Placeholder {
        /// Not enough data
        public static let notEnoughData = Strings.tr("Localizable", "widget.chart.placeholder.notEnoughData", fallback: "Not enough data")
        /// What does this mean?
        public static let whatDoesThisMean = Strings.tr("Localizable", "widget.chart.placeholder.whatDoesThisMean", fallback: "What does this mean?")
      }
    }
    public enum Help {
      /// Help
      public static let title = Strings.tr("Localizable", "widget.help.title", fallback: "Help")
      public enum Error {
        /// This is probably an unexpected error. First, try restarting the app.
        public static let description1 = Strings.tr("Localizable", "widget.help.error.description1", fallback: "This is probably an unexpected error. First, try restarting the app.")
        /// If that doesn't help, you can report the bug through the app settings and we'll do our best to help you resolve the problem.
        public static let description2 = Strings.tr("Localizable", "widget.help.error.description2", fallback: "If that doesn't help, you can report the bug through the app settings and we'll do our best to help you resolve the problem.")
        /// Why does my widget show an error message?
        public static let title = Strings.tr("Localizable", "widget.help.error.title", fallback: "Why does my widget show an error message?")
      }
      public enum NotEnoughData {
        /// Approach works best when you've been using the app for a short while and have recorded a few games. Theses statistics are compiled and showed as trends over time.
        public static let description1 = Strings.tr("Localizable", "widget.help.notEnoughData.description1", fallback: "Approach works best when you've been using the app for a short while and have recorded a few games. Theses statistics are compiled and showed as trends over time.")
        /// Try to make sure you add at least 2 games on different days to start seeing your statistics appear.
        public static let description2 = Strings.tr("Localizable", "widget.help.notEnoughData.description2", fallback: "Try to make sure you add at least 2 games on different days to start seeing your statistics appear.")
        /// Why does my widget say 'Not enough data'?
        public static let title = Strings.tr("Localizable", "widget.help.notEnoughData.title", fallback: "Why does my widget say 'Not enough data'?")
      }
    }
    public enum LayoutBuilder {
      /// You don't have any widgets yet. Tap the '+' to add
      public static let addNewInstructions = Strings.tr("Localizable", "widget.layoutBuilder.addNewInstructions", fallback: "You don't have any widgets yet. Tap the '+' to add")
      /// Tap and hold widgets to reorder
      public static let reorderInstructions = Strings.tr("Localizable", "widget.layoutBuilder.reorderInstructions", fallback: "Tap and hold widgets to reorder")
      /// Tap to change layout
      public static let tapToChange = Strings.tr("Localizable", "widget.layoutBuilder.tapToChange", fallback: "Tap to change layout")
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
