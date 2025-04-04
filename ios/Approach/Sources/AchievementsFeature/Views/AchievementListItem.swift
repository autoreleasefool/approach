import AchievementsLibrary
import AssetsLibrary
import ModelsLibrary
import StringsLibrary
import SwiftUI

struct AchievementListItem: View {
	let achievement: Achievement.List

	var body: some View {
		VStack(spacing: .smallSpacing) {
			InteractiveAchievement(
				achievement.icon,
				isEnabled: false
			)

			Text(achievement.displayName)
				.font(.headline)

			if let firstEarnedAt = achievement.firstEarnedAt {
				VStack(spacing: .tinySpacing) {
					Text(firstEarnedAt.formatted(date: .numeric, time: .omitted))
						.font(.caption)
					
					Text(Strings.Achievements.List.earnedCount(achievement.count))
						.font(.caption)
				}
			}
		}
		.opacity(achievement.firstEarnedAt == nil ? 0.5 : 1)
		.fixedSize(horizontal: false, vertical: true)
	}
}

// MARK: - Preview

#Preview {
	HStack {
		AchievementListItem(
			achievement: .init(
				title: EarnableAchievements.TenYears.title,
				firstEarnedAt: Date(),
				count: 1
			)
		)

		AchievementListItem(
			achievement: .init(
				title: EarnableAchievements.TenYears.title,
				firstEarnedAt: Date(),
				count: 3
			)
		)

		AchievementListItem(
			achievement: .init(
				title: EarnableAchievements.TenYears.title,
				firstEarnedAt: nil,
				count: 0
			)
		)
	}
}
