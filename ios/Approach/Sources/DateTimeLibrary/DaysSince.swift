import Foundation

public enum DaysSince: Equatable, Comparable {
	case never
	case days(Int)

	public static func < (lhs: DaysSince, rhs: DaysSince) -> Bool {
		switch (lhs, rhs) {
		case (.never, .never): return false
		case (.never, .days): return true
		case (.days, .never): return false
		case let (.days(lhsDays), .days(rhsDays)): return lhsDays < rhsDays
		}
	}
}

extension Date {
	public func daysSince(_ date: Date) -> DaysSince {
		.days(date.timeIntervalSince(self).asDaysRoundingDown)
	}
}
