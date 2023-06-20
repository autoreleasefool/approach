import Charts
import ModelsLibrary

public protocol HighestOfStatistic: Statistic {
	var highest: Int { get set }
}

extension HighestOfStatistic {
	public static var supportsAggregation: Bool {
		true
	}

	public mutating func aggregate(with: Statistic) {
		guard let with = with as? Self else { return }
		self.highest = max(self.highest, with.highest)
	}

	public var formattedValue: String {
		String(highest)
	}

	public var isEmpty: Bool {
		highest == 0
	}
}
