import DatabaseModelsLibrary
import Dependencies
import Foundation
import GRDB
import GRDBDatabaseTestUtilitiesPackageLibrary
import ModelsLibrary

extension TeamSeries.Database {
	public static func mock(
		id: ID,
		teamId: Team.ID,
		date: Date,
		archivedOn: Date? = nil
	) -> Self {
		Self(id: id, teamId: teamId, date: date, archivedOn: archivedOn)
	}
}

func insert(
	teamSeries initial: InitialValue<TeamSeries.Database>?,
	into db: Database
) throws {
	let teamSeries: [TeamSeries.Database]
	switch initial {
	case .none, .zero:
		teamSeries = []
	case .default:
		teamSeries = [
			.mock(
				id: UUID(0),
				teamId: UUID(0),
				date: Date(timeIntervalSince1970: 123_456_000)
			),
			.mock(
				id: UUID(1),
				teamId: UUID(1),
				date: Date(timeIntervalSince1970: 123_457_000)
			),
		]
	case let .custom(custom):
		teamSeries = custom
	}

	for series in teamSeries {
		try series.insert(db)
	}
}
