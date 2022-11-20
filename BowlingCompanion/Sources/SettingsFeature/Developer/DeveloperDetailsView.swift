import ConstantsLibrary
import SwiftUI

struct DeveloperDetailsView: View {
	var body: some View {
		List {
			Section("Contact") {
				Text(DeveloperConstants.name)
				Link(DeveloperConstants.twitterHandle, destination: DeveloperConstants.twitterUrl)
				Link(DeveloperConstants.mastodonHandle, destination: DeveloperConstants.mastodonUrl)
			}

			Section {
				Link("Personal Website", destination: DeveloperConstants.website)
				Link("Personal Blog", destination: DeveloperConstants.blog)
			}
		}
		.navigationTitle("Developer")
	}
}
