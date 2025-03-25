import Dependencies
import LoggingServiceInterface

extension LoggingService: DependencyKey {
	public static var liveValue: Self {
		return LoggingService(
			initialize: {},
			log: { _, _ in },
			fetchLogData: { throw ServiceError.fileNotFound }
		)
	}
}

extension LoggingService {
	enum ServiceError: Error {
		case fileNotFound
	}
}
