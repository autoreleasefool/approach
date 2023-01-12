import GRDB
import SharedModelsLibrary

extension Series {
	static let seriesLanes = hasMany(SeriesLane.self)
	static let lanes = hasMany(Lane.self, through: seriesLanes, using: SeriesLane.lane)

	var lanes: QueryInterfaceRequest<Lane> {
		request(for: Series.lanes)
	}
}
