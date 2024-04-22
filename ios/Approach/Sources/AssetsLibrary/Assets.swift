// swiftlint:disable all
// Generated using SwiftGen — https://github.com/SwiftGen/SwiftGen

#if os(macOS)
  import AppKit
#elseif os(iOS)
  import UIKit
#elseif os(tvOS) || os(watchOS)
  import UIKit
#endif
#if canImport(SwiftUI)
  import SwiftUI
#endif

// Deprecated typealiases
@available(*, deprecated, renamed: "ColorAsset.Color", message: "This typealias will be removed in SwiftGen 7.0")
public typealias AssetColorTypeAlias = ColorAsset.Color
@available(*, deprecated, renamed: "ImageAsset.Image", message: "This typealias will be removed in SwiftGen 7.0")
public typealias AssetImageTypeAlias = ImageAsset.Image

// swiftlint:disable superfluous_disable_command file_length implicit_return

// MARK: - Asset Catalogs

// swiftlint:disable identifier_name line_length nesting type_body_length type_name
public enum Asset {
  public enum Colors {
    public enum Action {
      public static let `default` = ColorAsset(name: "Action/Default")
    }
    public enum Background {
      public static let `default` = ColorAsset(name: "Background/Default")
      public static let secondary = ColorAsset(name: "Background/Secondary")
    }
    public enum Charts {
      public enum Averaging {
        public static let axes = ColorAsset(name: "Charts/Averaging/Axes")
        public enum Compact {
          public static let axes = ColorAsset(name: "Charts/Averaging/Compact/Axes")
          public static let lineMark = ColorAsset(name: "Charts/Averaging/Compact/LineMark")
          public static let negativeChange = ColorAsset(name: "Charts/Averaging/Compact/NegativeChange")
          public static let positiveChange = ColorAsset(name: "Charts/Averaging/Compact/PositiveChange")
        }
        public static let lineMark = ColorAsset(name: "Charts/Averaging/LineMark")
      }
      public static let background = ColorAsset(name: "Charts/Background")
      public enum Counting {
        public static let areaMark = ColorAsset(name: "Charts/Counting/AreaMark")
        public static let axes = ColorAsset(name: "Charts/Counting/Axes")
        public static let barMark = ColorAsset(name: "Charts/Counting/BarMark")
        public enum Compact {
          public static let areaMark = ColorAsset(name: "Charts/Counting/Compact/AreaMark")
          public static let axes = ColorAsset(name: "Charts/Counting/Compact/Axes")
          public static let barMark = ColorAsset(name: "Charts/Counting/Compact/BarMark")
          public static let lineMark = ColorAsset(name: "Charts/Counting/Compact/LineMark")
        }
        public static let lineMark = ColorAsset(name: "Charts/Counting/LineMark")
      }
      public static let foreground = ColorAsset(name: "Charts/Foreground")
      public enum Game {
        public static let areaMark = ColorAsset(name: "Charts/Game/AreaMark")
        public static let lineMark = ColorAsset(name: "Charts/Game/LineMark")
      }
      public enum List {
        public static let background = ColorAsset(name: "Charts/List/Background")
      }
      public enum Mock {
        public static let areaMark = ColorAsset(name: "Charts/Mock/AreaMark")
        public static let lineMark = ColorAsset(name: "Charts/Mock/LineMark")
      }
      public enum Percentage {
        public static let axes = ColorAsset(name: "Charts/Percentage/Axes")
        public enum Compact {
          public static let axes = ColorAsset(name: "Charts/Percentage/Compact/Axes")
          public static let lineMark = ColorAsset(name: "Charts/Percentage/Compact/LineMark")
          public static let negativeChange = ColorAsset(name: "Charts/Percentage/Compact/NegativeChange")
          public static let positiveChange = ColorAsset(name: "Charts/Percentage/Compact/PositiveChange")
        }
        public static let lineMark = ColorAsset(name: "Charts/Percentage/LineMark")
      }
      public enum Series {
        public static let areaMark = ColorAsset(name: "Charts/Series/AreaMark")
        public static let lineMark = ColorAsset(name: "Charts/Series/LineMark")
      }
    }
    public enum Chip {
      public static let infoBackground = ColorAsset(name: "Chip/infoBackground")
      public static let infoForeground = ColorAsset(name: "Chip/infoForeground")
      public static let plainBackground = ColorAsset(name: "Chip/plainBackground")
      public static let plainForeground = ColorAsset(name: "Chip/plainForeground")
      public static let primaryBackground = ColorAsset(name: "Chip/primaryBackground")
      public static let primaryForeground = ColorAsset(name: "Chip/primaryForeground")
    }
    public enum Destructive {
      public static let `default` = ColorAsset(name: "Destructive/Default")
    }
    public enum Error {
      public static let `default` = ColorAsset(name: "Error/Default")
      public static let light = ColorAsset(name: "Error/Light")
    }
    public enum Filters {
      public static let alley = ColorAsset(name: "Filters/Alley")
      public static let gear = ColorAsset(name: "Filters/Gear")
      public static let league = ColorAsset(name: "Filters/League")
      public static let matchPlay = ColorAsset(name: "Filters/MatchPlay")
      public static let series = ColorAsset(name: "Filters/Series")
      public enum Text {
        public static let onAlley = ColorAsset(name: "Filters/Text/OnAlley")
        public static let onGear = ColorAsset(name: "Filters/Text/OnGear")
        public static let onLeague = ColorAsset(name: "Filters/Text/OnLeague")
        public static let onMatchPlay = ColorAsset(name: "Filters/Text/OnMatchPlay")
        public static let onSeries = ColorAsset(name: "Filters/Text/OnSeries")
      }
    }
    public enum Frame {
      public static let pinTint = ColorAsset(name: "Frame/PinTint")
    }
    public enum List {
      public static let selection = ColorAsset(name: "List/Selection")
    }
    public enum MatchPlay {
      public static let lost = ColorAsset(name: "MatchPlay/Lost")
      public static let won = ColorAsset(name: "MatchPlay/Won")
    }
    public enum Primary {
      public static let `default` = ColorAsset(name: "Primary/Default")
      public static let light = ColorAsset(name: "Primary/Light")
    }
    public enum ScoreSheet {
      public enum Background {
        public static let `default` = ColorAsset(name: "ScoreSheet/Background/Default")
        public static let highlight = ColorAsset(name: "ScoreSheet/Background/Highlight")
        public static let plain = ColorAsset(name: "ScoreSheet/Background/Plain")
        public static let pride = ColorAsset(name: "ScoreSheet/Background/Pride")
      }
      public enum Border {
        public static let `default` = ColorAsset(name: "ScoreSheet/Border/Default")
        public static let defaultStrong = ColorAsset(name: "ScoreSheet/Border/DefaultStrong")
        public static let plain = ColorAsset(name: "ScoreSheet/Border/Plain")
        public static let plainStrong = ColorAsset(name: "ScoreSheet/Border/PlainStrong")
        public static let pride = ColorAsset(name: "ScoreSheet/Border/Pride")
        public static let prideStrong = ColorAsset(name: "ScoreSheet/Border/PrideStrong")
      }
      public enum Card {
        public static let `default` = ColorAsset(name: "ScoreSheet/Card/Default")
        public static let plain = ColorAsset(name: "ScoreSheet/Card/Plain")
        public static let pride = ColorAsset(name: "ScoreSheet/Card/Pride")
      }
      public enum Rail {
        public static let `default` = ColorAsset(name: "ScoreSheet/Rail/Default")
        public static let highlight = ColorAsset(name: "ScoreSheet/Rail/Highlight")
        public static let plain = ColorAsset(name: "ScoreSheet/Rail/Plain")
        public static let pride = ColorAsset(name: "ScoreSheet/Rail/Pride")
      }
      public enum Text {
        public enum OnBackground {
          public static let `default` = ColorAsset(name: "ScoreSheet/Text/OnBackground/Default")
          public static let foul = ColorAsset(name: "ScoreSheet/Text/OnBackground/Foul")
          public static let highlight = ColorAsset(name: "ScoreSheet/Text/OnBackground/Highlight")
          public static let highlightFoul = ColorAsset(name: "ScoreSheet/Text/OnBackground/HighlightFoul")
          public static let plain = ColorAsset(name: "ScoreSheet/Text/OnBackground/Plain")
          public static let pride = ColorAsset(name: "ScoreSheet/Text/OnBackground/Pride")
          public static let secondary = ColorAsset(name: "ScoreSheet/Text/OnBackground/Secondary")
        }
        public enum OnCard {
          public static let `default` = ColorAsset(name: "ScoreSheet/Text/OnCard/Default")
          public static let plain = ColorAsset(name: "ScoreSheet/Text/OnCard/Plain")
          public static let pride = ColorAsset(name: "ScoreSheet/Text/OnCard/Pride")
        }
        public enum OnRail {
          public static let `default` = ColorAsset(name: "ScoreSheet/Text/OnRail/Default")
          public static let highlight = ColorAsset(name: "ScoreSheet/Text/OnRail/Highlight")
          public static let plain = ColorAsset(name: "ScoreSheet/Text/OnRail/Plain")
          public static let pride = ColorAsset(name: "ScoreSheet/Text/OnRail/Pride")
        }
      }
    }
    public enum Secondary {
      public static let `default` = ColorAsset(name: "Secondary/Default")
    }
    public enum Success {
      public static let `default` = ColorAsset(name: "Success/Default")
    }
    public enum Text {
      public static let onAction = ColorAsset(name: "Text/OnAction")
      public static let onBackground = ColorAsset(name: "Text/OnBackground")
      public static let onError = ColorAsset(name: "Text/OnError")
      public static let onPrimary = ColorAsset(name: "Text/OnPrimary")
      public static let onSecondaryBackground = ColorAsset(name: "Text/OnSecondaryBackground")
      public static let onSuccess = ColorAsset(name: "Text/OnSuccess")
    }
    public enum TrackableFilters {
      public static let bowler = ColorAsset(name: "TrackableFilters/Bowler")
      public static let game = ColorAsset(name: "TrackableFilters/Game")
      public static let league = ColorAsset(name: "TrackableFilters/League")
      public static let series = ColorAsset(name: "TrackableFilters/Series")
      public enum Text {
        public static let onBowler = ColorAsset(name: "TrackableFilters/Text/OnBowler")
        public static let onGame = ColorAsset(name: "TrackableFilters/Text/OnGame")
        public static let onLeague = ColorAsset(name: "TrackableFilters/Text/OnLeague")
        public static let onSeries = ColorAsset(name: "TrackableFilters/Text/OnSeries")
      }
    }
    public enum Warning {
      public static let `default` = ColorAsset(name: "Warning/Default")
    }
  }
  public enum Media {
    public enum Charts {
      public static let error = ImageAsset(name: "Charts/Error")
      public static let noData = ImageAsset(name: "Charts/NoData")
    }
    public enum EmptyState {
      public static let alleys = ImageAsset(name: "EmptyState/Alleys")
      public static let bowlers = ImageAsset(name: "EmptyState/Bowlers")
      public static let games = ImageAsset(name: "EmptyState/Games")
      public static let gear = ImageAsset(name: "EmptyState/Gear")
      public static let leagues = ImageAsset(name: "EmptyState/Leagues")
      public static let opponents = ImageAsset(name: "EmptyState/Opponents")
      public static let picker = ImageAsset(name: "EmptyState/Picker")
      public static let series = ImageAsset(name: "EmptyState/Series")
      public static let teams = ImageAsset(name: "EmptyState/Teams")
    }
    public enum Error {
      public static let notFound = ImageAsset(name: "Error/NotFound")
    }
    public enum Frame {
      public static let pin = ImageAsset(name: "Frame/Pin")
      public static let pinDown = ImageAsset(name: "Frame/PinDown")
    }
    public enum Icons {
      public static let alley = ImageAsset(name: "Icons/Alley")
      public static let analytics = ImageAsset(name: "Icons/Analytics")
      public enum Pins {
        public static let aces = ImageAsset(name: "Icons/Pins/Aces")
        public static let empty = ImageAsset(name: "Icons/Pins/Empty")
        public static let headPin = ImageAsset(name: "Icons/Pins/HeadPin")
        public static let `left` = ImageAsset(name: "Icons/Pins/Left")
        public static let leftChopOff = ImageAsset(name: "Icons/Pins/LeftChopOff")
        public static let leftFive = ImageAsset(name: "Icons/Pins/LeftFive")
        public static let leftH2 = ImageAsset(name: "Icons/Pins/LeftH2")
        public static let leftSplit = ImageAsset(name: "Icons/Pins/LeftSplit")
        public static let leftSplitWithBonus = ImageAsset(name: "Icons/Pins/LeftSplitWithBonus")
        public static let leftThree = ImageAsset(name: "Icons/Pins/LeftThree")
        public static let leftTwelve = ImageAsset(name: "Icons/Pins/LeftTwelve")
        public static let `right` = ImageAsset(name: "Icons/Pins/Right")
        public static let rightChopOff = ImageAsset(name: "Icons/Pins/RightChopOff")
        public static let rightFive = ImageAsset(name: "Icons/Pins/RightFive")
        public static let rightH2 = ImageAsset(name: "Icons/Pins/RightH2")
        public static let rightSplit = ImageAsset(name: "Icons/Pins/RightSplit")
        public static let rightSplitWithBonus = ImageAsset(name: "Icons/Pins/RightSplitWithBonus")
        public static let rightThree = ImageAsset(name: "Icons/Pins/RightThree")
        public static let rightTwelve = ImageAsset(name: "Icons/Pins/RightTwelve")
      }
      public static let rocket = ImageAsset(name: "Icons/Rocket")
      public enum Social {
        public static let instagram = ImageAsset(name: "Icons/Social/Instagram")
      }
    }
    public enum Lane {
      public static let galaxy = ImageAsset(name: "Lane/Galaxy")
      public static let wood = ImageAsset(name: "Lane/Wood")
    }
    public enum Onboarding {
      public static let background = ImageAsset(name: "Onboarding/Background")
    }
  }
}
// swiftlint:enable identifier_name line_length nesting type_body_length type_name

// MARK: - Implementation Details

public final class ColorAsset {
  public fileprivate(set) var name: String

  #if os(macOS)
  public typealias Color = NSColor
  #elseif os(iOS) || os(tvOS) || os(watchOS)
  public typealias Color = UIColor
  #endif

  @available(iOS 11.0, tvOS 11.0, watchOS 4.0, macOS 10.13, *)
  public private(set) lazy var color: Color = {
    guard let color = Color(asset: self) else {
      fatalError("Unable to load color asset named \(name).")
    }
    return color
  }()

  #if os(iOS) || os(tvOS)
  @available(iOS 11.0, tvOS 11.0, *)
  public func color(compatibleWith traitCollection: UITraitCollection) -> Color {
    let bundle = Bundle.module
    guard let color = Color(named: name, in: bundle, compatibleWith: traitCollection) else {
      fatalError("Unable to load color asset named \(name).")
    }
    return color
  }
  #endif

  #if canImport(SwiftUI)
  @available(iOS 13.0, tvOS 13.0, watchOS 6.0, macOS 10.15, *)
  public private(set) lazy var swiftUIColor: SwiftUI.Color = {
    SwiftUI.Color(asset: self)
  }()
  #endif

  fileprivate init(name: String) {
    self.name = name
  }
}

public extension ColorAsset.Color {
  @available(iOS 11.0, tvOS 11.0, watchOS 4.0, macOS 10.13, *)
  convenience init?(asset: ColorAsset) {
    let bundle = Bundle.module
    #if os(iOS) || os(tvOS)
    self.init(named: asset.name, in: bundle, compatibleWith: nil)
    #elseif os(macOS)
    self.init(named: NSColor.Name(asset.name), bundle: bundle)
    #elseif os(watchOS)
    self.init(named: asset.name)
    #endif
  }
}

#if canImport(SwiftUI)
@available(iOS 13.0, tvOS 13.0, watchOS 6.0, macOS 10.15, *)
public extension SwiftUI.Color {
  init(asset: ColorAsset) {
    let bundle = Bundle.module
    self.init(asset.name, bundle: bundle)
  }
}
#endif

public struct ImageAsset {
  public fileprivate(set) var name: String

  #if os(macOS)
  public typealias Image = NSImage
  #elseif os(iOS) || os(tvOS) || os(watchOS)
  public typealias Image = UIImage
  #endif

  @available(iOS 8.0, tvOS 9.0, watchOS 2.0, macOS 10.7, *)
  public var image: Image {
    let bundle = Bundle.module
    #if os(iOS) || os(tvOS)
    let image = Image(named: name, in: bundle, compatibleWith: nil)
    #elseif os(macOS)
    let name = NSImage.Name(self.name)
    let image = (bundle == .main) ? NSImage(named: name) : bundle.image(forResource: name)
    #elseif os(watchOS)
    let image = Image(named: name)
    #endif
    guard let result = image else {
      fatalError("Unable to load image asset named \(name).")
    }
    return result
  }

  #if os(iOS) || os(tvOS)
  @available(iOS 8.0, tvOS 9.0, *)
  public func image(compatibleWith traitCollection: UITraitCollection) -> Image {
    let bundle = Bundle.module
    guard let result = Image(named: name, in: bundle, compatibleWith: traitCollection) else {
      fatalError("Unable to load image asset named \(name).")
    }
    return result
  }
  #endif

  #if canImport(SwiftUI)
  @available(iOS 13.0, tvOS 13.0, watchOS 6.0, macOS 10.15, *)
  public var swiftUIImage: SwiftUI.Image {
    SwiftUI.Image(asset: self)
  }
  #endif
}

public extension ImageAsset.Image {
  @available(iOS 8.0, tvOS 9.0, watchOS 2.0, *)
  @available(macOS, deprecated,
    message: "This initializer is unsafe on macOS, please use the ImageAsset.image property")
  convenience init?(asset: ImageAsset) {
    #if os(iOS) || os(tvOS)
    let bundle = Bundle.module
    self.init(named: asset.name, in: bundle, compatibleWith: nil)
    #elseif os(macOS)
    self.init(named: NSImage.Name(asset.name))
    #elseif os(watchOS)
    self.init(named: asset.name)
    #endif
  }
}

#if canImport(SwiftUI)
@available(iOS 13.0, tvOS 13.0, watchOS 6.0, macOS 10.15, *)
public extension SwiftUI.Image {
  init(asset: ImageAsset) {
    let bundle = Bundle.module
    self.init(asset.name, bundle: bundle)
  }

  init(asset: ImageAsset, label: Text) {
    let bundle = Bundle.module
    self.init(asset.name, bundle: bundle, label: label)
  }

  init(decorative asset: ImageAsset) {
    let bundle = Bundle.module
    self.init(decorative: asset.name, bundle: bundle)
  }
}
#endif
