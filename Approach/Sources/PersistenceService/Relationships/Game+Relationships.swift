import GRDB
import SharedModelsLibrary

extension Game {
	static let series = belongsTo(Series.self)
	static let frames = hasMany(Frame.self)

	var frames: QueryInterfaceRequest<Frame> {
		request(for: Game.frames)
	}
}
