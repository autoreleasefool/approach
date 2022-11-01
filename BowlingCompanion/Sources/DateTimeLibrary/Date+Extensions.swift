import Foundation

extension Date {
	private static let longFormatter: DateFormatter = {
		let formatter = DateFormatter()
		formatter.dateStyle = .long
		formatter.timeStyle = .none
		return formatter
	}()

	public var longFormat: String {
		Self.longFormatter.string(from: self)
	}
}
