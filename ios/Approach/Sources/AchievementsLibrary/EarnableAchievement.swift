import ModelsLibrary

public protocol EarnableAchievement: Sendable {
	static var title: String { get }
	static func consume(from: inout [ConsumableAchievementEvent]) -> [Self]

	init()
}

public protocol ConsumableAchievementEvent: Sendable {
	static var title: String { get }
}

// Earnable

public enum EarnableAchievements {}

extension EarnableAchievements {
	public static let allCases: [EarnableAchievement.Type] = [
		Iconista.self,
	]
}
