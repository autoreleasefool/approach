import Dependencies
import Foundation

public struct ExportService: Sendable {
	public var exportDatabase: @Sendable () -> AsyncThrowingStream<Event, Error>
	public var cleanUp: @Sendable () -> Void

	public init(
		exportDatabase: @escaping @Sendable () -> AsyncThrowingStream<Event, Error>,
		cleanUp: @escaping @Sendable () -> Void
	) {
		self.exportDatabase = exportDatabase
		self.cleanUp = cleanUp
	}
}

extension ExportService {
	public enum Event: Equatable {
		case progress(stepsComplete: Int, totalSteps: Int)
		case response(URL)
	}
}

extension ExportService: TestDependencyKey {
	public static var testValue = Self(
		exportDatabase: { unimplemented("\(Self.self).exportDatabase") },
		cleanUp: { unimplemented("\(Self.self).cleanUp") }
	)
}
