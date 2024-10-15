import Foundation

public enum DaysSince: Equatable {
	case never
	case days(Int)
}

extension Date {
	public func daysSince(_ date: Date) -> DaysSince {
		.days(date.timeIntervalSince(self).asDaysRoundingDown)
	}
}
