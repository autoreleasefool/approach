import ModelsLibrary

extension EarnableAchievements {
	public struct Iconista: EarnableAchievement {
		public static var title: String { "Iconista" }

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
		public static var title: String { "AppIconsViewed" }

		public init() {}
	}
}
