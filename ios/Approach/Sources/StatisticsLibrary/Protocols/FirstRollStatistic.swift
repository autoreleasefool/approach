import Charts
import ModelsLibrary
import StringsLibrary

public protocol FirstRollStatistic: PercentageStatistic, TrackablePerFirstRoll {
	var totalRolls: Int { get set }

	mutating func tracks(firstRoll: Frame.OrderedRoll, configuration: TrackablePerFrameConfiguration) -> Bool
}

extension FirstRollStatistic {
	public static var numeratorTitle: String { title }
	public static var denominatorTitle: String { Strings.Statistics.Title.totalRolls }
	public static var includeNumeratorInFormattedValue: Bool { true }

	public var denominator: Int {
		get { totalRolls }
		set { totalRolls = newValue }
	}

	public mutating func adjust(byFirstRoll: Frame.OrderedRoll, configuration: TrackablePerFrameConfiguration) {
		totalRolls += 1
		if tracks(firstRoll: byFirstRoll, configuration: configuration) {
			numerator += 1
		}
	}
}
