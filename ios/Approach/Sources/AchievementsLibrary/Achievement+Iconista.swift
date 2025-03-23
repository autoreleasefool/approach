import AssetsLibrary
import Foundation
import StringsLibrary
import SwiftUI

extension EarnableAchievements {
	public struct Iconista: EarnableAchievement, Equatable {
		public static var title: String { Strings.Achievements.Earnable.Iconista.title }
		public static var icon: Image { Asset.Media.Achievements.approach.swiftUIImage }

		public static var isEnabled: Bool { false }
		public static var showToastOnEarn: Bool { true }
		public static var isVisibleBeforeEarned: Bool { true }

		public static let events: [ConsumableAchievementEvent.Type] = [
			Events.AppIconsViewed.self,
		]

		public static let eventsByTitle: [String: any ConsumableAchievementEvent.Type] = Dictionary(
			uniqueKeysWithValues: events.map { ($0.title, $0) }
		)

		public static func consume(
			from: [any ConsumableAchievementEvent]
		) -> (consumed: Set<UUID>, earned: [Iconista]) {
			let consumed = from.compactMap { event in
				type(of: event) == Events.AppIconsViewed.self ? event.id : nil
			}

			return (Set(consumed), consumed.map { _ in .init() })
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
		public let id: UUID

		public init(id: UUID) {
			self.id = id
		}
	}
}
