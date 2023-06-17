import Charts
import ModelsLibrary

public protocol HighestOfStatistic {
	var highest: Int { get set }
}

extension HighestOfStatistic where Self: Statistic {
	public var formattedValue: String {
		String(highest)
	}

	public var isEmpty: Bool {
		highest == 0
	}
}

extension HighestOfStatistic where Self: GraphableStatistic {
	public var plottable: any Plottable {
		highest
	}

	public mutating func accumulate(by: Self) {
		self.highest = max(self.highest, by.highest)
	}
}
