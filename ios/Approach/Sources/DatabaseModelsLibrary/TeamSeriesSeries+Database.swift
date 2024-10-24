import GRDB
import ModelsLibrary

extension TeamSeriesSeries {
	public struct Database: Sendable, Codable, Equatable {
		public let teamSeriesId: TeamSeries.ID
		public let seriesId: Series.ID
		public let position: Int

		public init(teamSeriesId: TeamSeries.ID, seriesId: Series.ID, position: Int) {
			self.teamSeriesId = teamSeriesId
			self.seriesId = seriesId
			self.position = position
		}
	}
}

extension TeamSeriesSeries.Database: TableRecord, FetchableRecord, PersistableRecord {
	public static let databaseTableName = "teamSeriesSeries"
}

extension TeamSeriesSeries.Database {
	public enum Columns {
		public static let teamSeriesId = Column(CodingKeys.teamSeriesId)
		public static let seriesId = Column(CodingKeys.seriesId)
	}
}
