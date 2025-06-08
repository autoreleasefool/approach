import GRDB
import ModelsLibrary

extension TeamBowler.Database {
	public static let team = belongsTo(Team.Database.self)
	public static let bowler = belongsTo(Bowler.Database.self)
}
