import Charts
import ModelsLibrary

public protocol CountingStatistic: Statistic {
	var count: Int { get set }
}

extension CountingStatistic {
	public static var supportsAggregation: Bool {
		true
	}

	public static var supportsWidgets: Bool {
		true
	}

	public mutating func aggregate(with: Statistic) {
		guard let with = with as? Self else { return }
		self.count += with.count
	}

	public var formattedValue: String {
		String(count)
	}

	public var formattedValueDescription: String? {
		nil
	}

	public var isEmpty: Bool {
		// `count` is defined on `CountingStatistic` and we are defining `isEmpty` here, so we can't _use_ `isEmpty`
		// swiftlint:disable:next empty_count
		count == 0
	}
}
