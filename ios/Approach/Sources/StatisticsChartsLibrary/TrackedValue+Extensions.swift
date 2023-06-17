import Charts
import StatisticsLibrary

extension TrackedValue: Comparable {
	public static func == (lhs: Self, rhs: Self) -> Bool {
		lhs.value == rhs.value
	}

	public static func < (lhs: Self, rhs: Self) -> Bool {
		lhs.value < rhs.value
	}
}

extension TrackedValue: Plottable {
	public typealias PrimitivePlottable = Int

	public init?(primitivePlottable: Int) {
		self.init(primitivePlottable)
	}

	public var primitivePlottable: Int { value }
}
