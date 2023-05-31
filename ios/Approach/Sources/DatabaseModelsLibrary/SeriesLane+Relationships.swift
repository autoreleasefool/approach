import GRDB
import ModelsLibrary

extension SeriesLane.Database {
	public static let series = belongsTo(Series.Database.self)

	public static let lane = belongsTo(Lane.Database.self)
}
