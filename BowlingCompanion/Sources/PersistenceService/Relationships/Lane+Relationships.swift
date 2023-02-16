import GRDB
import SharedModelsLibrary

extension Lane {
	static let alley = belongsTo(Alley.self)
	static let seriesLanes = hasMany(SeriesLane.self)
	static let series = hasMany(Series.self, through: seriesLanes, using: SeriesLane.series)
}
