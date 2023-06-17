import Charts
import ModelsLibrary

public protocol CountingStatistic {
	var count: Int { get set }
}

extension CountingStatistic where Self: Statistic {
	public var formattedValue: String {
		String(count)
	}

	public var isEmpty: Bool {
		count == 0
	}
}

extension CountingStatistic where Self: GraphableStatistic {
	public var plottable: any Plottable {
		count
	}

	public mutating func accumulate(by: Self) {
		self.count += by.count
	}
}
