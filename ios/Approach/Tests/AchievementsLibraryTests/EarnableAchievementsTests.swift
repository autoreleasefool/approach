@testable import AchievementsLibrary
import Testing
import TestUtilitiesLibrary

@Suite("EarnableAchievements", .tags(.library))
struct EarnableAchievementsTests {

	@Test("All achievements are uniquely named", .tags(.unit))
	func allAchievementsAreUniquelyNamed() {
		let allAchievements = EarnableAchievements.allCases
		let allAchievementTitles = Set(allAchievements.map { $0.title })

		#expect(allAchievements.count == allAchievementTitles.count)
	}

	@Test("Achievements are findable by title", .tags(.unit))
	func achievementsAreFindableByTitle() {
		let allAchievements = EarnableAchievements.allCases
		let allAchievementTitles = allAchievements.map { $0.title }

		for title in allAchievementTitles {
			#expect(EarnableAchievements.allCasesByTitle[title] != nil)
		}
	}

	@Test("Achievements are enabled correctly", .tags(.unit))
	func achievementsAreEnabledCorrectly() {
		let enabledAchievements = EarnableAchievements.allCases
			.filter { $0.isEnabled }
			.map { $0.title }

		let expectedAchievements = [
			EarnableAchievements.TenYears.self,
		].map { $0.title }

		#expect(enabledAchievements == expectedAchievements)
	}

	@Test("Achievements are not visible before earned", .tags(.unit))
	func achievementsAreNotVisibleBeforeEarned() {
		let visibleAchievements = EarnableAchievements.allCases
			.filter { $0.isVisibleBeforeEarned }
			.map { $0.title }

		let expectedAchievements = [
			EarnableAchievements.Iconista.self,
		].map { $0.title }

		#expect(visibleAchievements == expectedAchievements)
	}

	@Test("Achievements are not shown on earn", .tags(.unit))
	func achievementsAreNotShownOnEarn() {
		let shownAchievements = EarnableAchievements.allCases
			.filter { $0.showToastOnEarn }
			.map { $0.title }

		let expectedAchievements = [
			EarnableAchievements.Iconista.self,
		].map { $0.title }

		#expect(shownAchievements == expectedAchievements)
	}
}
