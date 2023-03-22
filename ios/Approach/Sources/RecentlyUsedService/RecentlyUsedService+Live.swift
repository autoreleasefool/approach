import Dependencies
import Foundation
import PreferenceServiceInterface
import RecentlyUsedServiceInterface

extension Notification.Name {
	enum RecentlyUsed {
		static let didChange = Notification.Name("RecentlyUsed.didChange")
	}
}

extension RecentlyUsedService: DependencyKey {
	public static var liveValue: Self = {
		let encoder = JSONEncoder()
		let decoder = JSONDecoder()

		@Sendable func key(forCategory category: Resource) -> String {
			"RecentlyUsed.\(category.rawValue)"
		}

		@Sendable func entries(forCategory category: Resource) -> [Entry] {
			@Dependency(\.preferenceService) var preferenceService: PreferenceService

			let categoryKey = key(forCategory: category)
			let string = preferenceService.getString(categoryKey) ?? "[]"
			guard let data = string.data(using: .utf8),
						let recentlyUsed = try? decoder.decode([Entry].self, from: data) else {
				return []
			}

			return recentlyUsed
		}

		return Self(
			didRecentlyUseResource: { category, uuid in
				@Dependency(\.preferenceService) var preferenceService: PreferenceService
				@Dependency(\.date) var date: DateGenerator

				let categoryKey = key(forCategory: category)
				var recentlyUsed = entries(forCategory: category)
				let entry = Entry(id: uuid, lastUsedAt: date())

				if let index = recentlyUsed.firstIndex(where: { $0.id == entry.id }) {
					recentlyUsed.remove(at: index)
				}

				// TODO: eject outdated ids / at limit
				recentlyUsed.insert(entry, at: 0)

				guard let recentlyUsedData = try? encoder.encode(recentlyUsed),
							let recentlyUsedString = String(data: recentlyUsedData, encoding: .utf8) else {
					return
				}

				preferenceService.setString(categoryKey, recentlyUsedString)
				NotificationCenter.default.post(name: .RecentlyUsed.didChange, object: categoryKey)
			},
			getRecentlyUsed: { category in
				entries(forCategory: category)
			},
			observeRecentlyUsed: { category in
				.init { continuation in
					let categoryKey = key(forCategory: category)

					continuation.yield(
						(entries(forCategory: category))
					)

					let cancellable = NotificationCenter.default
						.publisher(for: .RecentlyUsed.didChange)
						.filter { ($0.object as? String) == categoryKey }
						.sink { _ in
							continuation.yield(
								(entries(forCategory: category))
							)
						}

					continuation.onTermination = { _ in cancellable.cancel() }
				}
			},
			resetRecentlyUsed: { category in
				@Dependency(\.preferenceService) var preferenceService: PreferenceService

				let categoryKey = key(forCategory: category)
				preferenceService.removeKey(categoryKey)
				NotificationCenter.default.post(name: .RecentlyUsed.didChange, object: categoryKey)
			}
		)
	}()
}
