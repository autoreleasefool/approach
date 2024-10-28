import Foundation

extension Double {
	var instantToDate: Date {
		Date(timeIntervalSince1970: self / 1_000.0)
	}
}
