import ModelsLibrary

public protocol EarnableAchievement: Sendable {
	static var title: String { get }

	static var isEnabled: Bool { get }
	static var showToastOnEarn: Bool { get }
	static var isVisibleBeforeEarned: Bool { get }

	static var events: [ConsumableAchievementEvent.Type] { get }
	static func consume(from: inout [ConsumableAchievementEvent]) -> [Self]

	init()
}

public protocol ConsumableAchievementEvent: Sendable {
	static var title: String { get }
	var title: String { get }
}

extension ConsumableAchievementEvent {
	public var title: String { Self.title }
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
}
