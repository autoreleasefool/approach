import Foundation

extension Double {
	private static let SECONDS_PER_DAY = 86_400
	private static let SECONDS_PER_HOUR = 3_600
	private static let SECONDS_PER_MINUTE = 60

	public var durationFormat: String {
		var remainingDuration = Int(self)
		let hours = remainingDuration / Self.SECONDS_PER_HOUR
		remainingDuration = remainingDuration % Self.SECONDS_PER_HOUR
		let minutes = remainingDuration / Self.SECONDS_PER_MINUTE

		let minutesString = minutes < 10 ? "0\(minutes)" : "\(minutes)"
		return "\(hours):\(minutesString)"
	}

	public var asDaysRoundingDown: Int {
		Int(self) / Self.SECONDS_PER_DAY
	}
}
