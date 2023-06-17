import Charts
import StringsLibrary

public protocol AveragingStatistic {
	var total: Int { get set }
	var divisor: Int { get set }
	var average: Double { get }
}

extension AveragingStatistic {
	public var average: Double {
		return divisor > 0 ? Double(total) / Double(divisor) : 0
	}
}

extension AveragingStatistic where Self: Statistic {
	public var formattedValue: String {
		format(average: average)
	}

	public var isEmpty: Bool {
		divisor == 0
	}
}

extension AveragingStatistic where Self: GraphableStatistic {
	public var plottable: any Plottable {
		average
	}

	public mutating func accumulate(by: Self) {
		self.total += by.total
		self.divisor += by.divisor
	}
}
