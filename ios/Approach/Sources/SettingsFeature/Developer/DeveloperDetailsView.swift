import ConstantsLibrary
import StringsLibrary
import SwiftUI

struct DeveloperDetailsView: View {
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
		}
		.navigationTitle(Strings.Settings.Developer.title)
	}
}

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

			Section(Strings.Settings.Development.contributing) {
				Button(Strings.Settings.Help.viewSource, action: onTapViewSourceButton)
			}
		}
		.navigationTitle(Strings.Settings.Development.title)
	}
}
