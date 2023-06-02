import GRDB
import ModelsLibrary

extension GameGear.Database {
	public static let game = belongsTo(Game.Database.self)
	public static let gear = belongsTo(Gear.Database.self)
}
