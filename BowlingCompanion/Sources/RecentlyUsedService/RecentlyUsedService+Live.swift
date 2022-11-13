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
	public static let liveValue: Self = {
		@Sendable func key(forCategory category: RecentlyUsedResource) -> String {
			"RecentlyUsed.\(category.rawValue)"
		}

		return Self(
			didRecentlyUseResource: { category, uuid in
				@Dependency(\.preferenceService) var preferenceService: PreferenceService

				let categoryKey = key(forCategory: category)
				let uuidString = uuid.uuidString
				var recentlyUsed = preferenceService.getStringArray(categoryKey) ?? []

				if let index = recentlyUsed.firstIndex(of: uuidString) {
					recentlyUsed.remove(at: index)
				}

				recentlyUsed.insert(uuidString, at: 0)
				preferenceService.setStringArray(categoryKey, recentlyUsed)
				NotificationCenter.default.post(name: .RecentlyUsed.didChange, object: categoryKey)
			},
			observeRecentlyUsed: { category in
				.init { continuation in
					@Dependency(\.preferenceService) var preferenceService: PreferenceService
					let categoryKey = key(forCategory: category)

					continuation.yield(
						(preferenceService.getStringArray(categoryKey) ?? [])
							.compactMap { UUID(uuidString: $0) }
					)

					let cancellable = NotificationCenter.default
						.publisher(for: .RecentlyUsed.didChange)
						.filter { ($0.object as? String) == categoryKey }
						.sink { _ in
							continuation.yield(
								(preferenceService.getStringArray(categoryKey) ?? [])
									.compactMap { UUID(uuidString: $0) }
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
