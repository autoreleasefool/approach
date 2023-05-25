import Dependencies
import ModelsLibrary
@testable import StatisticsLibrary
import XCTest

final class HighSingleTests: XCTestCase {
	func testAdjustByGame() {
		var statistic = Statistics.HighSingle()

		let gameList = [
			Game.TrackableEntry(id: UUID(0), score: 100),
			Game.TrackableEntry(id: UUID(1), score: 120),
			Game.TrackableEntry(id: UUID(2), score: 90),
		]

		for game in gameList {
			statistic.adjust(byGame: game, configuration: .init())
		}

		XCTAssertEqual(statistic.value, "120")
	}
}
