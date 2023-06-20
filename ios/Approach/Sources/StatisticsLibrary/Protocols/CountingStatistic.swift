import Charts
import ModelsLibrary

public protocol CountingStatistic: Statistic {
	var count: Int { get set }
}

extension CountingStatistic {
	public mutating func aggregate(with: Statistic) {
		guard let with = with as? Self else { return }
		self.count += with.count
	}

	public var formattedValue: String {
		String(count)
	}

	public var isEmpty: Bool {
		count == 0
	}
}
