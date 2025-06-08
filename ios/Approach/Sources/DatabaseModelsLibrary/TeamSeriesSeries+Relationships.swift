import GRDB
import ModelsLibrary

extension TeamSeriesSeries.Database {
	public static let teamSeries = belongsTo(TeamSeries.Database.self)
	public static let series = belongsTo(Series.Database.self)
}
