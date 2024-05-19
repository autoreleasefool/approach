import Foundation
import StringsLibrary

public enum AppConstants {
	public static let appName = Strings.App.name
	public static let openSourceRepositoryUrl = URL(string: Strings.Settings.Developer.openSourceRepositoryUrl)!
	public static let privacyPolicyUrl = URL(string: Strings.Settings.Analytics.PrivacyPolicy.url)!
}
