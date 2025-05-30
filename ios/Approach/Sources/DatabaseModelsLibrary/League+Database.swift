import Foundation
import GRDB
import ModelsLibrary

extension League {
	public struct Database: Archivable, Sendable, Identifiable, Codable, Equatable {
		public let bowlerId: Bowler.ID
		public let id: League.ID
		public var name: String
		public var recurrence: Recurrence
		public var defaultNumberOfGames: Int?
		public var additionalPinfall: Int?
		public var additionalGames: Int?
		public var excludeFromStatistics: ExcludeFromStatistics
		public var archivedOn: Date?

		public init(
			bowlerId: Bowler.ID,
			id: League.ID,
			name: String,
			recurrence: Recurrence,
			defaultNumberOfGames: Int?,
			additionalPinfall: Int?,
			additionalGames: Int?,
			excludeFromStatistics: ExcludeFromStatistics,
			archivedOn: Date?
		) {
			self.bowlerId = bowlerId
			self.id = id
			self.name = name
			self.recurrence = recurrence
			self.defaultNumberOfGames = defaultNumberOfGames
			self.additionalPinfall = additionalPinfall
			self.additionalGames = additionalGames
			self.excludeFromStatistics = excludeFromStatistics
			self.archivedOn = archivedOn
		}
	}
}

extension League.Database: TableRecord, FetchableRecord, PersistableRecord {
	public static let databaseTableName = "league"
}

extension League.Recurrence: DatabaseValueConvertible {}
extension League.ExcludeFromStatistics: DatabaseValueConvertible {}

extension League.Database {
	public enum Columns {
		public static let bowlerId = Column(CodingKeys.bowlerId)
		public static let id = Column(CodingKeys.id)
		public static let name = Column(CodingKeys.name)
		public static let recurrence = Column(CodingKeys.recurrence)
		public static let defaultNumberOfGames = Column(CodingKeys.defaultNumberOfGames)
		public static let additionalPinfall = Column(CodingKeys.additionalPinfall)
		public static let additionalGames = Column(CodingKeys.additionalGames)
		public static let excludeFromStatistics = Column(CodingKeys.excludeFromStatistics)
		public static let archivedOn = Column(CodingKeys.archivedOn)
	}
}

extension DerivableRequest<League.Database> {
	public func isIncludedInStatistics() -> Self {
		let excludeFromStatistics = League.Database.Columns.excludeFromStatistics
		return self
			.filter(excludeFromStatistics == League.ExcludeFromStatistics.include)
			.isNotArchived()
	}
}

extension League.Summary: FetchableRecord {}

extension League.List: FetchableRecord {}

extension League.Archived: FetchableRecord {}
