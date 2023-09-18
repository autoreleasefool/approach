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

	private static let mediumFormatter: DateFormatter = {
		let formatter = DateFormatter()
		formatter.dateFormat = "eee, MMM d"
		return formatter
	}()

	public var mediumFormat: String {
		Self.mediumFormatter.string(from: self)
	}

	private static let shortFormatter: DateFormatter = {
		let formatter = DateFormatter()
		formatter.dateFormat = "MMM d"
		return formatter
	}()

	public var shortFormat: String {
		Self.shortFormatter.string(from: self)
	}

	private static let machineDateFormatter: DateFormatter = {
		let formatter = DateFormatter()
		formatter.dateFormat = "yyyy-MM-dd"
		return formatter
	}()

	public var machineDateFormat: String {
		Self.machineDateFormatter.string(from: self)
	}
}
