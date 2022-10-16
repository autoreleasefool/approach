import Foundation
import RealmSwift
import SharedModelsLibrary

public class PersistentSeries: Object, ObjectKeyIdentifiable {
	@Persisted(primaryKey: true) public var _id: UUID
	@Persisted public var date: Date

	@Persisted public var games: List<PersistentGame>
	@Persisted(originProperty: "series") public var league: LinkingObjects<PersistentLeague>
}

// MARK: - Model

extension PersistentSeries {
	public convenience init(from series: Series) {
		self.init()
		self._id = series.id
		self.date = series.date
	}

	public var series: Series {
		.init(id: _id, date: date)
	}
}
