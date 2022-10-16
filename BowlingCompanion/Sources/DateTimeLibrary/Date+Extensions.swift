import Foundation

extension Date {
	private static let regularDateFormatter: DateFormatter = {
		let formatter = DateFormatter()
		formatter.dateStyle = .long
		formatter.timeStyle = .none
		return formatter
	}()

	public var regularDateFormat: String {
		Self.regularDateFormatter.string(from: self)
	}
}
