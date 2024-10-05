import ModelsLibrary

extension EarnableBadges {
	public struct Iconista: EarnableBadge {
		public static var title: String { "Iconista" }

		public static func consume(from: inout [any ConsumableBadgeEvent]) -> [Iconista] {
			let consumed = from.filter { type(of: $0).title == Events.AppIconsViewed.title }
			guard !consumed.isEmpty else { return [] }
			from.removeAll(where: { type(of: $0).title == Events.AppIconsViewed.title })
			return consumed.map { _ in Iconista() }
		}

		public init() {}
	}
}

// MARK: Events

extension EarnableBadges.Iconista {
	public enum Events {}
}

extension EarnableBadges.Iconista.Events {
	public struct AppIconsViewed: ConsumableBadgeEvent {
		public static var title: String { "AppIconsViewed" }

		public init() {}
	}
}
