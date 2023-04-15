import ExtensionsLibrary
import Foundation
import GRDB
import ModelsLibrary

extension Game {
	public struct Database: Sendable, Identifiable, Codable, TableRecord {
		public static let databaseTableName = "game"

		public let series: Series.ID
		public let id: Game.ID
		public var ordinal: Int
		public var locked: Lock
		public var manualScore: Int?
		public var excludeFromStatistics: ExcludeFromStatistics

		public init(
			series: Series.ID,
			id: Game.ID,
			ordinal: Int,
			locked: Lock,
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
}

extension Game.Lock: DatabaseValueConvertible {}
extension Game.ExcludeFromStatistics: DatabaseValueConvertible {}

extension Game.Database: FetchableRecord, PersistableRecord {
	public func willSave(_ db: Database) throws {
		guard id != .placeholder else { throw PlaceholderIDValidationError() }
	}
}

extension Game.Database {
	public enum Columns {
		public static let series = Column(CodingKeys.series)
		public static let id = Column(CodingKeys.id)
		public static let ordinal = Column(CodingKeys.ordinal)
		public static let locked = Column(CodingKeys.locked)
		public static let manualScore = Column(CodingKeys.manualScore)
		public static let excludeFromStatistics = Column(CodingKeys.excludeFromStatistics)
	}
}
