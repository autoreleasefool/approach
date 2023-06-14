import Dependencies
import Foundation
import GRDB
import ModelsLibrary
import StatisticsLibrary

struct StatisticsBuilder {
	let perSeriesConfiguration: TrackablePerSeriesConfiguration
	let perGameConfiguration: TrackablePerGameConfiguration
	let perFrameConfiguration: TrackablePerFrameConfiguration
	let aggregator: ChartAggregator?

	// MARK: - Per Series

	func adjust(statistics: inout [any Statistic], bySeries: RecordCursor<Series.TrackableEntry>?) throws {
		while let series = try bySeries?.next() {
			for index in statistics.startIndex..<statistics.endIndex {
				guard var seriesTrackable = statistics[index] as? any TrackablePerSeries else { continue }
				seriesTrackable.adjust(bySeries: series, configuration: perSeriesConfiguration)
				statistics[index] = seriesTrackable
			}
		}
	}

	func buildChart(
		forStatistic: any GraphablePerSeries.Type,
		withSeries: QueryInterfaceRequest<Series.TrackableEntry>,
		in db: Database
	) throws -> [ChartEntry] {
		var allEntries: [Date: any GraphablePerSeries] = [:]

		let allSeries = try withSeries.fetchCursor(db)
		while let series = try allSeries.next() {
			if allEntries[series.date] == nil {
				allEntries[series.date] = forStatistic.init()
			}

			allEntries[series.date]?.adjust(bySeries: series, configuration: perSeriesConfiguration)
		}

		return aggregator?.accumulate(entries: allEntries) ?? []
	}

	// MARK: - Per Game

	func adjust(statistics: inout [any Statistic], byGames: RecordCursor<Game.TrackableEntry>?) throws {
		while let game = try byGames?.next() {
			for index in statistics.startIndex..<statistics.endIndex {
				guard var gameTrackable = statistics[index] as? any TrackablePerGame else { continue }
				gameTrackable.adjust(byGame: game, configuration: perGameConfiguration)
				statistics[index] = gameTrackable
			}
		}
	}

	func buildChart(
		forStatistic: any GraphablePerGame.Type,
		withGames: QueryInterfaceRequest<Game.TrackableEntry>,
		in db: Database
	) throws -> [ChartEntry] {
		var allEntries: [Date: any GraphablePerGame] = [:]

		let allGames = try withGames.fetchCursor(db)
		while let game = try allGames.next() {
			var entry = allEntries[game.date] ?? forStatistic.init()
			entry.adjust(byGame: game, configuration: perGameConfiguration)
			allEntries[game.date] = entry
		}

		return aggregator?.accumulate(entries: allEntries) ?? []
	}

	// MARK: - Per Frame

	func adjust(statistics: inout [any Statistic], byFrames: RecordCursor<Frame.TrackableEntry>?) throws {
		while let frame = try byFrames?.next() {
			for index in statistics.startIndex..<statistics.endIndex {
				guard var frameTrackable = statistics[index] as? any TrackablePerFrame else { continue }
				frameTrackable.adjust(byFrame: frame, configuration: perFrameConfiguration)
				statistics[index] = frameTrackable
			}
		}
	}

	func buildChart(
		forStatistic: any GraphablePerFrame.Type,
		withFrames: QueryInterfaceRequest<Frame.TrackableEntry>,
		in db: Database
	) throws -> [ChartEntry] {
		var allEntries: [Date: any GraphablePerFrame] = [:]

		let allFrames = try withFrames.fetchCursor(db)
		while let frame = try allFrames.next() {
			if allEntries[frame.date] == nil {
				allEntries[frame.date] = forStatistic.init()
			}

			allEntries[frame.date]?.adjust(byFrame: frame, configuration: perFrameConfiguration)
		}

		return aggregator?.accumulate(entries: allEntries) ?? []
	}
}
