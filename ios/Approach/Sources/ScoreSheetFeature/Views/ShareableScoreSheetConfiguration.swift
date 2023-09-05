import AssetsLibrary
import Foundation
import StringsLibrary

public struct ShareableScoreSheetConfiguration: Equatable {
	public var style: Style
	public var labelPosition: LabelPosition
	public var showFrameLabels: Bool
	public var showFrameDetails: Bool
	public var bowlerName: String?
	public var leagueName: String?
	public var seriesDate: Date?
	public var alleyName: String?

	public init(
		style: ShareableScoreSheetConfiguration.Style = .default,
		labelPosition: ShareableScoreSheetConfiguration.LabelPosition = .bottom,
		showFrameLabels: Bool = true,
		showFrameDetails: Bool = true,
		bowlerName: String? = nil,
		leagueName: String? = nil,
		seriesDate: Date? = nil,
		alleyName: String? = nil
	) {
		self.style = style
		self.labelPosition = labelPosition
		self.showFrameLabels = showFrameLabels
		self.showFrameDetails = showFrameDetails
		self.bowlerName = bowlerName
		self.leagueName = leagueName
		self.seriesDate = seriesDate
		self.alleyName = alleyName
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
	public enum LabelPosition: String, Equatable, CaseIterable, Identifiable, CustomStringConvertible {
		case top
		case right
		case bottom
		case left

		public var id: String { rawValue }
		public var description: String {
			switch self {
			case .top: return Strings.Sharing.Layout.LabelPosition.top
			case .bottom: return Strings.Sharing.Layout.LabelPosition.bottom
			case .left: return Strings.Sharing.Layout.LabelPosition.left
			case .right: return Strings.Sharing.Layout.LabelPosition.right
			}
		}
	}
}

extension ShareableScoreSheetConfiguration {
	public struct Style: Equatable {
		public let title: String
		public let textOnBackground: ColorAsset
		public let textOnRail: ColorAsset
		public let textOnCard: ColorAsset
		public let background: ColorAsset
		public let railBackground: ColorAsset
		public let cardBackground: ColorAsset
		public let border: ColorAsset
		public let strongBorder: ColorAsset

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
