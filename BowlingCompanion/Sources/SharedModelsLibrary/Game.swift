import Foundation

public struct Game: Sendable, Identifiable, Hashable {
	public let id: UUID
	public let ordinal: Int
	public let locked: LockedState
	public let manualScore: Int?
	public let frames: [Frame]

	public init(id: UUID, ordinal: Int) {
		self.id = id
		self.ordinal = ordinal
		self.locked = .unlocked
		self.manualScore = nil
		self.frames = (1...10).map { .init(ordinal: $0) }
	}

	public init(
		id: UUID,
		ordinal: Int,
		locked: LockedState,
		manualScore: Int?,
		frames: [Frame]
	) {
		self.id = id
		self.ordinal = ordinal
		self.locked = locked
		self.manualScore = manualScore
		self.frames = frames
	}
}

extension Game {
	public enum LockedState: Sendable {
		case locked
		case unlocked
	}
}
