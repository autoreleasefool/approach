import GRDB
import ModelsLibrary

extension SeriesLane {
	public struct Database: Sendable, Codable, TableRecord {
		public static let databaseTableName = "seriesLane"

		public let seriesId: Series.ID
		public let laneId: Lane.ID

		public init(seriesId: Series.ID, laneId: Lane.ID) {
			self.seriesId = seriesId
			self.laneId = laneId
		}
	}
}

extension SeriesLane.Database: FetchableRecord, PersistableRecord {
	public static let series = belongsTo(Series.Database.self)
	public static let lane = belongsTo(Lane.Database.self)
}

extension SeriesLane.Database {
	public enum Columns {
		public static let seriesId = Column(CodingKeys.seriesId)
		public static let laneId = Column(CodingKeys.laneId)
	}
}
