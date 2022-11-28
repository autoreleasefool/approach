import Foundation
import StringsLibrary

public enum DeveloperConstants {
	public static let name = Strings.Settings.Developer.name
	public static let twitterHandle = Strings.Settings.Developer.twitterHandle
	public static let twitterUrl = URL(string: Strings.Settings.Developer.twitterUrl)!
	public static let mastodonHandle = Strings.Settings.Developer.mastodonHandle
	public static let mastodonUrl = URL(string: Strings.Settings.Developer.mastodonUrl)!
	public static let website = URL(string: Strings.Settings.Developer.website)!
	public static let blog = URL(string: Strings.Settings.Developer.blog)!
}
