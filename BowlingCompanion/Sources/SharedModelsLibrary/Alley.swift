import Foundation
import StringsLibrary

public struct Alley: Sendable, Identifiable, Hashable, Codable {
	public let id: UUID
	public let name: String
	public let address: String?
	public let material: Material
	public let pinFall: PinFall
	public let mechanism: Mechanism
	public let pinBase: PinBase

	public init(
		id: UUID,
		name: String,
		address: String?,
		material: Material,
		pinFall: PinFall,
		mechanism: Mechanism,
		pinBase: PinBase
	) {
		self.id = id
		self.name = name
		self.address = address
		self.material = material
		self.pinFall = pinFall
		self.mechanism = mechanism
		self.pinBase = pinBase
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
			case .synthetic: return Strings.Alley.Properties.Material.synthetic
			case .wood: return Strings.Alley.Properties.Material.wood
			case .unknown: return Strings.unknown
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
			case .freefall: return Strings.Alley.Properties.PinFall.freefall
			case .strings: return Strings.Alley.Properties.PinFall.strings
			case .unknown: return Strings.unknown
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
			case .dedicated: return Strings.Alley.Properties.Mechanism.dedicated
			case .interchangeable: return Strings.Alley.Properties.Mechanism.interchangeable
			case .unknown: return Strings.unknown
			}
		}
	}
}

extension Alley {
	public enum PinBase: Int, Sendable, Identifiable, CaseIterable, Codable, CustomStringConvertible {
		case black = 0
		case white = 1
		case other = 2
		case unknown = -1

		public var id: Int { rawValue }
		public var description: String {
			switch self {
			case .black: return Strings.Alley.Properties.PinBase.black
			case .white: return Strings.Alley.Properties.PinBase.white
			case .other: return Strings.other
			case .unknown: return Strings.unknown
			}
		}
	}
}
