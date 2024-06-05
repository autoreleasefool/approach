import Algorithms
import Dependencies
import Foundation
import RecentlyUsedServiceInterface
import StatisticsLibrary
import UserDefaultsPackageServiceInterface

extension Notification.Name {
	enum RecentlyUsed {
		static let didChange = Notification.Name("RecentlyUsed.didChange")
	}
}

extension RecentlyUsedService: DependencyKey {
	public static var maximumEntries: Int { 20 }

	public static var liveValue: Self {
		// FIXME: Replace with a @Dependency
		let decoder = JSONDecoder()

		@Sendable func key(forCategory category: Resource) -> String {
			"RecentlyUsed.\(category.rawValue)"
		}

		@Sendable func entries(forCategory category: Resource) -> [Entry] {
			@Dependency(\.userDefaults) var userDefaults

			let categoryKey = key(forCategory: category)
			let string = userDefaults.string(forKey: categoryKey) ?? "[]"
			guard let data = string.data(using: .utf8),
						let recentlyUsed = try? decoder.decode([Entry].self, from: data) else {
				return []
			}

			return recentlyUsed
		}

		return Self(
			didRecentlyUseResource: { category, uuid in
				@Dependency(\.date) var date
				@Dependency(\.userDefaults) var userDefaults

				let categoryKey = key(forCategory: category)
				var recentlyUsed = entries(forCategory: category)
				let entry = Entry(id: uuid, lastUsedAt: date())

				recentlyUsed.removeAll { $0.id == entry.id }
				recentlyUsed = Array(recentlyUsed.prefix(maximumEntries - 1))
				recentlyUsed.insert(entry, at: 0)

				@Dependency(JSONEncoderService.self) var encoder
				guard let recentlyUsedData = try? encoder.encode(recentlyUsed),
							let recentlyUsedString = String(data: recentlyUsedData, encoding: .utf8) else {
					return
				}

				userDefaults.setString(forKey: categoryKey, to: recentlyUsedString)
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
			observeRecentlyUsedIds: { category in
				.init { continuation in
					let categoryKey = key(forCategory: category)

					continuation.yield(
						(entries(forCategory: category).map(\.id))
					)

					let cancellable = NotificationCenter.default
						.publisher(for: .RecentlyUsed.didChange)
						.filter { ($0.object as? String) == categoryKey }
						.sink { _ in
							continuation.yield(
								(entries(forCategory: category).map(\.id))
							)
						}

					continuation.onTermination = { _ in cancellable.cancel() }
				}
			},
			resetRecentlyUsed: { category in
				@Dependency(\.userDefaults) var userDefaults

				let categoryKey = key(forCategory: category)
				userDefaults.remove(key: categoryKey)
				NotificationCenter.default.post(name: .RecentlyUsed.didChange, object: categoryKey)
			}
		)
	}
}

extension RecentlyUsedTrackableFilterService: DependencyKey {
	private static var userDefaultsKey = "RecentlyUsedTrackableFilter.TrackableFilter"

	public static var liveValue: Self {
		// FIXME: Replace with a @Dependency
		let decoder = JSONDecoder()

		@Sendable func entries() -> [TrackableFilter] {
			@Dependency(\.userDefaults) var userDefaults

			let string = userDefaults.string(forKey: Self.userDefaultsKey) ?? "[]"
			guard let data = string.data(using: .utf8),
						let recentlyUsed = try? decoder.decode([TrackableFilter].self, from: data) else {
				return []
			}

			return recentlyUsed
		}

		return Self(
			didRecentlyUse: { filter in
				@Dependency(\.userDefaults) var userDefaults
				let recentlyUsed = ([filter] + entries())
					.prefix(RecentlyUsedService.maximumEntries - 1)
					.uniqued()

				@Dependency(JSONEncoderService.self) var encoder
				guard let recentlyUsedData = try? encoder.encode(Array(recentlyUsed)),
							let recentlyUsedString = String(data: recentlyUsedData, encoding: .utf8) else {
					return
				}

				userDefaults.setString(forKey: Self.userDefaultsKey, to: recentlyUsedString)
				NotificationCenter.default.post(name: .RecentlyUsed.didChange, object: Self.userDefaultsKey)
			},
			observeRecentlyUsed: {
				.init { continuation in
					continuation.yield(entries())

					let cancellable = NotificationCenter.default
						.publisher(for: .RecentlyUsed.didChange)
						.filter { ($0.object as? String) == Self.userDefaultsKey }
						.sink { _ in
							continuation.yield(entries())
						}

					continuation.onTermination = { _ in cancellable.cancel() }
				}
			}
		)
	}
}
