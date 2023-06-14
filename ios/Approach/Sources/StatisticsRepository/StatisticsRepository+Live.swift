import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import Foundation
import GRDB
import ModelsLibrary
import PreferenceServiceInterface
import StatisticsLibrary
import StatisticsRepositoryInterface

extension StatisticsRepository: DependencyKey {
	public static var liveValue: Self = {
		@Dependency(\.database) var database
		@Dependency(\.preferences) var preferences
		@Dependency(\.uuid) var uuid

		return Self(
			loadSources: { source in
				try database.reader().read {
					switch source {
					case let .bowler(id):
						let request = Bowler.Database
							.filter(id: id)
						guard let sources = try TrackableFilter.SourcesByBowler
							.fetchAll($0, request)
							.first else {
							return nil
						}
						return .init(bowler: sources.bowler, league: nil, series: nil, game: nil)
					case let .league(id):
						let request = League.Database
							.filter(id: id)
							.including(required: League.Database.bowler)
						guard let sources = try TrackableFilter.SourcesByLeague
							.fetchAll($0, request)
							.first else {
							return nil
						}
						return .init(bowler: sources.bowler, league: sources.league, series: nil, game: nil)
					case let .series(id):
						let request = Series.Database
							.filter(id: id)
							.including(required: Series.Database.league)
							.including(required: Series.Database.bowler)
						guard let sources = try TrackableFilter.SourcesBySeries
							.fetchAll($0, request)
							.first else {
							return nil
						}
						return .init(bowler: sources.bowler, league: sources.league, series: sources.series, game: nil)
					case let .game(id):
						let request = Game.Database
							.filter(id: id)
							.including(required: Game.Database.series)
							.including(required: Game.Database.league)
							.including(required: Game.Database.bowler)
						guard let sources = try TrackableFilter.SourcesByGame
							.fetchAll($0, request)
							.first else {
							return nil
						}
						return .init(bowler: sources.bowler, league: sources.league, series: sources.series, game: sources.game)
					}
				}
			},
			loadStaticValues: { filter in
				try database.reader().read {
					var statistics = Statistics.all(forSource: filter.source).map { $0.init() }

					let (series, games, frames) = try filter.buildInitialQueries(db: $0)
					let seriesCursor = try series?
						.annotated(with: Series.Database.trackableGames(filter: .init()).sum(Game.Database.Columns.score).forKey("total"))
						.asRequest(of: Series.TrackableEntry.self)
						.fetchCursor($0)
					let gamesCursor = try games?
						.annotated(withRequired: Game.Database.series.select(
							Series.Database.Columns.id.forKey("seriesid"),
							Series.Database.Columns.date
						))
						.asRequest(of: Game.TrackableEntry.self)
						.fetchCursor($0)
					let framesCursor = try frames?
						.annotated(withRequired: Frame.Database.series.select(
							Series.Database.Columns.id.forKey("seriesId"),
							Series.Database.Columns.date
						))
						.asRequest(of: Frame.TrackableEntry.self)
						.fetchCursor($0)

					let builder = StatisticsBuilder(
						perSeriesConfiguration: preferences.perSeriesConfiguration(),
						perGameConfiguration: preferences.perGameConfiguration(),
						perFrameConfiguration: preferences.perFrameConfiguration(),
						aggregator: nil
					)

					try builder.adjust(statistics: &statistics, bySeries: seriesCursor)
					try builder.adjust(statistics: &statistics, byGames: gamesCursor)
					try builder.adjust(statistics: &statistics, byFrames: framesCursor)

					return statistics
				}
			},
			loadChart: { statistic, filter in
				guard statistic.supports(trackableSource: filter.source) else { return [] }

				let builder = StatisticsBuilder(
					perSeriesConfiguration: preferences.perSeriesConfiguration(),
					perGameConfiguration: preferences.perGameConfiguration(),
					perFrameConfiguration: preferences.perFrameConfiguration(),
					aggregator: .init(uuid: uuid, aggregation: filter.aggregation)
				)

				return try database.reader().read { db in
					let results: [ChartEntry]

					if let graphable = statistic as? (any GraphablePerSeries.Type) {
						let (series, _, _) = try filter.buildInitialQueries(db: db)
						guard let series else { return [] }
						let request = series
							.annotated(with: Series.Database.trackableGames(filter: .init()).sum(Game.Database.Columns.score).forKey("total"))
							.asRequest(of: Series.TrackableEntry.self)

						results = try builder.buildChart(forStatistic: graphable, withSeries: request, in: db)
					} else if let graphable = statistic as? (any GraphablePerGame.Type) {
						let (_, games, _) = try filter.buildInitialQueries(db: db)
						guard let games else { return [] }
						let request = games
							.annotated(withRequired: Game.Database.series.select(
								Series.Database.Columns.id.forKey("seriesid"),
								Series.Database.Columns.date
							))
							.asRequest(of: Game.TrackableEntry.self)

						results = try builder.buildChart(forStatistic: graphable, withGames: request, in: db)
					} else if let graphable = statistic as? (any GraphablePerFrame.Type) {
						let (_, _, frames) = try filter.buildInitialQueries(db: db)
						guard let frames else { return [] }
						let request = frames
							.annotated(withRequired: Frame.Database.series.select(
								Series.Database.Columns.id.forKey("seriesId"),
								Series.Database.Columns.date
							))
							.asRequest(of: Frame.TrackableEntry.self)

						results = try builder.buildChart(forStatistic: graphable, withFrames: request, in: db)
					} else {
						return []
					}

					return results
				}
			}
		)
	}()
}
