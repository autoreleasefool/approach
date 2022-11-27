import Foundation

public struct Alley: Sendable, Identifiable, Hashable, Codable {
	public let id: UUID
	public let name: String
	public let address: String?
	public let material: Material
	public let pinFall: PinFall
	public let mechanism: Mechanism

	public init(
		id: UUID,
		name: String,
		address: String?,
		material: Material,
		pinFall: PinFall,
		mechanism: Mechanism
	) {
		self.id = id
		self.name = name
		self.address = address
		self.material = material
		self.pinFall = pinFall
		self.mechanism = mechanism
	}
}

extension Alley {
	public enum Material: Int, Sendable, Identifiable, CaseIterable, Codable, CustomStringConvertible {
		case synthetic = 0
		case wood = 1
		case unknown = -1

		public var id: Int { rawValue }
		public var description: String {
			switch self {
			case .synthetic: return "Synthetic"
			case .wood: return "Wood"
			case .unknown: return "Unknown"
			}
		}
	}
}

extension Alley {
	public enum PinFall: Int, Sendable, Identifiable, CaseIterable, Codable, CustomStringConvertible {
		case freefall = 0
		case strings = 1
		case unknown = -1

		public var id: Int { rawValue }
		public var description: String {
			switch self {
			case .freefall: return "Freefall"
			case .strings: return "Strings"
			case .unknown: return "Unknown"
			}
		}
	}
}

extension Alley {
	public enum Mechanism: Int, Sendable, Identifiable, CaseIterable, Codable, CustomStringConvertible {
		case dedicated = 0
		case interchangeable = 1
		case unknown = -1

		public var id: Int { rawValue }
		public var description: String {
			switch self {
			case .dedicated: return "Dedicated"
			case .interchangeable: return "Interchangeable"
			case .unknown: return "Unknown"
			}
		}
	}
}
