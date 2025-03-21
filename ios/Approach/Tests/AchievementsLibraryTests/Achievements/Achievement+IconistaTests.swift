@testable import AchievementsLibrary
import Testing
import TestUtilitiesLibrary

@Suite("Achievement+Iconista", .tags(.library))
struct IconistaTests {

	@Test("Consumes expected events", .tags(.unit))
	func consumesExpectedEvents() {
		var events: [any ConsumableAchievementEvent] = [
			EarnableAchievements.Iconista.Events.AppIconsViewed(),
			EarnableAchievements.TenYears.Events.TenYearsBadgeClaimed(),
			EarnableAchievements.Iconista.Events.AppIconsViewed(),
		]

		let expectedEvents = [
			EarnableAchievements.TenYears.Events.TenYearsBadgeClaimed(),
		].map { $0.title }

		let expectedEarned = [
			EarnableAchievements.Iconista(),
			EarnableAchievements.Iconista(),
		]

		let earned = EarnableAchievements.Iconista.consume(from: &events)

		#expect(events.map { $0.title } == expectedEvents)
		#expect(earned == expectedEarned)
	}

	@Test("Includes all events", .tags(.unit))
	func includesAllEvents() {
		let allEvents = EarnableAchievements.Iconista.events.map { $0.title }

		#expect(allEvents == [
			EarnableAchievements.Iconista.Events.AppIconsViewed().title,
		])
	}
}
