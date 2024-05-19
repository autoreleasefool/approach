// swiftlint:disable file_length
import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import FeatureFlagsServiceInterface
import Foundation
import GRDB
import ModelsLibrary
import PreferenceServiceInterface
import StatisticsLibrary
import StatisticsRepositoryInterface
import StatisticsWidgetsLibrary
import UserDefaultsPackageServiceInterface

extension StatisticsRepository: DependencyKey {
	public static var liveValue: Self {
		@Sendable func isSeenKey(forStatistic: Statistic.Type) -> String {
			"Statistic.IsSeen.\(forStatistic.title)"
		}

		// MARK: - Per Series

		@Sendable func adjust(statistics: inout [Statistic], bySeries: RecordCursor<Series.TrackableEntry>?) throws {
			@Dependency(\.preferences) var preferences

			let perSeriesConfiguration = preferences.perSeriesConfiguration()
			while let series = try bySeries?.next() {
				for index in statistics.startIndex..<statistics.endIndex {
					statistics[index].adjust(bySeries: series, configuration: perSeriesConfiguration)
				}
			}
		}

		@Sendable func accumulate<Key: ChartEntryKey>(
			statistic: Statistic.Type,
			bySeries: RecordCursor<Series.TrackableEntry>?
		) throws -> [Key: Statistic] {
			@Dependency(\.preferences) var preferences

			let perSeriesConfiguration = preferences.perSeriesConfiguration()
			var allEntries: [Key: Statistic] = [:]
			while let series = try bySeries?.next() {
				guard let key = Key.extractKey(from: series) else { continue }
				if allEntries[key] == nil {
					allEntries[key] = statistic.init()
				}

				allEntries[key]?.adjust(bySeries: series, configuration: perSeriesConfiguration)
			}
			return allEntries
		}

		// MARK: - Per Game

		@Sendable func adjust(statistics: inout [Statistic], byGames: RecordCursor<Game.TrackableEntry>?) throws {
			@Dependency(\.preferences) var preferences

			let perGameConfiguration = preferences.perGameConfiguration()
			while let game = try byGames?.next() {
				for index in statistics.startIndex..<statistics.endIndex {
					statistics[index].adjust(byGame: game, configuration: perGameConfiguration)
				}
			}
		}

		@Sendable func accumulate<Key: ChartEntryKey>(
			statistic: Statistic.Type,
			byGames: RecordCursor<Game.TrackableEntry>?
		) throws -> [Key: Statistic] {
			@Dependency(\.preferences) var preferences

			let perGameConfiguration = preferences.perGameConfiguration()
			var allEntries: [Key: Statistic] = [:]
			while let game = try byGames?.next() {
				guard let key = Key.extractKey(from: game) else { continue }
				if allEntries[key] == nil {
					allEntries[key] = statistic.init()
				}

				allEntries[key]?.adjust(byGame: game, configuration: perGameConfiguration)
			}
			return allEntries
		}

		// MARK: - Per Frame

		@Sendable func adjust(statistics: inout [Statistic], byFrames: RecordCursor<Frame.TrackableEntry>?) throws {
			@Dependency(\.preferences) var preferences

			let perFrameConfiguration = preferences.perFrameConfiguration()
			while let frame = try byFrames?.next() {
				for index in statistics.startIndex..<statistics.endIndex {
					statistics[index].adjust(byFrame: frame, configuration: perFrameConfiguration)
				}
			}
		}

		@Sendable func accumulate<Key: ChartEntryKey>(
			statistic: Statistic.Type,
			byFrames: RecordCursor<Frame.TrackableEntry>?
		) throws -> [Key: Statistic] {
			@Dependency(\.preferences) var preferences

			let perFrameConfiguration = preferences.perFrameConfiguration()
			var allEntries: [Key: Statistic] = [:]
			while let frame = try byFrames?.next() {
				guard let key = Key.extractKey(from: frame) else { continue }
				if allEntries[key] == nil {
					allEntries[key] = statistic.init()
				}

				allEntries[key]?.adjust(byFrame: frame, configuration: perFrameConfiguration)
			}
			return allEntries
		}

		@Sendable func buildEntries<Key: ChartEntryKey>(
			forStatistic statistic: Statistic.Type,
			filter: TrackableFilter,
			db: Database
		) throws -> [Key: Statistic]? {
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

		@Sendable func loadSources(source: TrackableFilter.Source) async throws -> TrackableFilter.Sources {
			@Dependency(DatabaseService.self) var database

			return try await database.reader().read {
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
		}

		// MARK: - Implementation

		return Self(
			loadSources: loadSources(source:),
			loadDefaultSources: {
				@Dependency(DatabaseService.self) var database
				@Dependency(\.preferences) var preferences

				let decoder = JSONDecoder()
				if let lastUsedSource = preferences.string(forKey: .statisticsLastUsedTrackableFilterSource),
					 let data = lastUsedSource.data(using: .utf8),
					 let source = try? decoder.decode(TrackableFilter.Source.self, from: data) {
					return try await loadSources(source: source)
				}

				return try await database.reader().read {
					let bowlers = try Bowler.Database
						.limit(2)
						.filter(byKind: .playable)
						.asRequest(of: Bowler.Summary.self)
						.fetchAll($0)

					guard let bowler = bowlers.first, bowlers.count == 1 else {
						return nil
					}

					return TrackableFilter.Sources(bowler: bowler, league: nil, series: nil, game: nil)
				}
			},
			saveLastUsedSource: { source in
				@Dependency(\.preferences) var preferences

				let encoder = JSONEncoder()
				guard let data = try? encoder.encode(source),
							let lastUsed = String(data: data, encoding: .utf8) else {
					return
				}

				preferences.setString(forKey: .statisticsLastUsedTrackableFilterSource, to: lastUsed)
			},
			loadValues: { filter in
				@Dependency(DatabaseService.self) var database
				@Dependency(\.preferences) var preferences
				@Dependency(\.userDefaults) var userDefaults

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

				let frameConfiguration = preferences.perFrameConfiguration()
				let isHidingZeroStatistics = preferences.bool(forKey: .statisticsHideZeroStatistics) ?? true
				let isShowingStatisticDescriptions = !(preferences.bool(forKey: .statisticsHideStatisticsDescriptions) ?? false)

				return StatisticCategory.allCases.compactMap { category in
					let categoryStatistics = statistics
						.filter { type(of: $0).category == category }
						.filter { isHidingZeroStatistics ? !$0.isEmpty : true }

					guard !categoryStatistics.isEmpty else { return nil }

					return .init(
						title: String(describing: category),
						description: isShowingStatisticDescriptions
						? category.detailedDescription(frameConfiguration: frameConfiguration)
						: nil,
						images: isShowingStatisticDescriptions
						? category.imageAssets(frameConfiguration: frameConfiguration)?.asListEntryImages()
						: nil,
						entries: .init(uniqueElements: categoryStatistics.map {
							let statistic = type(of: $0)
							let describable = statistic as? DescribableStatistic.Type

							return .init(
								title: statistic.title,
								description: describable?.pinDescription,
								value: $0.formattedValue,
								valueDescription: $0.formattedValueDescription,
								highlightAsNew: statistic.isEligibleForNewLabel
									&& userDefaults.bool(forKey: isSeenKey(forStatistic: statistic)) != true
							)
						})
					)
				}
			},
			loadChart: { statistic, filter in
				@Dependency(DatabaseService.self) var database
				@Dependency(\.uuid) var uuid

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
					let chart: Statistics.ChartContent?
					switch filter.source {
					case .bowler, .league, .game:
						let entries: [EntryDate: Statistic]? = try buildEntries(forStatistic: statistic, filter: filter, db: db)
						chart = chartBuilder.buildChart(withEntries: entries, forStatistic: statistic)
					case .series:
						let entries: [EntryGameOrdinal: Statistic]? = try buildEntries(forStatistic: statistic, filter: filter, db: db)
						chart = chartBuilder.buildChart(withEntries: entries, forStatistic: statistic)
					}

					guard let chart else { return unavailable() }

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
				@Dependency(DatabaseService.self) var database

				return try await database.reader().read {
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
			loadDefaultWidgetSources: {
				@Dependency(DatabaseService.self) var database

				return try await database.reader().read {
					let bowlers = try Bowler.Database
						.limit(2)
						.filter(byKind: .playable)
						.asRequest(of: Bowler.Summary.self)
						.fetchAll($0)

					guard let bowler = bowlers.first, bowlers.count == 1 else {
						return nil
					}

					return .init(bowler: bowler, league: nil)
				}
			},
			loadWidgetData: { configuration in
				@Dependency(\.calendar) var calendar
				@Dependency(\.date) var date
				@Dependency(DatabaseService.self) var database
				@Dependency(\.uuid) var uuid

				@Sendable func unavailable() -> Statistics.ChartContent {
					.chartUnavailable(statistic: configuration.statistic)
				}

				@Sendable func dataMissing() -> Statistics.ChartContent {
					.dataMissing(statistic: configuration.statistic)
				}

				guard let statistic = Statistics.type(of: configuration.statistic) else { return unavailable() }

				return try await database.reader().read { db in
					guard let filter = configuration.trackableFilter(relativeTo: date(), in: calendar) else {
						return unavailable()
					}

					// FIXME: should user be able to choose accumulate/periodic?
					let chartBuilder = ChartBuilder(uuid: uuid, aggregation: .accumulate)

					let chart: Statistics.ChartContent?
					switch filter.source {
					case .bowler, .league, .game:
						let entries: [EntryDate: Statistic]? = try buildEntries(forStatistic: statistic, filter: filter, db: db)
						chart = chartBuilder.buildChart(withEntries: entries, forStatistic: statistic)
					case .series:
						let entries: [EntryGameOrdinal: Statistic]? = try buildEntries(forStatistic: statistic, filter: filter, db: db)
						chart = chartBuilder.buildChart(withEntries: entries, forStatistic: statistic)
					}

					guard let chart else { return unavailable() }

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
			hideNewStatisticLabels: {
				@Dependency(\.userDefaults) var userDefaults
				for statistic in Statistics.allCases {
					userDefaults.setBool(forKey: isSeenKey(forStatistic: statistic), to: true)
				}
			}
		)
	}
}
