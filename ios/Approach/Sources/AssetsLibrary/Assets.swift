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
        public static let lineMark = ColorAsset(name: "Charts/Counting/LineMark")
      }
      public static let foreground = ColorAsset(name: "Charts/Foreground")
      public enum Percentage {
        public static let axes = ColorAsset(name: "Charts/Percentage/Axes")
        public static let barMark = ColorAsset(name: "Charts/Percentage/BarMark")
        public static let denominatorLineMark = ColorAsset(name: "Charts/Percentage/DenominatorLineMark")
        public static let numeratorLineMark = ColorAsset(name: "Charts/Percentage/NumeratorLineMark")
      }
    }
    public enum Destructive {
      public static let `default` = ColorAsset(name: "Destructive/Default")
    }
    public enum Error {
      public static let `default` = ColorAsset(name: "Error/Default")
      public static let light = ColorAsset(name: "Error/Light")
    }
    public enum Filters {
      public static let auburn = ColorAsset(name: "Filters/Auburn")
      public static let celadon = ColorAsset(name: "Filters/Celadon")
      public static let englishViolet = ColorAsset(name: "Filters/EnglishViolet")
      public static let puce = ColorAsset(name: "Filters/Puce")
      public static let seaGreen = ColorAsset(name: "Filters/SeaGreen")
    }
    public enum Frame {
      public static let pinTint = ColorAsset(name: "Frame/PinTint")
    }
    public enum Primary {
      public static let `default` = ColorAsset(name: "Primary/Default")
      public static let light = ColorAsset(name: "Primary/Light")
    }
    public enum Secondary {
      public static let `default` = ColorAsset(name: "Secondary/Default")
    }
    public enum Success {
      public static let `default` = ColorAsset(name: "Success/Default")
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
      public static let analytics = ImageAsset(name: "Icons/Analytics")
    }
    public enum Lane {
      public static let backdrop = ImageAsset(name: "Lane/Backdrop")
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
