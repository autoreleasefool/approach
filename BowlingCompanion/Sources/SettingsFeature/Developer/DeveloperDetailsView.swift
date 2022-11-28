import ConstantsLibrary
import StringsLibrary
import SwiftUI

struct DeveloperDetailsView: View {
	var body: some View {
		List {
			Section(Strings.Settings.Developer.contact) {
				Text(DeveloperConstants.name)
				Link(DeveloperConstants.twitterHandle, destination: DeveloperConstants.twitterUrl)
				Link(DeveloperConstants.mastodonHandle, destination: DeveloperConstants.mastodonUrl)
			}

			Section {
				Link(Strings.Settings.Developer.websiteTitle, destination: DeveloperConstants.website)
				Link(Strings.Settings.Developer.blogTitle, destination: DeveloperConstants.blog)
			}
		}
		.navigationTitle(Strings.Settings.Developer.title)
	}
}
