@preconcurrency import GRDB
import ModelsLibrary

extension GameLane.Database {
	public static let game = belongsTo(Game.Database.self)
	public static let lane = belongsTo(Lane.Database.self)
}
