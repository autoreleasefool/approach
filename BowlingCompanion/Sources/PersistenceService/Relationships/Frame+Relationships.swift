import GRDB
import SharedModelsLibrary

extension Frame {
	static let game = belongsTo(Game.self)
}
