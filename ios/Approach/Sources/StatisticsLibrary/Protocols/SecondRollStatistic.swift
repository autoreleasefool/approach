import Charts
import ModelsLibrary
import StringsLibrary

public protocol SecondRollStatistic: PercentageStatistic, TrackablePerSecondRoll {}

extension SecondRollStatistic {
	public static var numeratorTitle: String { title }
	public static var includeNumeratorInFormattedValue: Bool { true }

	public var isEmpty: Bool {
		numerator == 0
	}
}
