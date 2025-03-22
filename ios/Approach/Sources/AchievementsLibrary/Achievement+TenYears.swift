import Foundation
import StringsLibrary

extension EarnableAchievements {
	public struct TenYears: EarnableAchievement, Equatable {
		public static var title: String { Strings.Achievements.Earnable.TenYear.title }

		public static var isEnabled: Bool { true }
		public static var showToastOnEarn: Bool { false }
		public static var isVisibleBeforeEarned: Bool { false }

		public static let events: [ConsumableAchievementEvent.Type] = [
			Events.TenYearsBadgeClaimed.self,
		]

		public static let eventsByTitle: [String: ConsumableAchievementEvent.Type] = Dictionary(
			uniqueKeysWithValues: events.map { ($0.title, $0) }
		)

		public static func consume(
			from: [any ConsumableAchievementEvent]
		) -> (consumed: Set<UUID>, earned: [TenYears]) {
			let consumed = from.compactMap { event in
				type(of: event) == Events.TenYearsBadgeClaimed.self ? event.id : nil
			}

			return (Set(consumed), consumed.map { _ in .init() })
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
		public let id: UUID

		public init(id: UUID) {
			self.id = id
		}
	}
}
