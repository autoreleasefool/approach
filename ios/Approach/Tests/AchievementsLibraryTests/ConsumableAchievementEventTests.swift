@testable import AchievementsLibrary
import Testing
import TestUtilitiesLibrary

@Suite("ConsumableAchievementEvent", .tags(.library))
struct ConsumableAchievementEventTests {

	@Test("All events are uniquely named", .tags(.unit))
	func allEventsAreUniquelyNamed() {
		let allAchievements = EarnableAchievements.allCases
		let allEvents = allAchievements.flatMap { $0.events }
		let allEventTitles = Set(allEvents.map { $0.title })

		#expect(allEvents.count == allEventTitles.count)
	}
}
