import StringsLibrary

extension EarnableAchievements {
	public struct Iconista: EarnableAchievement, Equatable {
		public static var title: String { Strings.Achievements.Earnable.Iconista.title }

		public static var isEnabled: Bool { false }
		public static var showToastOnEarn: Bool { true }
		public static var isVisibleBeforeEarned: Bool { true }

		public static var events: [any ConsumableAchievementEvent.Type] {
			[Events.AppIconsViewed.self]
		}

		public static func consume(from: inout [any ConsumableAchievementEvent]) -> [Iconista] {
			let consumed = from.filter { type(of: $0).title == Events.AppIconsViewed.title }
			guard !consumed.isEmpty else { return [] }
			from.removeAll(where: { type(of: $0).title == Events.AppIconsViewed.title })
			return consumed.map { _ in Iconista() }
		}

		public init() {}
	}
}

// MARK: Events

extension EarnableAchievements.Iconista {
	public enum Events {}
}

extension EarnableAchievements.Iconista.Events {
	public struct AppIconsViewed: ConsumableAchievementEvent {
		public init() {}
	}
}
