import GRDB
import ModelsLibrary

extension BowlerPreferredGear.Database {
	public static let bowler = belongsTo(Bowler.Database.self)
	public static let gear = belongsTo(Gear.Database.self)
}
