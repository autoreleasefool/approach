import Charts
import StringsLibrary

public protocol AveragingStatistic: Statistic {
	var total: Int { get set }
	var divisor: Int { get set }
	var average: Double { get }
}

extension AveragingStatistic {
	public static var supportsAggregation: Bool {
		false
	}

	public var average: Double {
		return divisor > 0 ? Double(total) / Double(divisor) : 0
	}

	public mutating func aggregate(with: Statistic) {
		guard let with = with as? Self else { return }
		self.total += with.total
		self.divisor += with.divisor
	}

	public var formattedValue: String {
		format(average: average)
	}

	public var isEmpty: Bool {
		divisor == 0
	}
}
