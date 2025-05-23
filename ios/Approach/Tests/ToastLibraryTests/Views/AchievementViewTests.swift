import Foundation
import SnapshotTesting
import Testing
import TestUtilitiesLibrary
@testable import ToastLibrary

@Suite("AchievementView Tests", .tags(.library), .snapshots(record: .failed))
struct AchievementViewTests {

	@Test("Achievement view", .tags(.snapshot))
	@MainActor
	func snapshotAchievementView() {
		let achievementView = AchievementView(title: "Achievement") { }
			.frame(width: 428)

		assertSnapshot(of: achievementView, as: .image)
	}
}
