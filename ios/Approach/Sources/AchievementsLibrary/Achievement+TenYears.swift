import ModelsLibrary

extension EarnableAchievements {
	public struct TenYears: EarnableAchievement {
		public static var title: String { "Ten Years" }

		public static var isEnabled: Bool { true }
		public static var showToastOnEarn: Bool { false }
		public static var isVisibleBeforeEarned: Bool { false }

		public static var events: [any ConsumableAchievementEvent.Type] {
			[Events.TenYearsBadgeClaimed.self]
		}

		public static func consume(from: inout [any ConsumableAchievementEvent]) -> [TenYears] {
			let consumed = from.filter { type(of: $0).title == Events.TenYearsBadgeClaimed.title }
			guard !consumed.isEmpty else { return [] }
			from.removeAll(where: { type(of: $0).title == Events.TenYearsBadgeClaimed.title })
			return consumed.map { _ in TenYears() }
		}

		public init() {}
	}
}

// MARK: Events

extension EarnableAchievements.TenYears {
	public enum Events {}
}

extension EarnableAchievements.TenYears.Events {
	public struct TenYearsBadgeClaimed: ConsumableAchievementEvent {
		public init() {}
	}
}
