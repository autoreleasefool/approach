import Dependencies
import Foundation

public enum LogLevel {
	case error
	case warning
	case info
	case debug
	case verbose
}

public struct LoggingService: Sendable {
	public var initialize: @Sendable () -> Void
	public var log: @Sendable (Any, LogLevel) -> Void
	public var fetchLogData: @Sendable () async throws -> URL

	public init(
		initialize: @escaping @Sendable () -> Void,
		log: @escaping @Sendable (Any, LogLevel) -> Void,
		fetchLogData: @escaping @Sendable () async throws -> URL
	) {
		self.initialize = initialize
		self.log = log
		self.fetchLogData = fetchLogData
	}

	public func error(_ message: Any) {
		self.log(message, .error)
	}

	public func warn(_ message: Any) {
		self.log(message, .warning)
	}

	public func info(_ message: Any) {
		self.log(message, .info)
	}

	public func debug(_ message: Any) {
		self.log(message, .debug)
	}

	public func verbose(_ message: Any) {
		self.log(message, .verbose)
	}
}

extension LoggingService: TestDependencyKey {
	public static var testValue = Self(
		initialize: { unimplemented("\(Self.self).initialize") },
		log: { _, _ in unimplemented("\(Self.self).log") },
		fetchLogData: { unimplemented("\(Self.self).initialize") }
	)
}

extension DependencyValues {
	public var logging: LoggingService {
		get { self[LoggingService.self] }
		set { self[LoggingService.self] = newValue }
	}
}
