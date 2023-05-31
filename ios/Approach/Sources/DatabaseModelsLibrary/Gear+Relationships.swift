import GRDB
import ModelsLibrary

extension Gear.Database {
	public static let bowler = belongsTo(Bowler.Database.self)
}
