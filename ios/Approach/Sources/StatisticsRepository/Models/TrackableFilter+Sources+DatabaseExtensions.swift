import GRDB
import ModelsLibrary
import StatisticsLibrary
import StatisticsRepositoryInterface

extension TrackableFilter {
	struct SourcesByBowler: FetchableRecord {
		var bowler: Bowler.Summary?

		init(row: Row) throws {
			bowler = try Bowler.Summary(row: row)
		}
	}
}

extension TrackableFilter {
	struct SourcesByLeague: FetchableRecord {
		var bowler: Bowler.Summary?
		var league: League.Summary?

		init(row: Row) throws {
			league = try League.Summary(row: row)
			bowler = row["bowler"]
		}
	}
}

extension TrackableFilter {
	struct SourcesBySeries: FetchableRecord {
		var bowler: Bowler.Summary?
		var league: League.Summary?
		var series: Series.Summary?

		init(row: Row) throws {
			series = try Series.Summary(row: row)
			league = row["league"]
			bowler = row["bowler"]
		}
	}
}

extension TrackableFilter {
	struct SourcesByGame: FetchableRecord {
		var bowler: Bowler.Summary?
		var league: League.Summary?
		var series: Series.Summary?
		var game: Game.Summary?

		init(row: Row) throws {
			game = try Game.Summary(row: row)
			series = row["series"]
			league = row["league"]
			bowler = row["bowler"]
		}
	}
}
