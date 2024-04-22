import DatabaseModelsLibrary
import Dependencies
import Foundation
import GRDB
import GRDBDatabaseTestUtilitiesPackageLibrary
import ModelsLibrary

extension Series.Database {
	public static func mock(
		leagueId: League.ID = UUID(0),
		id: ID,
		date: Date,
		appliedDate: Date? = nil,
		preBowl: Series.PreBowl = .regular,
		excludeFromStatistics: Series.ExcludeFromStatistics = .include,
		alleyId: Alley.ID? = nil,
		archivedOn: Date? = nil
	) -> Self {
		.init(
			leagueId: leagueId,
			id: id,
			date: date,
			appliedDate: appliedDate,
			preBowl: preBowl,
			excludeFromStatistics: excludeFromStatistics,
			alleyId: alleyId,
			archivedOn: archivedOn
		)
	}
}

extension Series.Summary {
	public init(_ from: Series.Database) {
		self.init(
			id: from.id,
			date: from.date
		)
	}
}

extension Series.List {
	public init(_ from: Series.Database, withScores: [Game.Score], withTotal: Int) {
		self.init(
			id: from.id,
			date: from.date,
			appliedDate: from.appliedDate,
			scores: withScores,
			total: withTotal,
			preBowl: from.preBowl
		)
	}
}

func insert(
	series initial: InitialValue<Series.Database>?,
	into db: Database
) throws {
	let series: [Series.Database]
	switch initial {
	case .none, .zero:
		series = []
	case .default:
		series = [
			.init(
				leagueId: UUID(0),
				id: UUID(0),
				date: Date(timeIntervalSince1970: 123_456_000),
				appliedDate: nil,
				preBowl: .regular,
				excludeFromStatistics: .include,
				alleyId: UUID(0),
				archivedOn: nil
			),
			.init(
				leagueId: UUID(0),
				id: UUID(1),
				date: Date(timeIntervalSince1970: 123_457_000),
				appliedDate: nil,
				preBowl: .regular,
				excludeFromStatistics: .include,
				alleyId: UUID(0),
				archivedOn: nil
			),
			.init(
				leagueId: UUID(1),
				id: UUID(2),
				date: Date(timeIntervalSince1970: 123_456_000),
				appliedDate: nil,
				preBowl: .regular,
				excludeFromStatistics: .include,
				alleyId: UUID(1),
				archivedOn: nil
			),
			.init(
				leagueId: UUID(1),
				id: UUID(3),
				date: Date(timeIntervalSince1970: 123_457_000),
				appliedDate: nil,
				preBowl: .regular,
				excludeFromStatistics: .include,
				alleyId: UUID(1),
				archivedOn: nil
			),
		]
	case let .custom(custom):
		series = custom
	}

	try series.forEach { try $0.insert(db) }
}
