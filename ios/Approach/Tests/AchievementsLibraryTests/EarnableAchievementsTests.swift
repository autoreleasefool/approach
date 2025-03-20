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
}
