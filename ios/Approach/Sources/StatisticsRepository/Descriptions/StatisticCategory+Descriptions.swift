import AssetsLibrary
import StatisticsLibrary
import StatisticsRepositoryInterface
import StringsLibrary
import UIKit

extension StatisticCategory {
	func detailedDescription(frameConfiguration: TrackablePerFrameConfiguration) -> String? {
		switch self {
		case .aces: Strings.Statistics.Categories.Aces.description
		case .chopOffs: Strings.Statistics.Categories.ChopOffs.description
		case .fives: Strings.Statistics.Categories.Fives.description
		case .headPins:
			frameConfiguration.countHeadPin2AsHeadPin
			? Strings.Statistics.Categories.HeadPins.Description.withH2
			: Strings.Statistics.Categories.HeadPins.Description.withoutH2
		case .splits:
			frameConfiguration.countSplitWithBonusAsSplit
			? Strings.Statistics.Categories.Splits.Description.withBonus
			: Strings.Statistics.Categories.Splits.Description.withoutBonus
		case .threes: Strings.Statistics.Categories.Threes.description
		case .taps: Strings.Statistics.Categories.Taps.description
		case .twelves: Strings.Statistics.Categories.Twelves.description
		case .middleHits: Strings.Statistics.Categories.MiddleHits.description
		case .strikesAndSpares: Strings.Statistics.Categories.StrikesAndSpares.description
		case .firstRoll: Strings.Statistics.Categories.FirstRoll.description
		case .fouls: nil
		case .matchPlayResults: nil
		case .overall: nil
		case .series: nil
		case .pinsLeftOnDeck: nil
		}
	}

	func imageAssets(frameConfiguration: TrackablePerFrameConfiguration) -> [ImageAsset]? {
		switch self {
		case .aces: return [Asset.Media.Icons.Pins.aces]
		case .chopOffs: return [Asset.Media.Icons.Pins.leftChopOff, Asset.Media.Icons.Pins.rightChopOff]
		case .fives: return [Asset.Media.Icons.Pins.leftFive, Asset.Media.Icons.Pins.rightFive]
		case .headPins:
			return frameConfiguration.countHeadPin2AsHeadPin
			? [Asset.Media.Icons.Pins.headPin, Asset.Media.Icons.Pins.leftH2, Asset.Media.Icons.Pins.rightH2]
			: [Asset.Media.Icons.Pins.headPin]
		case .splits:
			return frameConfiguration.countSplitWithBonusAsSplit
			? [
				Asset.Media.Icons.Pins.leftSplit,
				Asset.Media.Icons.Pins.leftSplitWithBonus,
				Asset.Media.Icons.Pins.rightSplit,
				Asset.Media.Icons.Pins.rightSplitWithBonus,
			]
			: [Asset.Media.Icons.Pins.leftSplit, Asset.Media.Icons.Pins.rightSplit]
		case .threes: return [Asset.Media.Icons.Pins.leftThree, Asset.Media.Icons.Pins.rightThree]
		case .taps: return [Asset.Media.Icons.Pins.left, Asset.Media.Icons.Pins.right]
		case .twelves: return [Asset.Media.Icons.Pins.leftTwelve, Asset.Media.Icons.Pins.rightTwelve]
		case .middleHits: return [Asset.Media.Icons.Pins.headPin]
		case .strikesAndSpares: return nil
		case .firstRoll: return nil
		case .fouls: return nil
		case .matchPlayResults: return nil
		case .overall: return nil
		case .series: return nil
		case .pinsLeftOnDeck: return nil
		}
	}
}

extension Array where Element == ImageAsset {
	func asListEntryImages() -> [Statistics.ListEntryGroup.Image] {
		self.enumerated().map { .init(id: $0.offset, image: $0.element.image) }
	}
}
