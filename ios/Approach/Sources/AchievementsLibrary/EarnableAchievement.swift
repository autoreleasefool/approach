import AssetsLibrary
import Foundation
import SwiftUI

public protocol EarnableAchievement: Sendable {
	static var title: String { get }
	static var displayName: String { get }
	static var icon: Image { get }

	static var isEnabled: Bool { get }
	static var showToastOnEarn: Bool { get }
	static var isVisibleBeforeEarned: Bool { get }

	static var events: [any ConsumableAchievementEvent.Type] { get }
	static var eventsByTitle: [String: any ConsumableAchievementEvent.Type] { get }
	static func consume(from: [any ConsumableAchievementEvent]) -> (consumed: Set<UUID>, earned: [Self])

	init()
}

extension EarnableAchievement {
	public static var title: String { String(describing: self) }
}

public protocol ConsumableAchievementEvent: Sendable {
	static var title: String { get }
	var id: UUID { get }

	init(id: UUID)
}

extension ConsumableAchievementEvent {
	public static var title: String { String(describing: self) }
}

// MARK: Earnable

public enum EarnableAchievements {}

extension EarnableAchievements {
	public static let allCases: [EarnableAchievement.Type] = [
		TenYears.self,
		Iconista.self,
	]

	public static let allCasesByTitle: [String: EarnableAchievement.Type] = Dictionary(
		uniqueKeysWithValues: allCases.map { ($0.title, $0) }
	)
}
