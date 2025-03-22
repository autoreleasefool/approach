@testable import AchievementsLibrary
import Foundation
import Testing
import TestUtilitiesLibrary

@Suite("Achievement+TenYears", .tags(.library))
struct TenYearsTests {

	@Test("Consumes expected events", .tags(.unit))
	func consumesExpectedEvents() {
		let tenYearsId1 = UUID()
		let tenYearsId2 = UUID()
		let iconistaId = UUID()

		let events: [any ConsumableAchievementEvent] = [
			EarnableAchievements.TenYears.Events.TenYearsBadgeClaimed(id: tenYearsId1),
			EarnableAchievements.Iconista.Events.AppIconsViewed(id: iconistaId),
			EarnableAchievements.TenYears.Events.TenYearsBadgeClaimed(id: tenYearsId2),
		]

		let expectedConsumed = Set([tenYearsId1, tenYearsId2])

		let expectedEarned = [
			EarnableAchievements.TenYears(),
			EarnableAchievements.TenYears(),
		]

		let (consumed, earned) = EarnableAchievements.TenYears.consume(from: events)

		#expect(consumed == expectedConsumed)
		#expect(earned == expectedEarned)
	}

	@Test("Includes all events", .tags(.unit))
	func includesAllEvents() {
		let allEvents = EarnableAchievements.TenYears.events.map { $0.title }

		#expect(allEvents == [
			EarnableAchievements.TenYears.Events.TenYearsBadgeClaimed.title,
		])
	}

	@Test("Events are findable by title", .tags(.unit))
	func eventsAreFindableByTitle() {
		let allEvents = EarnableAchievements.TenYears.events
		let allEventsTitles = allEvents.map { $0.title }

		for title in allEventsTitles {
			#expect(EarnableAchievements.TenYears.eventsByTitle[title] != nil)
		}
	}
}
