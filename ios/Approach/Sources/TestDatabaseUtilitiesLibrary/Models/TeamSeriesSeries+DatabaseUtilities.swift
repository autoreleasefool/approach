import DatabaseModelsLibrary
import Dependencies
import Foundation
import GRDB
import GRDBDatabaseTestUtilitiesPackageLibrary
import ModelsLibrary

extension TeamSeriesSeries.Database {
	public static func mock(
		teamSeriesId: TeamSeries.ID,
		seriesId: Series.ID,
		position: Int
	) -> Self {
		Self(teamSeriesId: teamSeriesId, seriesId: seriesId, position: position)
	}
}

func insert(
	teamSeriesSeries initial: InitialValue<TeamSeriesSeries.Database>?,
	into db: Database
) throws {
	let teamSeriesSeries: [TeamSeriesSeries.Database]
	switch initial {
	case .none, .zero:
		teamSeriesSeries = []
	case .default:
		teamSeriesSeries = [
			.mock(teamSeriesId: UUID(0), seriesId: UUID(0), position: 0),
			.mock(teamSeriesId: UUID(1), seriesId: UUID(2), position: 1),
			.mock(teamSeriesId: UUID(2), seriesId: UUID(1), position: 0),
			.mock(teamSeriesId: UUID(3), seriesId: UUID(3), position: 1),
		]
	case let .custom(custom):
		teamSeriesSeries = custom
	}

	for series in teamSeriesSeries {
		try series.insert(db)
	}
}

