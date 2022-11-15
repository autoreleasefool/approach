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
	public enum Kind: String, Sendable, Identifiable, CaseIterable, Codable {
		case shoes = "Shoes"
		case bowlingBall = "Ball"
		case towler = "Towel"
		case other = "Other"

		public var id: String { rawValue }
	}
}
