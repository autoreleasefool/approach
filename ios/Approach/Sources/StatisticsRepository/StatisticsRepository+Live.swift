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

		// MARK: - Per Series

		@Sendable func adjust(statistics: inout [Statistic], bySeries: RecordCursor<Series.TrackableEntry>?) throws {
			let perSeriesConfiguration = preferences.perSeriesConfiguration()
			while let series = try bySeries?.next() {
				for index in statistics.startIndex..<statistics.endIndex {
					statistics[index].adjust(bySeries: series, configuration: perSeriesConfiguration)
				}
			}
		}

		@Sendable func accumulate(
			statistic: Statistic.Type,
			bySeries: RecordCursor<Series.TrackableEntry>?
		) throws -> [Date: Statistic] {
			let perSeriesConfiguration = preferences.perSeriesConfiguration()
			var allEntries: [Date: Statistic] = [:]
			while let series = try bySeries?.next() {
				if allEntries[series.date] == nil {
					allEntries[series.date] = statistic.init()
				}

				allEntries[series.date]?.adjust(bySeries: series, configuration: perSeriesConfiguration)
			}
			return allEntries
		}

		// MARK: - Per Game

		@Sendable func adjust(statistics: inout [Statistic], byGames: RecordCursor<Game.TrackableEntry>?) throws {
			let perGameConfiguration = preferences.perGameConfiguration()
			while let game = try byGames?.next() {
				for index in statistics.startIndex..<statistics.endIndex {
					statistics[index].adjust(byGame: game, configuration: perGameConfiguration)
				}
			}
		}

		@Sendable func accumulate(
			statistic: Statistic.Type,
			byGames: RecordCursor<Game.TrackableEntry>?
		) throws -> [Date: Statistic] {
			let perGameConfiguration = preferences.perGameConfiguration()
			var allEntries: [Date: Statistic] = [:]
			while let game = try byGames?.next() {
				if allEntries[game.date] == nil {
					allEntries[game.date] = statistic.init()
				}

				allEntries[game.date]?.adjust(byGame: game, configuration: perGameConfiguration)
			}
			return allEntries
		}

		// MARK: - Per Frame

		@Sendable func adjust(statistics: inout [Statistic], byFrames: RecordCursor<Frame.TrackableEntry>?) throws {
			let perFrameConfiguration = preferences.perFrameConfiguration()
			while let frame = try byFrames?.next() {
				for index in statistics.startIndex..<statistics.endIndex {
					statistics[index].adjust(byFrame: frame, configuration: perFrameConfiguration)
				}
			}
		}

		@Sendable func accumulate(
			statistic: Statistic.Type,
			byFrames: RecordCursor<Frame.TrackableEntry>?
		) throws -> [Date: Statistic] {
			let perFrameConfiguration = preferences.perFrameConfiguration()
			var allEntries: [Date: Statistic] = [:]
			while let frame = try byFrames?.next() {
				if allEntries[frame.date] == nil {
					allEntries[frame.date] = statistic.init()
				}

				allEntries[frame.date]?.adjust(byFrame: frame, configuration: perFrameConfiguration)
			}
			return allEntries
		}

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
			loadValues: { filter in
				try database.reader().read {
					var statistics = Statistics.all(forSource: filter.source).map { $0.init() }

					let (series, games, frames) = try filter.buildTrackableQueries(db: $0)
					let seriesCursor = try series?.fetchCursor($0)
					let gamesCursor = try games?.fetchCursor($0)
					let framesCursor = try frames?.fetchCursor($0)

					try adjust(statistics: &statistics, bySeries: seriesCursor)
					try adjust(statistics: &statistics, byGames: gamesCursor)
					try adjust(statistics: &statistics, byFrames: framesCursor)

					return StatisticCategory.allCases.compactMap { category in
						let matchingStatistics = statistics.filter { type(of: $0).category == category }
						guard !matchingStatistics.isEmpty else { return nil }
						return .init(
							category: category,
							entries: .init(uniqueElements: matchingStatistics.map {
								.init(title: type(of: $0).title, value: $0.formattedValue)
							})
						)
					}
				}
			},
			loadCountingChart: { statistic, filter in
				guard statistic.supports(trackableSource: filter.source) else { return nil }

				return try database.reader().read { db in
					let chartBuilder = ChartBuilder(uuid: uuid, aggregation: filter.aggregation)
					var output: ChartBuilder.Output?

					if statistic is TrackablePerSeries.Type {
						let (series, _, _) = try filter.buildTrackableQueries(db: db)
						guard let seriesCursor = try series?.fetchCursor(db) else { return nil }
						let entries = try accumulate(statistic: statistic, bySeries: seriesCursor)
						output = chartBuilder.buildChart(withEntries: entries)
					} else if statistic is TrackablePerGame.Type {
						let (_, games, _) = try filter.buildTrackableQueries(db: db)
						guard let gamesCursor = try games?.fetchCursor(db) else { return nil }
						let entries = try accumulate(statistic: statistic, byGames: gamesCursor)
						output = chartBuilder.buildChart(withEntries: entries)
					} else if statistic is TrackablePerFrame.Type {
						let (_, _, frames) = try filter.buildTrackableQueries(db: db)
						guard let framesCursor = try frames?.fetchCursor(db) else { return nil }
						let entries = try accumulate(statistic: statistic, byFrames: framesCursor)
						output = chartBuilder.buildChart(withEntries: entries)
					} else {
						output = nil
					}

					switch output {
					case .averaging: return nil
					case let .counting(data): return data
					case .none: return nil
					}
				}
			},
			loadHighestOfChart: { statistic, filter in
				guard statistic.supports(trackableSource: filter.source) else { return nil }

				return try database.reader().read { db in
					let chartBuilder = ChartBuilder(uuid: uuid, aggregation: filter.aggregation)
					var output: ChartBuilder.Output?

					if statistic is TrackablePerSeries.Type {
						let (series, _, _) = try filter.buildTrackableQueries(db: db)
						guard let seriesCursor = try series?.fetchCursor(db) else { return nil }
						let entries = try accumulate(statistic: statistic, bySeries: seriesCursor)
						output = chartBuilder.buildChart(withEntries: entries)
					} else if statistic is TrackablePerGame.Type {
						let (_, games, _) = try filter.buildTrackableQueries(db: db)
						guard let gamesCursor = try games?.fetchCursor(db) else { return nil }
						let entries = try accumulate(statistic: statistic, byGames: gamesCursor)
						output = chartBuilder.buildChart(withEntries: entries)
					} else if statistic is TrackablePerFrame.Type {
						let (_, _, frames) = try filter.buildTrackableQueries(db: db)
						guard let framesCursor = try frames?.fetchCursor(db) else { return nil }
						let entries = try accumulate(statistic: statistic, byFrames: framesCursor)
						output = chartBuilder.buildChart(withEntries: entries)
					} else {
						output = nil
					}

					switch output {
					case .averaging: return nil
					case let .counting(data): return data
					case .none: return nil
					}
				}
			},
			loadAveragingChart: { statistic, filter in
				guard statistic.supports(trackableSource: filter.source) else { return nil }

				return try database.reader().read { db in
					let chartBuilder = ChartBuilder(uuid: uuid, aggregation: filter.aggregation)
					var output: ChartBuilder.Output?

					if statistic is TrackablePerSeries.Type {
						let (series, _, _) = try filter.buildTrackableQueries(db: db)
						guard let seriesCursor = try series?.fetchCursor(db) else { return nil }
						let entries = try accumulate(statistic: statistic, bySeries: seriesCursor)
						output = chartBuilder.buildChart(withEntries: entries)
					} else if statistic is TrackablePerGame.Type {
						let (_, games, _) = try filter.buildTrackableQueries(db: db)
						guard let gamesCursor = try games?.fetchCursor(db) else { return nil }
						let entries = try accumulate(statistic: statistic, byGames: gamesCursor)
						output = chartBuilder.buildChart(withEntries: entries)
					} else if statistic is TrackablePerFrame.Type {
						let (_, _, frames) = try filter.buildTrackableQueries(db: db)
						guard let framesCursor = try frames?.fetchCursor(db) else { return nil }
						let entries = try accumulate(statistic: statistic, byFrames: framesCursor)
						output = chartBuilder.buildChart(withEntries: entries)
					} else {
						output = nil
					}

					switch output {
					case let .averaging(data): return data
					case .counting: return nil
					case .none: return nil
					}
				}
			}
		)
	}()
}
