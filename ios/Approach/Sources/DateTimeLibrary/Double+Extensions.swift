import Foundation

extension Double {
	private static let SECONDS_PER_HOUR = 3600
	private static let SECONDS_PER_MINUTE = 60

	public var durationFormat: String {
		var remainingDuration = Int(self)
		let hours = remainingDuration / Self.SECONDS_PER_HOUR
		remainingDuration = remainingDuration % Self.SECONDS_PER_HOUR
		let minutes = remainingDuration / Self.SECONDS_PER_MINUTE

		let minutesString = minutes < 10 ? "0\(minutes)" : "\(minutes)"
		return "\(hours):\(minutesString)"
	}
}
