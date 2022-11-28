import Foundation

public struct Game: Sendable, Identifiable, Hashable, Codable {
	public static let NUMBER_OF_FRAMES = 10

	public let series: Series.ID
	public let id: UUID
	public let ordinal: Int
	public let locked: LockedState
	public let manualScore: Int?

	public init(
		series: Series.ID,
		id: UUID,
		ordinal: Int,
		locked: LockedState,
		manualScore: Int?
	) {
		self.series = series
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
