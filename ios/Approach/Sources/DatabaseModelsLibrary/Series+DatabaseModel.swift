import ExtensionsLibrary
import Foundation
import GRDB
import ModelsLibrary

extension Series {
	public struct Database: Sendable, Identifiable, Codable, TableRecord {
		public static let databaseTableName = "series"

		public let league: League.ID
		public let id: Series.ID
		public var date: Date
		public var numberOfGames: Int
		public var preBowl: PreBowl
		public var excludeFromStatistics: ExcludeFromStatistics
		public var alley: Alley.ID?

		public init(
			league: League.ID,
			id: Series.ID,
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
}

extension Series.PreBowl: DatabaseValueConvertible {}
extension Series.ExcludeFromStatistics: DatabaseValueConvertible {}

extension Series.Database: FetchableRecord, PersistableRecord {
	public func willSave(_ db: Database) throws {
		guard id != .placeholder else { throw DBValidationError.usingPlaceholderId }
	}
}

extension Series.Database {
	public enum Columns {
		public static let league = Column(CodingKeys.league)
		public static let id = Column(CodingKeys.id)
		public static let date = Column(CodingKeys.date)
		public static let numberOfGames = Column(CodingKeys.numberOfGames)
		public static let preBowl = Column(CodingKeys.preBowl)
		public static let excludeFromStatistics = Column(CodingKeys.excludeFromStatistics)
		public static let alley = Column(CodingKeys.alley)
	}
}
