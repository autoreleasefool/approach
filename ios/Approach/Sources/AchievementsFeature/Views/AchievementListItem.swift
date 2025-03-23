import AchievementsLibrary
import AssetsLibrary
import ModelsLibrary
import Sticker
import SwiftUI

struct AchievementListItem: View {
	let achievement: Achievement.List
	let isMoveable: Bool

	@State private var valueTranslation: CGSize = .zero
	@State private var isDragging = false

	var body: some View {
		VStack(spacing: .smallSpacing) {
			icon

			Text(achievement.title)
				.font(.headline)
		}
		.opacity(achievement.firstEarnedAt == nil ? 0.5 : 1)
		.fixedSize(horizontal: false, vertical: true)
	}

	private var icon: some View {
		ZStack {
			Asset.Media.Achievements.tenYears.swiftUIImage
				.resizable()
				.scaledToFit()
				.frame(maxWidth: .infinity, maxHeight: .infinity)
		}
		.rotation3DEffect(
			.degrees(isDragging ? 20 : 0),
			axis: (x: -valueTranslation.height, y: valueTranslation.width, z: 0)
		)
		.gesture(
			DragGesture()
				.onChanged { value in
					withAnimation {
						valueTranslation = value.translation
						isDragging = true
					}
				}
				.onEnded { _ in
					withAnimation {
						valueTranslation = .zero
						isDragging = false
					}
				},
			isEnabled: isMoveable
		)
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
			),
			isMoveable: true
		)
//		.frame(width: 200)

		AchievementListItem(
			achievement: .init(
				title: EarnableAchievements.TenYears.title,
				firstEarnedAt: Date(),
				count: 3
			),
			isMoveable: true
		)
//		.frame(: 200)

		AchievementListItem(
			achievement: .init(
				title: EarnableAchievements.TenYears.title,
				firstEarnedAt: nil,
				count: 0
			),
			isMoveable: true
		)
	}
}
