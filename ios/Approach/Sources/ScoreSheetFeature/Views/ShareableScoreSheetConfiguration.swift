import AssetsLibrary
import Foundation
import StringsLibrary

public struct ShareableScoreSheetConfiguration: Equatable {
	public var style: Style
	public var labelPosition: LabelPosition = .bottom
	public var showFrameLabels: Bool = true
	public var showFrameDetails: Bool = true
	public var bowlerName: String?
	public var leagueName: String?
	public var seriesDate: Date?
	public var alleyName: String?

	public init(style: Style = .default) {
		self.style = style

		self.bowlerName = "Joseph"
		self.leagueName = "Majors, 2022-2023"
		self.seriesDate = Date()
		self.alleyName = "Skyview Lanes"
	}

	var hasLabels: Bool {
		bowlerName != nil || leagueName != nil || seriesDate != nil || alleyName != nil
	}

	func hasLabels(onSide: LabelPosition) -> Bool {
		hasLabels && labelPosition == onSide
	}

	var titleLabel: (header: String, subHeader: String?)? {
		switch (bowlerName, leagueName) {
		case let (.some(bowlerName), .some(leagueName)):
			return (bowlerName, leagueName)
		case let (.some(bowlerName), .none):
			return (bowlerName, nil)
		case let (.none, .some(leagueName)):
			return (leagueName, nil)
		case (.none, .none):
			return nil
		}
	}
}

extension ShareableScoreSheetConfiguration {
	public enum LabelPosition: Equatable {
		case top
		case right
		case bottom
		case left
	}
}

extension ShareableScoreSheetConfiguration {
	public struct Style: Equatable {
		public let title: String
		let textOnBackground: ColorAsset
		let textOnRail: ColorAsset
		let textOnCard: ColorAsset
		let background: ColorAsset
		let railBackground: ColorAsset
		let cardBackground: ColorAsset
		let border: ColorAsset
		let strongBorder: ColorAsset

		public static let `default` = Self(
			title: Strings.Sharing.ScoreSheet.Style.default,
			textOnBackground: Asset.Colors.ScoreSheet.Text.OnBackground.default,
			textOnRail: Asset.Colors.ScoreSheet.Text.OnRail.default,
			textOnCard: Asset.Colors.ScoreSheet.Text.OnCard.default,
			background: Asset.Colors.ScoreSheet.Background.default,
			railBackground: Asset.Colors.ScoreSheet.Rail.default,
			cardBackground: Asset.Colors.ScoreSheet.Card.default,
			border: Asset.Colors.ScoreSheet.Border.default,
			strongBorder: Asset.Colors.ScoreSheet.Border.defaultStrong
		)

		public static let plain = Self(
			title: Strings.Sharing.ScoreSheet.Style.plain,
			textOnBackground: Asset.Colors.ScoreSheet.Text.OnBackground.plain,
			textOnRail: Asset.Colors.ScoreSheet.Text.OnRail.plain,
			textOnCard: Asset.Colors.ScoreSheet.Text.OnCard.plain,
			background: Asset.Colors.ScoreSheet.Background.plain,
			railBackground: Asset.Colors.ScoreSheet.Rail.plain,
			cardBackground: Asset.Colors.ScoreSheet.Card.plain,
			border: Asset.Colors.ScoreSheet.Border.plain,
			strongBorder: Asset.Colors.ScoreSheet.Border.plainStrong
		)

		public static let pride = Self(
			title: Strings.Sharing.ScoreSheet.Style.pride,
			textOnBackground: Asset.Colors.ScoreSheet.Text.OnBackground.pride,
			textOnRail: Asset.Colors.ScoreSheet.Text.OnRail.pride,
			textOnCard: Asset.Colors.ScoreSheet.Text.OnCard.pride,
			background: Asset.Colors.ScoreSheet.Background.pride,
			railBackground: Asset.Colors.ScoreSheet.Rail.pride,
			cardBackground: Asset.Colors.ScoreSheet.Card.pride,
			border: Asset.Colors.ScoreSheet.Border.pride,
			strongBorder: Asset.Colors.ScoreSheet.Border.prideStrong
		)

		public static let allStyles: [Self] = [
			.default,
			.plain,
			.pride,
		]

		public static func == (lhs: Self, rhs: Self) -> Bool {
			lhs.title == rhs.title
		}
	}
}
