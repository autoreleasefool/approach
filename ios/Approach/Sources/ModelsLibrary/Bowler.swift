import Foundation

public enum Bowler {}

extension Bowler {
	public typealias ID = UUID
}

extension Bowler {
	public enum Status: String, Codable, Sendable {
		case playable
		case opponent
	}
}

extension Bowler {
	public struct Summary: Identifiable, Codable, Equatable {
		public let id: Bowler.ID
		public let name: String

		public init(id: Bowler.ID, name: String) {
			self.id = id
			self.name = name
		}
	}
}

extension Bowler {
	public struct List: Identifiable, Codable, Equatable {
		private static let averageFormatter: NumberFormatter = {
			let formatter = NumberFormatter()
			formatter.maximumFractionDigits = 1
			formatter.alwaysShowsDecimalSeparator = false
			return formatter
		}()

		public let id: Bowler.ID
		public let name: String
		public let average: Double?

		public var summary: Summary {
			.init(id: id, name: name)
		}

		public var averageDescription: String {
			guard let average, average > 0 else { return "" }
			return Self.averageFormatter.string(from: NSNumber(value: average)) ?? ""
		}

		public init(id: Bowler.ID, name: String, average: Double?) {
			self.id = id
			self.name = name
			self.average = average
		}
	}
}
