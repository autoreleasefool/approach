import AssetsLibrary
import SwiftUI

extension EnvironmentValues {
	@Entry public var scoreSheetConfiguration: ScoreSheet.Configuration = .plain
}

extension ScoreSheet {
	public struct Configuration: Equatable {
		public let foreground: ColorAsset
		public let foregroundHighlight: ColorAsset
		public let foregroundSecondary: ColorAsset
		public let foregroundFoul: ColorAsset
		public let foregroundFoulHighlight: ColorAsset
		public let background: ColorAsset
		public let backgroundHighlight: ColorAsset
		public let railForeground: ColorAsset
		public let railForegroundHighlight: ColorAsset
		public let railBackground: ColorAsset
		public let railBackgroundHighlight: ColorAsset
		public let border: ColorAsset
		public let allowLeadingRounding: Bool
		public let allowTopRounding: Bool
		public let allowTrailingRounding: Bool
		public let allowBottomRounding: Bool
		public let railOnTop: Bool

		public init(
			foreground: ColorAsset,
			foregroundHighlight: ColorAsset,
			foregroundSecondary: ColorAsset,
			foregroundFoul: ColorAsset,
			foregroundFoulHighlight: ColorAsset,
			background: ColorAsset,
			backgroundHighlight: ColorAsset,
			railForeground: ColorAsset,
			railForegroundHighlight: ColorAsset,
			railBackground: ColorAsset,
			railBackgroundHighlight: ColorAsset,
			border: ColorAsset,
			allowLeadingRounding: Bool,
			allowTopRounding: Bool,
			allowTrailingRounding: Bool,
			allowBottomRounding: Bool,
			railOnTop: Bool
		) {
			self.foreground = foreground
			self.foregroundHighlight = foregroundHighlight
			self.foregroundSecondary = foregroundSecondary
			self.foregroundFoul = foregroundFoul
			self.foregroundFoulHighlight = foregroundFoulHighlight
			self.background = background
			self.backgroundHighlight = backgroundHighlight
			self.railForeground = railForeground
			self.railForegroundHighlight = railForegroundHighlight
			self.railBackground = railBackground
			self.railBackgroundHighlight = railBackgroundHighlight
			self.border = border
			self.allowLeadingRounding = allowLeadingRounding
			self.allowTopRounding = allowTopRounding
			self.allowTrailingRounding = allowTrailingRounding
			self.allowBottomRounding = allowBottomRounding
			self.railOnTop = railOnTop
		}

		public static var plain: Configuration {
			Configuration(
				foreground: Asset.Colors.ScoreSheet.Plain.Text.OnBackground.default,
				foregroundHighlight: Asset.Colors.ScoreSheet.Plain.Text.OnBackground.highlight,
				foregroundSecondary: Asset.Colors.ScoreSheet.Plain.Text.OnBackground.secondary,
				foregroundFoul: Asset.Colors.ScoreSheet.Plain.Text.OnBackground.foul,
				foregroundFoulHighlight: Asset.Colors.ScoreSheet.Plain.Text.OnBackground.highlightFoul,
				background: Asset.Colors.ScoreSheet.Plain.Background.default,
				backgroundHighlight: Asset.Colors.ScoreSheet.Plain.Background.highlight,
				railForeground: Asset.Colors.ScoreSheet.Plain.Text.OnRail.default,
				railForegroundHighlight: Asset.Colors.ScoreSheet.Plain.Text.OnRail.highlight,
				railBackground: Asset.Colors.ScoreSheet.Plain.Rail.default,
				railBackgroundHighlight: Asset.Colors.ScoreSheet.Plain.Rail.highlight,
				border: Asset.Colors.ScoreSheet.Plain.Border.default,
				allowLeadingRounding: true,
				allowTopRounding: true,
				allowTrailingRounding: true,
				allowBottomRounding: true,
				railOnTop: false
			)
		}
	}
}

// MARK: - Colours

extension ScoreSheet.Configuration {
	public func foreground(highlight: Bool) -> ColorAsset {
		highlight ? foregroundHighlight : foreground
	}

	public func background(highlight: Bool) -> ColorAsset {
		highlight ? backgroundHighlight : background
	}

	public func foreground(
		highlight: Bool,
		didFoul: Bool,
		isSecondary: Bool
	) -> ColorAsset {
		if isSecondary {
			foregroundSecondary
		} else if highlight {
			if didFoul {
				foregroundFoulHighlight
			} else {
				foregroundHighlight
			}
		} else {
			if didFoul {
				foregroundFoul
			} else {
				foreground
			}
		}
	}

	public func railForeground(highlight: Bool) -> ColorAsset {
		highlight ? railForegroundHighlight : railForeground
	}

	public func railBackground(highlight: Bool) -> ColorAsset {
		highlight ? railBackgroundHighlight : railBackground
	}
}

// MARK: - Rounding

extension ScoreSheet.Configuration {
	public enum CornerComponent {
		case roll
		case frame
		case rail
		case score
	}

	public enum Position {
		case topLeading
		case topTrailing
		case bottomLeading
		case bottomTrailing
	}

	public func shouldRound(
		_ component: CornerComponent,
		inPosition position: Position,
		frameIndex: Int? = nil,
		rollIndex: Int? = nil
	) -> Bool {
		switch component {
		case .roll:
			return frameIndex == 0 && rollIndex == 0 &&
				(position == .topLeading && !railOnTop && allowTopRounding && allowLeadingRounding)
		case .frame:
			return frameIndex == 0 &&
				(position == .bottomLeading && allowLeadingRounding && railOnTop && allowBottomRounding)
		case .rail:
			return frameIndex == 0 &&
				(
					(position == .topLeading && allowLeadingRounding && railOnTop && allowTopRounding) ||
					(position == .bottomLeading && allowLeadingRounding && !railOnTop && allowBottomRounding)
				)
		case .score:
			return (
				(position == .topTrailing && allowTrailingRounding && allowTopRounding) ||
				(position == .bottomTrailing && allowTrailingRounding && allowBottomRounding)
			)
		}
	}
}

extension ScoreSheet.Configuration {
	public static func shareablePlain(
		allowTopRounding: Bool,
		allowBottomRounding: Bool,
		allowLeadingRounding: Bool,
		allowTrailingRounding: Bool,
		railOnTop: Bool = true
	) -> ScoreSheet.Configuration {
		ScoreSheet.Configuration(
			foreground: Asset.Colors.ScoreSheet.Plain.Text.OnBackground.default,
			foregroundHighlight: Asset.Colors.ScoreSheet.Plain.Text.OnBackground.highlight,
			foregroundSecondary: Asset.Colors.ScoreSheet.Plain.Text.OnBackground.secondary,
			foregroundFoul: Asset.Colors.ScoreSheet.Plain.Text.OnBackground.foul,
			foregroundFoulHighlight: Asset.Colors.ScoreSheet.Plain.Text.OnBackground.highlightFoul,
			background: Asset.Colors.ScoreSheet.Plain.Background.default,
			backgroundHighlight: Asset.Colors.ScoreSheet.Plain.Background.highlight,
			railForeground: Asset.Colors.ScoreSheet.Plain.Text.OnRail.default,
			railForegroundHighlight: Asset.Colors.ScoreSheet.Plain.Text.OnRail.highlight,
			railBackground: Asset.Colors.ScoreSheet.Plain.Rail.default,
			railBackgroundHighlight: Asset.Colors.ScoreSheet.Plain.Rail.highlight,
			border: Asset.Colors.ScoreSheet.Plain.Border.default,
			allowLeadingRounding: allowLeadingRounding,
			allowTopRounding: allowTopRounding,
			allowTrailingRounding: allowTrailingRounding,
			allowBottomRounding: allowBottomRounding,
			railOnTop: railOnTop
		)
	}

	public static func shareableGrayscale(
		allowTopRounding: Bool,
		allowBottomRounding: Bool,
		allowLeadingRounding: Bool,
		allowTrailingRounding: Bool,
		railOnTop: Bool = true
	) -> ScoreSheet.Configuration {
		ScoreSheet.Configuration(
			foreground: Asset.Colors.ScoreSheet.Grayscale.Text.OnBackground.default,
			foregroundHighlight: Asset.Colors.ScoreSheet.Grayscale.Text.OnBackground.highlight,
			foregroundSecondary: Asset.Colors.ScoreSheet.Grayscale.Text.OnBackground.secondary,
			foregroundFoul: Asset.Colors.ScoreSheet.Grayscale.Text.OnBackground.foul,
			foregroundFoulHighlight: Asset.Colors.ScoreSheet.Grayscale.Text.OnBackground.highlightFoul,
			background: Asset.Colors.ScoreSheet.Grayscale.Background.default,
			backgroundHighlight: Asset.Colors.ScoreSheet.Grayscale.Background.highlight,
			railForeground: Asset.Colors.ScoreSheet.Grayscale.Text.OnRail.default,
			railForegroundHighlight: Asset.Colors.ScoreSheet.Grayscale.Text.OnRail.highlight,
			railBackground: Asset.Colors.ScoreSheet.Grayscale.Rail.default,
			railBackgroundHighlight: Asset.Colors.ScoreSheet.Grayscale.Rail.highlight,
			border: Asset.Colors.ScoreSheet.Grayscale.Border.default,
			allowLeadingRounding: allowLeadingRounding,
			allowTopRounding: allowTopRounding,
			allowTrailingRounding: allowTrailingRounding,
			allowBottomRounding: allowBottomRounding,
			railOnTop: railOnTop
		)
	}
}
