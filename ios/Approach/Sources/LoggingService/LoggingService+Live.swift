import CocoaLumberjack
import CocoaLumberjackSwift
import Dependencies
import FileManagerServiceInterface
import LoggingServiceInterface

extension LoggingService: DependencyKey {
	public static var liveValue: Self = {
		let fileLogger = DDFileLogger()
		let logFileManager = fileLogger.logFileManager

		return Self(
			initialize: {
				DDLog.add(DDOSLogger.sharedInstance)
				DDLog.add(fileLogger)
			},
			log: { message, level in
				switch level {
				case .error:
					DDLogError(message)
				case .warning:
					DDLogWarn(message)
				case .info:
					DDLogInfo(message)
				case .debug:
					DDLogDebug(message)
				case .verbose:
					DDLogDebug(message)
				}
			},
			fetchLogData: {
				@Dependency(\.fileManager) var fileManager
				let urls = logFileManager.unsortedLogFileNames.compactMap(URL.init)
				return try fileManager.zipContents(ofUrls: urls, to: "approach_logs.zip")
			}
		)
	}()
}
