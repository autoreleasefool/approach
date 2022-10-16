import Foundation

public struct Game: Sendable, Identifiable, Hashable {
	public let id: UUID
	public let ordinal: Int
	public let locked: LockedState
	public let manualScore: Int?

	public init(
		id: UUID,
		ordinal: Int,
		locked: LockedState,
		manualScore: Int?
	) {
		self.id = id
		self.ordinal = ordinal
		self.locked = locked
		self.manualScore = manualScore
	}
}

extension Game {
	public enum LockedState: Sendable {
		case locked
		case unlocked
	}
}
