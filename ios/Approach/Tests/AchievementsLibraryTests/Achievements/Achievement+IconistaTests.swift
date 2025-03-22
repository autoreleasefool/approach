@testable import AchievementsLibrary
import Foundation
import Testing
import TestUtilitiesLibrary

@Suite("Achievement+Iconista", .tags(.library))
struct IconistaTests {

	@Test("Consumes expected events", .tags(.unit))
	func consumesExpectedEvents() {
		let iconistaId1 = UUID()
		let iconistaId2 = UUID()
		let tenYearsId = UUID()
		let events: [any ConsumableAchievementEvent] = [
			EarnableAchievements.Iconista.Events.AppIconsViewed(id: iconistaId1),
			EarnableAchievements.TenYears.Events.TenYearsBadgeClaimed(id: tenYearsId),
			EarnableAchievements.Iconista.Events.AppIconsViewed(id: iconistaId2),
		]

		let expectedConsumed = Set([iconistaId1, iconistaId2])

		let expectedEarned = [
			EarnableAchievements.Iconista(),
			EarnableAchievements.Iconista(),
		]

		let (consumed, earned) = EarnableAchievements.Iconista.consume(from: events)

		#expect(consumed == expectedConsumed)
		#expect(earned == expectedEarned)
	}

	@Test("Includes all events", .tags(.unit))
	func includesAllEvents() {
		let allEvents = EarnableAchievements.Iconista.events.map { $0.title }

		#expect(allEvents == [
			EarnableAchievements.Iconista.Events.AppIconsViewed.title,
		])
	}

	@Test("Events are findable by title", .tags(.unit))
	func eventsAreFindableByTitle() {
		let allEvents = EarnableAchievements.Iconista.events
		let allEventsTitles = allEvents.map { $0.title }

		for title in allEventsTitles {
			#expect(EarnableAchievements.Iconista.eventsByTitle[title] != nil)
		}
	}
}
