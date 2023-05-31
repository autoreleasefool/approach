import GRDB
import ModelsLibrary

extension SeriesLane {
	public struct Database: Sendable, Codable, Equatable {
		public let seriesId: Series.ID
		public let laneId: Lane.ID

		public init(seriesId: Series.ID, laneId: Lane.ID) {
			self.seriesId = seriesId
			self.laneId = laneId
		}
	}
}

extension SeriesLane.Database: TableRecord, FetchableRecord, PersistableRecord {
	public static let databaseTableName = "seriesLane"
}

extension SeriesLane.Database {
	public enum Columns {
		public static let seriesId = Column(CodingKeys.seriesId)
		public static let laneId = Column(CodingKeys.laneId)
	}
}
