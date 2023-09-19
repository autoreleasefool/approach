import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import Foundation
import GRDB
import ModelsLibrary
import PreferenceServiceInterface
import StatisticsLibrary
import StatisticsRepositoryInterface
import StatisticsWidgetsLibrary

extension StatisticsRepository: DependencyKey {
	public static var liveValue: Self = {
		@Dependency(\.calendar) var calendar
		@Dependency(\.database) var database
		@Dependency(\.date) var date
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

		@Sendable func buildEntries(
			forStatistic statistic: Statistic.Type,
			filter: TrackableFilter,
			db: Database
		) throws -> [Date: Statistic]? {
			if statistic is TrackablePerSeries.Type {
				let (series, _, _) = try filter.buildTrackableQueries(db: db)
				guard let seriesCursor = try series?.fetchCursor(db) else { return nil }
				return try accumulate(statistic: statistic, bySeries: seriesCursor)
			} else if statistic is TrackablePerGame.Type {
				let (_, games, _) = try filter.buildTrackableQueries(db: db)
				guard let gamesCursor = try games?.fetchCursor(db) else { return nil }
				return try accumulate(statistic: statistic, byGames: gamesCursor)
			} else if statistic is TrackablePerFrame.Type {
				let (_, _, frames) = try filter.buildTrackableQueries(db: db)
				guard let framesCursor = try frames?.fetchCursor(db) else { return nil }
				return try accumulate(statistic: statistic, byFrames: framesCursor)
			}

			return nil
		}

		// MARK: - Implementation

		return Self(
			loadSources: { source in
				try await database.reader().read {
					switch source {
					case let .bowler(id):
						let request = Bowler.Database
							.filter(id: id)
						let sources = try TrackableFilter.SourcesByBowler
							.fetchOneGuaranteed($0, request)
						return .init(bowler: sources.bowler, league: nil, series: nil, game: nil)
					case let .league(id):
						let request = League.Database
							.filter(id: id)
							.including(required: League.Database.bowler)
						let sources = try TrackableFilter.SourcesByLeague
							.fetchOneGuaranteed($0, request)
						return .init(bowler: sources.bowler, league: sources.league, series: nil, game: nil)
					case let .series(id):
						let request = Series.Database
							.filter(id: id)
							.including(required: Series.Database.league)
							.including(required: Series.Database.bowler)
						let sources = try TrackableFilter.SourcesBySeries
							.fetchOneGuaranteed($0, request)
						return .init(bowler: sources.bowler, league: sources.league, series: sources.series, game: nil)
					case let .game(id):
						let request = Game.Database
							.filter(id: id)
							.including(required: Game.Database.series)
							.including(required: Game.Database.league)
							.including(required: Game.Database.bowler)
						let sources = try TrackableFilter.SourcesByGame
							.fetchOneGuaranteed($0, request)
						return .init(bowler: sources.bowler, league: sources.league, series: sources.series, game: sources.game)
					}
				}
			},
			loadDefaultSources: {
				try await database.reader().read {
					let bowlers = try Bowler.Database
						.limit(2)
						.asRequest(of: Bowler.Summary.self)
						.fetchAll($0)

					guard let bowler = bowlers.first, bowlers.count == 1 else {
						return nil
					}

					return TrackableFilter.Sources(bowler: bowler, league: nil, series: nil, game: nil)
				}
			},
			loadValues: { filter in
				let statistics = try await database.reader().read {
					var statistics = Statistics.all(forSource: filter.source).map { $0.init() }

					let (series, games, frames) = try filter.buildTrackableQueries(db: $0)
					let seriesCursor = try series?.fetchCursor($0)
					let gamesCursor = try games?.fetchCursor($0)
					let framesCursor = try frames?.fetchCursor($0)

					try adjust(statistics: &statistics, bySeries: seriesCursor)
					try adjust(statistics: &statistics, byGames: gamesCursor)
					try adjust(statistics: &statistics, byFrames: framesCursor)

					return statistics
				}

				let isHidingZeroStatistics = preferences.bool(forKey: .statisticsHideZeroStatistics) ?? true

				return StatisticCategory.allCases.compactMap { category in
					var categoryStatistics = statistics
						.filter { type(of: $0).category == category }
						.filter { isHidingZeroStatistics ? !$0.isEmpty : true }

					guard !categoryStatistics.isEmpty else { return nil }

					return .init(
						category: category,
						entries: .init(uniqueElements: categoryStatistics.map {
							.init(title: type(of: $0).title, value: $0.formattedValue)
						})
					)
				}
			},
			loadChart: { statistic, filter in
				@Sendable func unavailable() -> Statistics.ChartContent {
					.chartUnavailable(statistic: statistic.title)
				}

				@Sendable func dataMissing() -> Statistics.ChartContent {
					.dataMissing(statistic: statistic.title)
				}

				guard statistic.supports(trackableSource: filter.source) else {
					return unavailable()
				}

				return try await database.reader().read { db in
					let chartBuilder = ChartBuilder(uuid: uuid, aggregation: filter.aggregation)
					guard let entries = try buildEntries(forStatistic: statistic, filter: filter, db: db),
								let chart = chartBuilder.buildChart(withEntries: entries, forStatistic: statistic)
					else {
						return unavailable()
					}

					switch chart {
					case let .averaging(data):
						return data.isEmpty ? dataMissing() : chart
					case let .counting(data):
						return data.isEmpty ? dataMissing() : chart
					case let .percentage(data):
						return data.isEmpty ? dataMissing() : chart
					case .chartUnavailable:
						return unavailable()
					case .dataMissing:
						return dataMissing()
					}
				}
			},
			loadWidgetSources: { source in
				try await database.reader().read {
					switch source {
					case let .bowler(id):
						let request = Bowler.Database
							.filter(id: id)
						let sources = try StatisticsWidget.Source.SourcesByBowler
							.fetchOneGuaranteed($0, request)
						return .init(bowler: sources.bowler, league: nil)
					case let .league(id):
						let request = League.Database
							.filter(id: id)
							.including(required: League.Database.bowler)
						let sources = try StatisticsWidget.Source.SourcesByLeague
							.fetchOneGuaranteed($0, request)
						return .init(bowler: sources.bowler, league: sources.league)
					}
				}
			},
			loadWidgetData: { configuration in
				let statistic = configuration.statistic.type
				@Sendable func unavailable() -> Statistics.ChartContent {
					.chartUnavailable(statistic: statistic.title)
				}

				@Sendable func dataMissing() -> Statistics.ChartContent {
					.dataMissing(statistic: statistic.title)
				}

				return try await database.reader().read { db in
					guard let filter = configuration.trackableFilter(relativeTo: date(), in: calendar) else {
						return unavailable()
					}

					// FIXME: should user be able to choose accumulate/periodic?
					let chartBuilder = ChartBuilder(uuid: uuid, aggregation: .accumulate)
					guard let entries = try buildEntries(forStatistic: statistic, filter: filter, db: db),
								let chart = chartBuilder.buildChart(withEntries: entries, forStatistic: statistic)
					else {
						return unavailable()
					}

					switch chart {
					case let .averaging(data):
						return data.isEmpty ? dataMissing() : chart
					case let .counting(data):
						return data.isEmpty ? dataMissing() : chart
					case let .percentage(data):
						return data.isEmpty ? dataMissing() : chart
					case .chartUnavailable:
						return unavailable()
					case .dataMissing:
						return dataMissing()
					}
				}
			}
		)
	}()
}
