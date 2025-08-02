import ConstantsLibrary
import StringsLibrary
import SwiftUI

struct DevelopmentView: View {
	let onTapViewSourceButton: () -> Void

	var body: some View {
		List {
			Section(Strings.Settings.Developer.contact) {
				Text(DeveloperConstants.name)
				Link(DeveloperConstants.mastodonHandle, destination: DeveloperConstants.mastodonUrl)
			}

			Section(Strings.Settings.Developer.learnMore) {
				Link(destination: DeveloperConstants.website) {
					LabeledContent(Strings.Settings.Developer.websiteTitle, value: Strings.Settings.Developer.website)
				}
				Link(destination: DeveloperConstants.blog) {
					LabeledContent(Strings.Settings.Developer.blogTitle, value: Strings.Settings.Developer.blog)
				}
			}

			Section {
				Button(Strings.Settings.Development.viewSource, action: onTapViewSourceButton)
			} header: {
				Text(Strings.Settings.Development.contributing)
			} footer: {
				Text(Strings.Settings.Development.help(Strings.App.name))
			}
		}
		.navigationTitle(Strings.Settings.Development.title)
	}
}
