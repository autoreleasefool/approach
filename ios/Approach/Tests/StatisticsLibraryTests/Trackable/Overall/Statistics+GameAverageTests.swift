import Dependencies
import ModelsLibrary
@testable import StatisticsLibrary
import XCTest

final class GameAverageTests: XCTestCase {
	func testAdjustByGame() {
		var statistic = Statistics.GameAverage()

		let gameList = [
			Game.TrackableEntry(seriesId: UUID(0), id: UUID(0), score: 100, date: Date(timeIntervalSince1970: 123)),
			Game.TrackableEntry(seriesId: UUID(0), id: UUID(1), score: 120, date: Date(timeIntervalSince1970: 123)),
			Game.TrackableEntry(seriesId: UUID(0), id: UUID(2), score: 90, date: Date(timeIntervalSince1970: 123)),
		]

		for game in gameList {
			statistic.adjust(byGame: game, configuration: .init())
		}

		AssertAveraging(statistic, hasTotal: 310, withDivisor: 3)
	}

	func testDoesNotAdjustByZeroGames() {
		var statistic = Statistics.GameAverage()

		let gameList = [
			Game.TrackableEntry(seriesId: UUID(0), id: UUID(0), score: 0, date: Date(timeIntervalSince1970: 123)),
			Game.TrackableEntry(seriesId: UUID(0), id: UUID(1), score: 0, date: Date(timeIntervalSince1970: 123)),
			Game.TrackableEntry(seriesId: UUID(0), id: UUID(2), score: 0, date: Date(timeIntervalSince1970: 123)),
		]

		for game in gameList {
			statistic.adjust(byGame: game, configuration: .init())
		}

		AssertAveraging(statistic, hasTotal: 0, withDivisor: 0)
	}
}
