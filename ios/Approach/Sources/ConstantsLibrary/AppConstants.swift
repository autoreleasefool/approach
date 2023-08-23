import Foundation
import StringsLibrary

public enum AppConstants {
	public static let appName = Strings.App.name
	public static let openSourceRepositoryUrl = URL(string: Strings.Settings.Developer.openSourceRepositoryUrl)!

	public static let appBuild: String = {
		(try? Configuration.value(for: "CFBundleVersion")) ?? "⚠️"
	}()

	public static let appVersionLong: String = {
		(try? Configuration.value(for: "CFBundleShortVersionString")) ?? "⚠️"
	}()

	public static let appVersionReadable: String = {
		Strings.Settings.AppInfo.appVersion(appVersionLong, appBuild)
	}()
}

extension AppConstants {
	public enum ApiKey {
		public static let disable = "DISABLE"

		public static let telemetryDeck: String = {
			(try? Configuration.value(for: "TELEMETRY_DECK_API_KEY")) ?? AppConstants.ApiKey.disable
		}()
	}
}
