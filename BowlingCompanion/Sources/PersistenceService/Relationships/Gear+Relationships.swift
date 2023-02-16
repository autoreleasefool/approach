import GRDB
import SharedModelsLibrary

extension Gear {
	static let bowler = belongsTo(Bowler.self)
}
