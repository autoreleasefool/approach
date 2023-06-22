import Dependencies
import Foundation
@testable import ModelsLibrary
@testable import StatisticsLibrary

func create<T: Statistic>(
	statistic: T.Type,
	adjustedBySeries: [Series.TrackableEntry] = [],
	withSeriesConfiguration: TrackablePerSeriesConfiguration = .default,
	adjustedByGames: [Game.TrackableEntry] = [],
	withGameConfiguration: TrackablePerGameConfiguration = .default,
	adjustedByFrames: [Frame.TrackableEntry] = [],
	withFrameConfiguration: TrackablePerFrameConfiguration = .default
) -> T {
	var stat = statistic.init()

	for series in adjustedBySeries {
		stat.adjust(bySeries: series, configuration: withSeriesConfiguration)
	}

	for game in adjustedByGames {
		stat.adjust(byGame: game, configuration: withGameConfiguration)
	}

	for frame in adjustedByFrames {
		stat.adjust(byFrame: frame, configuration: withFrameConfiguration)
	}

	return stat
}

extension Series.TrackableEntry {
	static var mocks: [Series.TrackableEntry] {
		(1..<100).flatMap {
			[
				Series.TrackableEntry(id: UUID($0), numberOfGames: $0, total: 123, date: Date(timeIntervalSince1970: 123)),
				Series.TrackableEntry(id: UUID($0), numberOfGames: $0, total: 456, date: Date(timeIntervalSince1970: 456)),
			]
		}
	}
}

extension Game.TrackableEntry {
	static var mocks: [Game.TrackableEntry] {
		(1..<10).flatMap {
			[
				Game.TrackableEntry(seriesId: UUID(0), id: UUID($0), score: 123, date: Date(timeIntervalSince1970: 123)),
				Game.TrackableEntry(seriesId: UUID(0), id: UUID($0), score: 234, date: Date(timeIntervalSince1970: 234)),
			]
		}
	}
}

extension Frame.TrackableEntry {
	static var mocks: [Frame.TrackableEntry] {
		(1..<10).map {
			Frame.TrackableEntry(
				seriesId: UUID(0),
				gameId: UUID(0),
				index: $0,
				rolls: [.init(index: 0, roll: .default), .init(index: 1, roll: .init(pinsDowned: [.leftTwoPin, .leftThreePin, .headPin, .rightThreePin, .rightTwoPin], didFoul: true))],
				date: Date(timeIntervalSince1970: 123)
			)
		}
	}
}
