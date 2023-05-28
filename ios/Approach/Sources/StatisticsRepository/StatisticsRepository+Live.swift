import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import GRDB
import ModelsLibrary
import PreferenceServiceInterface
import StatisticsLibrary
import StatisticsRepositoryInterface

extension StatisticsRepository: DependencyKey {
	public static var liveValue: Self = {
		@Dependency(\.database) var database
		@Dependency(\.preferences) var preferences

		@Sendable func perFrameConfiguration() -> TrackablePerFrameConfiguration {
			.init(
				countHeadPin2AsHeadPin: preferences.bool(forKey: .statisticsCountH2AsH) ?? false
			)
		}

		@Sendable func perGameConfiguration() -> TrackablePerGameConfiguration {
			.init()
		}

		@Sendable func perSeriesConfiguration() -> TrackablePerSeriesConfiguration {
			.init()
		}

		@Sendable func adjustStatistics(
			_ statistics: inout [any Statistic],
			bySeries: RecordCursor<Series.TrackableEntry>
		) throws {
			let perSeries = perSeriesConfiguration()
			while let series = try bySeries.next() {
				for index in statistics.startIndex..<statistics.endIndex {
					guard var seriesTrackable = statistics[index] as? any TrackablePerSeries else { continue }
					seriesTrackable.adjust(bySeries: series, configuration: perSeries)
					statistics[index] = seriesTrackable
				}
			}
		}

		@Sendable func adjustStatistics(
			_ statistics: inout [any Statistic],
			byGame: RecordCursor<Game.TrackableEntry>
		) throws {
			let perGame = perGameConfiguration()
			while let game = try byGame.next() {
				for index in statistics.startIndex..<statistics.endIndex {
					guard var gameTrackable = statistics[index] as? any TrackablePerGame else { continue }
					gameTrackable.adjust(byGame: game, configuration: perGame)
					statistics[index] = gameTrackable
				}
			}
		}

		@Sendable func adjustStatistics(
			_ statistics: inout [any Statistic],
			byFrame: RecordCursor<Frame.TrackableEntry>
		) throws {
			let perFrame = perFrameConfiguration()
			while let frame = try byFrame.next() {
				for index in statistics.startIndex..<statistics.endIndex {
					guard var frameTrackable = statistics[index] as? any TrackablePerFrame else { continue }
					frameTrackable.adjust(byFrame: frame, configuration: perFrame)
					statistics[index] = frameTrackable
				}
			}
		}

		return Self(
			loadForBowler: { id in
				try database.reader().read {
					var statistics = Statistics.all.map { $0.init() }
					guard let bowler = try Bowler.Database.fetchOne($0, id: id) else {
						throw RecordError.recordNotFound(databaseTableName: "bowler", key: ["id": id.uuidString.databaseValue])
					}

					let seriesCursor = try bowler
						.request(
							for: Bowler.Database.trackableSeries
								.annotated(with: Series.Database.trackableGames.sum(Game.Database.Columns.score).forKey("total"))
						)
						.asRequest(of: Series.TrackableEntry.self)
						.fetchCursor($0)

					let gamesCursor = try bowler
						.request(for: Bowler.Database.trackableGames)
						.asRequest(of: Game.TrackableEntry.self)
						.fetchCursor($0)

					let framesCursor = try bowler
						.request(for: Bowler.Database.trackableFrames)
						.asRequest(of: Frame.TrackableEntry.self)
						.fetchCursor($0)

					try adjustStatistics(&statistics, bySeries: seriesCursor)
					try adjustStatistics(&statistics, byGame: gamesCursor)
					try adjustStatistics(&statistics, byFrame: framesCursor)
					return statistics
				}
			}
		)
	}()
}
