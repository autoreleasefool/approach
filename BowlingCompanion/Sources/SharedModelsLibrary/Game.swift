import Foundation
import StringsLibrary

public struct Game: Sendable, Identifiable, Hashable, Codable {
	public static let NUMBER_OF_FRAMES = 10

	public let series: Series.ID
	public let id: UUID
	public let ordinal: Int
	public let locked: LockedState
	public let manualScore: Int?
	public let excludeFromStatistics: ExcludeFromStatistics

	public init(
		series: Series.ID,
		id: UUID,
		ordinal: Int,
		locked: LockedState,
		manualScore: Int?,
		excludeFromStatistics: ExcludeFromStatistics
	) {
		self.series = series
		self.id = id
		self.ordinal = ordinal
		self.locked = locked
		self.manualScore = manualScore
		self.excludeFromStatistics = excludeFromStatistics
	}
}

extension Game {
	public enum LockedState: Sendable, Codable {
		case locked
		case unlocked
	}
}

extension Game {
	public enum ExcludeFromStatistics: Int, Sendable, Identifiable, CaseIterable, Codable, CustomStringConvertible {
		case include = 0
		case exclude = 1

		public var id: Int { rawValue }
		public var description: String {
			switch self {
			case .include: return Strings.Game.Properties.ExcludeFromStatistics.include
			case .exclude: return Strings.Game.Properties.ExcludeFromStatistics.exclude
			}
		}

		public init(from: Series.ExcludeFromStatistics) {
			switch from {
			case .include:
				self = .include
			case .exclude:
				self = .exclude
			}
		}
	}
}
