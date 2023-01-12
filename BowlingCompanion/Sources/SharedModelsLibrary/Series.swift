import Foundation
import StringsLibrary

public struct Series: Sendable, Identifiable, Hashable, Codable {
	public let league: League.ID
	public let id: UUID
	public let date: Date
	public let numberOfGames: Int
	public let preBowl: PreBowl
	public let excludeFromStatistics: ExcludeFromStatistics
	public let alley: Alley.ID?

	public init(
		league: League.ID,
		id: UUID,
		date: Date,
		numberOfGames: Int,
		preBowl: PreBowl,
		excludeFromStatistics: ExcludeFromStatistics,
		alley: Alley.ID?
	) {
		self.league = league
		self.id = id
		self.date = date
		self.numberOfGames = numberOfGames
		self.preBowl = preBowl
		self.excludeFromStatistics = excludeFromStatistics
		self.alley = alley
	}
}

extension Series {
	public enum PreBowl: Int, Sendable, Identifiable, CaseIterable, Codable, CustomStringConvertible {
		case regularPlay = 0
		case preBowl = 1

		public var id: Int { rawValue }
		public var description: String {
			switch self {
			case .preBowl: return Strings.Series.Properties.PreBowl.preBowl
			case .regularPlay: return Strings.Series.Properties.PreBowl.regular
			}
		}
	}
}

extension Series {
	public enum ExcludeFromStatistics: Int, Sendable, Identifiable, CaseIterable, Codable, CustomStringConvertible {
		case include = 0
		case exclude = 1

		public var id: Int { rawValue }
		public var description: String {
			switch self {
			case .include: return Strings.Series.Properties.ExcludeFromStatistics.include
			case .exclude: return Strings.Series.Properties.ExcludeFromStatistics.exclude
			}
		}
	}
}
