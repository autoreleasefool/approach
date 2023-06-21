import Charts
import StringsLibrary

public protocol PercentageStatistic: Statistic {
	static var numeratorTitle: String { get }
	static var denominatorTitle: String { get }

	var numerator: Int { get set }
	var denominator: Int { get set }
	var percentage: Double { get }
}

extension PercentageStatistic {
	public static var supportsAggregation: Bool {
		true
	}

	public var percentage: Double {
		return denominator > 0 ? Double(numerator) / Double(denominator) : 0
	}

	public mutating func aggregate(with: Statistic) {
		guard let with = with as? Self else { return }
		self.numerator += with.numerator
		self.denominator += with.denominator
	}

	public var formattedValue: String {
		format(percentage: percentage)
	}

	public var isEmpty: Bool {
		denominator == 0
	}
}
