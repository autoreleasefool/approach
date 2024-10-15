import Dependencies
import Foundation

public struct ExportService: Sendable {
	public var lastExportDate: @Sendable () -> Date?
	public var exportDatabase: @Sendable () -> AsyncThrowingStream<Event, Error>
	public var cleanUp: @Sendable () -> Void

	public init(
		lastExportDate: @escaping @Sendable () -> Date?,
		exportDatabase: @escaping @Sendable () -> AsyncThrowingStream<Event, Error>,
		cleanUp: @escaping @Sendable () -> Void
	) {
		self.lastExportDate = lastExportDate
		self.exportDatabase = exportDatabase
		self.cleanUp = cleanUp
	}
}

extension ExportService {
	public enum Event: Equatable, Sendable {
		case progress(stepsComplete: Int, totalSteps: Int)
		case response(URL)
	}
}

extension ExportService: TestDependencyKey {
	public static var testValue: Self {
		Self(
			lastExportDate: { unimplemented("\(Self.self).lastExportDate", placeholder: nil) },
			exportDatabase: { unimplemented("\(Self.self).exportDatabase", placeholder: .never) },
			cleanUp: { unimplemented("\(Self.self).cleanUp") }
		)
	}
}
