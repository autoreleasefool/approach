import Foundation

public struct Game: Sendable, Identifiable, Hashable, Codable {
	public let seriesId: Series.ID
	public let id: UUID
	public let ordinal: Int
	public let locked: LockedState
	public let manualScore: Int?

	public init(
		seriesId: Series.ID,
		id: UUID,
		ordinal: Int,
		locked: LockedState,
		manualScore: Int?
	) {
		self.seriesId = seriesId
		self.id = id
		self.ordinal = ordinal
		self.locked = locked
		self.manualScore = manualScore
	}
}

extension Game {
	public enum LockedState: Sendable, Codable {
		case locked
		case unlocked
	}
}
