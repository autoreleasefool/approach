import AnalyticsPackageServiceInterface
import AnalyticsServiceInterface
import ComposableArchitecture
import DatabaseServiceInterface
import ErrorReportingClientPackageLibrary
import FeatureFlagsLibrary
import FeatureFlagsPackageServiceInterface
import GRDB
import TestDatabaseUtilitiesLibrary
import UserDefaultsPackageServiceInterface

// MARK: - Analytics

extension AnalyticsService {
	public static var mock: Self {
		Self(
			initialize: {
				print("[AnalyticsService] initialize")
			},
			trackEvent: { event in
				print("[AnalyticsService] Tracking \(event.name)")
			},
			setGlobalProperty: { key, value in
				print("[AnalyticsService] Setting property \(key) to \(String(describing: value))")
			},
			getOptInStatus: { .optedIn },
			setOptInStatus: { _ in .optedIn }
		)
	}
}

// MARK: Breadcrumbs

extension BreadcrumbService {
	public static var mock: Self {
		Self(
			drop: { breadcrumb in
				print("[Breadcrumb] \(breadcrumb.category) - \(breadcrumb.message)")
			}
		)
	}
}

// MARK: Database

extension DatabaseService {
	public static var defaults: Self {
		let db: any DatabaseWriter
		do {
			db = try initializeApproachDatabase(
				withLocations: .default,
				withAlleys: .default,
				withLanes: .default,
				withBowlers: .default,
				withGear: .default,
				withLeagues: .default,
				withSeries: .default,
				withGames: .default,
				withFrames: .default
			)
		} catch {
			fatalError("Could not initialize database: \(error)")
		}

		return DatabaseService(
			initialize: {},
			dbUrl: { fatalError() },
			close: {},
			reader: { db },
			writer: { db }
		)
	}
}

// MARK: Error Reporting

extension ErrorReportingClient {
	public static var mock: Self {
		Self(
			captureError: { error in
				print("[ErrorReporting] Reported \(error)")
			},
			captureMessage: { message in
				print("[ErrorReporting] \(message)")
			}
		)
	}
}

extension FeatureFlagsService {
	public static func enabling(_ enabledFlags: Set<FeatureFlag>) -> Self {
		Self(
			initialize: { _ in },
			isEnabled: { flag in enabledFlags.contains(flag) },
			allEnabled: { flags in flags.allSatisfy { enabledFlags.contains($0) } },
			observe: { _ in .finished },
			observeAll: { _ in .finished },
			setEnabled: { _, _ in },
			resetOverrides: {}
		)
	}

	public static var allEnabled: Self {
		.enabling(Set(FeatureFlag.allFlags))
	}

	public static var noneEnabled: Self {
		.enabling([])
	}
}

// MARK: User Defaults

extension UserDefaultsService {
	public static var mock: Self {
		let store = LockIsolated<[String: Sendable]>([:])

		return Self(
			bool: { key in store.value[key] as? Bool },
			setBool: { key, value in store.withValue { $0[key] = value } },
			double: { key in store.value[key] as? Double },
			setDouble: { key, value in store.withValue { $0[key] = value } },
			float: { key in store.value[key] as? Float },
			setFloat: { key, value in store.withValue { $0[key] = value } },
			int: { key in store.value[key] as? Int },
			setInt: { key, value in store.withValue { $0[key] = value } },
			string: { key in store.value[key] as? String },
			setString: { key, value in store.withValue { $0[key] = value } },
			stringArray: { key in store.value[key] as? [String] },
			setStringArray: { key, value in store.withValue { $0[key] = value } },
			contains: { key in store.value[key] != nil },
			remove: { key in store.withValue { $0[key] = nil } },
			observe: { _ in .finished }
		)
	}
}
