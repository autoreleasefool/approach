import Foundation
import GRDB
import ModelsLibrary

extension League {
	public struct Database: Sendable, Identifiable, Codable, Equatable {
		public let bowlerId: Bowler.ID
		public let id: League.ID
		public var name: String
		public var recurrence: Recurrence
		public var numberOfGames: Int?
		public var additionalPinfall: Int?
		public var additionalGames: Int?
		public var excludeFromStatistics: ExcludeFromStatistics
		public var isArchived: Bool

		public init(
			bowlerId: Bowler.ID,
			id: League.ID,
			name: String,
			recurrence: Recurrence,
			numberOfGames: Int?,
			additionalPinfall: Int?,
			additionalGames: Int?,
			excludeFromStatistics: ExcludeFromStatistics,
			isArchived: Bool
		) {
			self.bowlerId = bowlerId
			self.id = id
			self.name = name
			self.recurrence = recurrence
			self.numberOfGames = numberOfGames
			self.additionalPinfall = additionalPinfall
			self.additionalGames = additionalGames
			self.excludeFromStatistics = excludeFromStatistics
			self.isArchived = isArchived
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
		public static let numberOfGames = Column(CodingKeys.numberOfGames)
		public static let additionalPinfall = Column(CodingKeys.additionalPinfall)
		public static let additionalGames = Column(CodingKeys.additionalGames)
		public static let excludeFromStatistics = Column(CodingKeys.excludeFromStatistics)
		public static let isArchived = Column(CodingKeys.isArchived)
	}
}

extension DerivableRequest<League.Database> {
	public func orderByName() -> Self {
		let name = League.Database.Columns.name
		return order(name.collating(.localizedCaseInsensitiveCompare))
	}

	public func bowled(byBowler: Bowler.ID) -> Self {
		let bowler = League.Database.Columns.bowlerId
		return filter(bowler == byBowler)
	}

	public func filter(byRecurrence: League.Recurrence?) -> Self {
		guard let byRecurrence else { return self }
		let recurrence = League.Database.Columns.recurrence
		return filter(recurrence == byRecurrence)
	}
}

extension League.Summary: FetchableRecord {}

extension League.List: FetchableRecord {}
