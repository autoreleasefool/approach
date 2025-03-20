import AchievementsLibrary
import Foundation
import ModelsLibrary

extension Achievement.List {
	public var achievement: EarnableAchievement.Type {
		EarnableAchievements.allCasesByTitle[title]!
	}
}
