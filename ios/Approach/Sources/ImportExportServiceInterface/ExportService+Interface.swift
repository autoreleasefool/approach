import Dependencies
import Foundation

public struct ExportService: Sendable {
	public var exportDatabase: @Sendable () -> AsyncThrowingStream<Event, Error>

	public init(
		exportDatabase: @escaping @Sendable () -> AsyncThrowingStream<Event, Error>
	) {
		self.exportDatabase = exportDatabase
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
		exportDatabase: { unimplemented("\(Self.self).exportDatabase") }
	)
}

extension DependencyValues {
	public var export: ExportService {
		get { self[ExportService.self] }
		set { self[ExportService.self] = newValue }
	}
}
