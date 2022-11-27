import Foundation

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
			case .shoes: return "Shoes"
			case .bowlingBall: return "Ball"
			case .towel: return "Towel"
			case .other: return "Other"
			}
		}
	}
}
