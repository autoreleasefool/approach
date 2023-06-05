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

		@Sendable func adjust(statistics: inout [any Statistic], bySeries: RecordCursor<Series.TrackableEntry>?) throws {
			let perSeries = preferences.perSeriesConfiguration()
			while let series = try bySeries?.next() {
				for index in statistics.startIndex..<statistics.endIndex {
					guard var seriesTrackable = statistics[index] as? any TrackablePerSeries else { continue }
					seriesTrackable.adjust(bySeries: series, configuration: perSeries)
					statistics[index] = seriesTrackable
				}
			}
		}

		@Sendable func adjust(statistics: inout [any Statistic], byGames: RecordCursor<Game.TrackableEntry>?) throws {
			let perGame = preferences.perGameConfiguration()
			while let game = try byGames?.next() {
				for index in statistics.startIndex..<statistics.endIndex {
					guard var gameTrackable = statistics[index] as? any TrackablePerGame else { continue }
					gameTrackable.adjust(byGame: game, configuration: perGame)
					statistics[index] = gameTrackable
				}
			}
		}

		@Sendable func adjust(statistics: inout [any Statistic], byFrames: RecordCursor<Frame.TrackableEntry>?) throws {
			let perFrame = preferences.perFrameConfiguration()
			while let frame = try byFrames?.next() {
				for index in statistics.startIndex..<statistics.endIndex {
					guard var frameTrackable = statistics[index] as? any TrackablePerFrame else { continue }
					frameTrackable.adjust(byFrame: frame, configuration: perFrame)
					statistics[index] = frameTrackable
				}
			}
		}

		return Self(
			loadStaticValues: { filter in
				try database.reader().read {
					var statistics = Statistics.all(forSource: filter.source).map { $0.init() }

					let (series, games, frames) = try filter.buildInitialQueries(db: $0)
					let seriesCursor = try series?
						.annotated(with: Series.Database.trackableGames(filter: .init()).sum(Game.Database.Columns.score).forKey("total"))
						.asRequest(of: Series.TrackableEntry.self)
						.fetchCursor($0)
					let gamesCursor = try games?
						.annotated(withRequired: Game.Database.series.select(Series.Database.Columns.date))
						.asRequest(of: Game.TrackableEntry.self)
						.fetchCursor($0)
					let framesCursor = try frames?
						.annotated(withRequired: Frame.Database.series.select(Series.Database.Columns.date))
						.asRequest(of: Frame.TrackableEntry.self)
						.fetchCursor($0)

					try adjust(statistics: &statistics, bySeries: seriesCursor)
					try adjust(statistics: &statistics, byGames: gamesCursor)
					try adjust(statistics: &statistics, byFrames: framesCursor)
					return statistics
				}
			}
		)
	}()
}
