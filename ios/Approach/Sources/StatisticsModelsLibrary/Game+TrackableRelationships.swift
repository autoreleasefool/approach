import DatabaseModelsLibrary
import GRDB
import ModelsLibrary
import StatisticsLibrary

extension Game.Database {
	public static func trackableFrames(filter: TrackableFilter.FrameFilter?) -> HasManyAssociation<Self, Frame.Database> {
		var association = hasMany(Frame.Database.self)

		if let bowlingBallIds = filter?.bowlingBallsUsed.map(\.id), !bowlingBallIds.isEmpty {
			let bowlingBallsUsed = Set(bowlingBallIds)
			association = association
				.filter(
					bowlingBallsUsed.contains(Frame.Database.Columns.ball0) ||
					bowlingBallsUsed.contains(Frame.Database.Columns.ball1) ||
					bowlingBallsUsed.contains(Frame.Database.Columns.ball2)
				)
		}

		return association
			.order(Frame.Database.Columns.index.asc)
	}
}
