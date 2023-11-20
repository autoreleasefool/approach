import Dependencies
import Foundation

public struct AppInfoService: Sendable {
	public var recordNewSession: @Sendable () async -> Void
	public var numberOfSessions: @Sendable () -> Int
	public var recordInstallDate: @Sendable () async -> Void
	public var installDate: @Sendable () -> Date
	public var appVersion: @Sendable () -> String

	public init(
		recordNewSession: @escaping @Sendable () async -> Void,
		numberOfSessions: @escaping @Sendable () -> Int,
		recordInstallDate: @escaping @Sendable () async -> Void,
		installDate: @escaping @Sendable () -> Date,
		appVersion: @escaping @Sendable () -> String
	) {
		self.recordNewSession = recordNewSession
		self.numberOfSessions = numberOfSessions
		self.recordInstallDate = recordInstallDate
		self.installDate = installDate
		self.appVersion = appVersion
	}
}

extension AppInfoService: TestDependencyKey {
	public static var testValue = Self(
		recordNewSession: { unimplemented("\(Self.self).recordNewSession") },
		numberOfSessions: { unimplemented("\(Self.self).numberOfSessions") },
		recordInstallDate: { unimplemented("\(Self.self).recordInstallDate") },
		installDate: { unimplemented("\(Self.self).installDate") },
		appVersion: { unimplemented("\(Self.self).appVersion") }
	)
}

extension DependencyValues {
	public var appInfo: AppInfoService {
		get { self[AppInfoService.self] }
		set { self[AppInfoService.self] = newValue }
	}
}
