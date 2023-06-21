import Charts
import ModelsLibrary
import StringsLibrary

public protocol SecondRollStatistic: PercentageStatistic, TrackablePerSecondRoll {
	var totalSecondRolls: Int { get set }

	mutating func tracks(secondRoll: Frame.OrderedRoll, configuration: TrackablePerFrameConfiguration) -> Bool
}

extension SecondRollStatistic {
	public static var numeratorTitle: String { title }
	public static var denominatorTitle: String { Strings.Statistics.Title.totalRolls }

	public var denominator: Int {
		get { totalSecondRolls }
		set { totalSecondRolls = newValue }
	}

	public mutating func adjust(bySecondRoll: Frame.OrderedRoll, configuration: TrackablePerFrameConfiguration) {
		totalSecondRolls += 1
		if tracks(secondRoll: bySecondRoll, configuration: configuration) {
			numerator += 1
		}
	}
}
