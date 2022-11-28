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
			case .synthetic: return Strings.Alleys.Material.synthetic
			case .wood: return Strings.Alleys.Material.wood
			case .unknown: return Strings.Alleys.Material.unknown
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
			case .freefall: return Strings.Alleys.PinFall.freefall
			case .strings: return Strings.Alleys.PinFall.strings
			case .unknown: return Strings.Alleys.PinFall.unknown
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
			case .dedicated: return Strings.Alleys.Mechanism.dedicated
			case .interchangeable: return Strings.Alleys.Mechanism.interchangeable
			case .unknown: return Strings.Alleys.Mechanism.unknown
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
			case .black: return Strings.Alleys.PinBase.black
			case .white: return Strings.Alleys.PinBase.white
			case .other: return Strings.Alleys.PinBase.other
			case .unknown: return Strings.Alleys.PinBase.unknown
			}
		}
	}
}
