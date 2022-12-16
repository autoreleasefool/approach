import Foundation
import StringsLibrary

public struct Gear: Sendable, Identifiable, Hashable, Codable {
	public let bowler: Bowler.ID?
	public let id: UUID
	public let name: String
	public let kind: Kind

	public init(
		bowler: Bowler.ID?,
		id: UUID,
		name: String,
		kind: Kind
	) {
		self.bowler = bowler
		self.id = id
		self.name = name
		self.kind = kind
	}
}

extension Gear {
	public enum Kind: Int, Sendable, Identifiable, CaseIterable, Codable, CustomStringConvertible {
		case shoes = 0
		case bowlingBall = 1
		case towel = 2
		case other = 3

		public var id: Int { rawValue }
		public var description: String {
			switch self {
			case .shoes: return Strings.Gear.Properties.Kind.shoes
			case .bowlingBall: return Strings.Gear.Properties.Kind.bowlingBall
			case .towel: return Strings.Gear.Properties.Kind.towel
			case .other: return Strings.other
			}
		}

		public var image: String {
			switch self {
			case .bowlingBall: return "poweroutlet.type.h"
			case .shoes: return "shoeprints.fill"
			case .towel: return "square.split.bottomrightquarter"
			case .other: return "questionmark.app"
			}
		}
	}
}
