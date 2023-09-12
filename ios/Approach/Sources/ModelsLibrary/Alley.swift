import Foundation

public enum Alley {}

extension Alley {
	public typealias ID = UUID

	public static let NUMBER_OF_LANES_RANGE = 0...1000
}

extension Alley {
	public enum Material: String, Codable, Sendable, Identifiable, CaseIterable {
		case synthetic
		case wood

		public var id: String { rawValue }
	}
}

extension Alley {
	public enum PinFall: String, Codable, Sendable, Identifiable, CaseIterable {
		case freefall
		case strings

		public var id: String { rawValue }
	}
}

extension Alley {
	public enum Mechanism: String, Codable, Sendable, Identifiable, CaseIterable {
		case dedicated
		case interchangeable

		public var id: String { rawValue }
	}
}

extension Alley {
	public enum PinBase: String, Codable, Sendable, Identifiable, CaseIterable {
		case black
		case white
		case other

		public var id: String { rawValue }
	}
}

extension Alley {
	public struct Summary: Identifiable, Codable, Equatable {
		public let id: Alley.ID
		public let name: String
		public let material: Material?
		public let pinFall: PinFall?
		public let mechanism: Mechanism?
		public let pinBase: PinBase?
		public let location: Location.Summary?

		public var named: Named {
			.init(id: id, name: name)
		}

		public init(
			id: Alley.ID,
			name: String,
			material: Material?,
			pinFall: PinFall?,
			mechanism: Mechanism?,
			pinBase: PinBase?,
			location: Location.Summary?
		) {
			self.id = id
			self.name = name
			self.material = material
			self.pinFall = pinFall
			self.mechanism = mechanism
			self.pinBase = pinBase
			self.location = location
		}
	}
}

extension Alley {
	public struct List: Identifiable, Codable, Equatable {
		public let id: Alley.ID
		public let name: String
		public let material: Material?
		public let pinFall: PinFall?
		public let mechanism: Mechanism?
		public let pinBase: PinBase?
		public let location: Location.Summary?
		public let average: Double?

		public var named: Named {
			.init(id: id, name: name)
		}

		public init(
			id: Alley.ID,
			name: String,
			material: Material?,
			pinFall: PinFall?,
			mechanism: Mechanism?,
			pinBase: PinBase?,
			location: Location.Summary?,
			average: Double?
		) {
			self.id = id
			self.name = name
			self.material = material
			self.pinFall = pinFall
			self.mechanism = mechanism
			self.pinBase = pinBase
			self.location = location
			self.average = average
		}
	}
}

extension Alley {
	public struct Named: Identifiable, Codable, Equatable {
		public let id: Alley.ID
		public let name: String

		public init(id: Alley.ID, name: String) {
			self.id = id
			self.name = name
		}
	}
}
