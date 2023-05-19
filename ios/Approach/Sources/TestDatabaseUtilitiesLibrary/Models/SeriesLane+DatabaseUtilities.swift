import DatabaseModelsLibrary
import Foundation
import GRDB
import ModelsLibrary

func insert(
	seriesLanes initial: InitialValue<SeriesLane.Database>?,
	into db: Database
) throws {
	let seriesLanes: [SeriesLane.Database]
	switch initial {
	case .none, .zero:
		seriesLanes = []
	case .default:
		seriesLanes = [
			.init(seriesId: UUID(0), laneId: UUID(0)),
			.init(seriesId: UUID(0), laneId: UUID(1)),
			.init(seriesId: UUID(1), laneId: UUID(1)),
			.init(seriesId: UUID(1), laneId: UUID(2)),
			.init(seriesId: UUID(2), laneId: UUID(3)),
			.init(seriesId: UUID(2), laneId: UUID(4)),
			.init(seriesId: UUID(2), laneId: UUID(5)),
		]
	case let .custom(custom):
		seriesLanes = custom
	}

	for lane in seriesLanes {
		try lane.insert(db)
	}
}
