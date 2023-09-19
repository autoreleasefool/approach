import Foundation
import GRDB
import ModelsLibrary

extension StatisticsWidget {
	public struct Database: Sendable, Identifiable, Codable, Equatable {
		public let id: StatisticsWidget.ID
		public var created: Date
		public var bowlerId: Bowler.ID?
		public var leagueId: League.ID?
		public var timeline: StatisticsWidget.Timeline
		public var statistic: String
		public var context: String
		public var priority: Int

		public init(
			id: StatisticsWidget.ID,
			created: Date,
			bowlerId: Bowler.ID?,
			leagueId: League.ID?,
			timeline: StatisticsWidget.Timeline,
			statistic: String,
			context: String,
			priority: Int
		) {
			self.id = id
			self.created = created
			self.bowlerId = bowlerId
			self.leagueId = leagueId
			self.timeline = timeline
			self.statistic = statistic
			self.context = context
			self.priority = priority
		}
	}
}

extension StatisticsWidget.Database: TableRecord, FetchableRecord, PersistableRecord {
	public static let databaseTableName = "statisticsWidget"
}

extension StatisticsWidget.Timeline: DatabaseValueConvertible {}

extension StatisticsWidget.Database {
	public enum Columns {
		public static let id = Column(CodingKeys.id)
		public static let created = Column(CodingKeys.created)
		public static let bowlerId = Column(CodingKeys.bowlerId)
		public static let leagueId = Column(CodingKeys.leagueId)
		public static let timeline = Column(CodingKeys.timeline)
		public static let statistic = Column(CodingKeys.statistic)
		public static let context = Column(CodingKeys.context)
		public static let priority = Column(CodingKeys.priority)
	}
}

extension StatisticsWidget.Configuration: FetchableRecord {}
