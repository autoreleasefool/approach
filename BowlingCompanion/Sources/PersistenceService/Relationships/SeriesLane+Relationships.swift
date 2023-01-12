import GRDB
import SharedModelsLibrary

extension SeriesLane {
	static let series = belongsTo(Series.self)
	static let lane = belongsTo(Lane.self)
}
