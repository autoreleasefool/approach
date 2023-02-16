import GRDB
import SharedModelsLibrary

extension League {
	static let bowler = belongsTo(Bowler.self)
	static let series = hasMany(Series.self)

	var series: QueryInterfaceRequest<Series> {
			request(for: League.series)
		}
}
