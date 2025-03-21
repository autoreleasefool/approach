@testable import AchievementsLibrary
import Testing
import TestUtilitiesLibrary

@Suite("Achievement+TenYears", .tags(.library))
struct TenYearsTests {

	@Test("Consumes expected events", .tags(.unit))
	func consumesExpectedEvents() {
		var events: [any ConsumableAchievementEvent] = [
			EarnableAchievements.TenYears.Events.TenYearsBadgeClaimed(),
			EarnableAchievements.Iconista.Events.AppIconsViewed(),
			EarnableAchievements.TenYears.Events.TenYearsBadgeClaimed(),
		]

		let expectedEvents = [
			EarnableAchievements.Iconista.Events.AppIconsViewed(),
		].map { $0.title }

		let expectedEarned = [
			EarnableAchievements.TenYears(),
			EarnableAchievements.TenYears(),
		]

		let earned = EarnableAchievements.TenYears.consume(from: &events)

		#expect(events.map { $0.title } == expectedEvents)
		#expect(earned == expectedEarned)
	}

	@Test("Includes all events", .tags(.unit))
	func includesAllEvents() {
		let allEvents = EarnableAchievements.TenYears.events.map { $0.title }

		#expect(allEvents == [
			EarnableAchievements.TenYears.Events.TenYearsBadgeClaimed().title,
		])
	}
}
