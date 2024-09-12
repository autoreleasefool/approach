import StringsLibrary
import SwiftUI

struct DeveloperOptionsSection: View {
	let onTapFeatureFlagsButton: () -> Void
	let onTapPopulateDatabaseButton: () -> Void

	var body: some View {
		Section {
			Button(action: onTapFeatureFlagsButton) {
				Text(Strings.Settings.FeatureFlags.title)
			}
			.buttonStyle(.navigation)

			Button(action: onTapPopulateDatabaseButton) {
				Text(Strings.Settings.DeveloperOptions.populateDatabase)
			}
		}
	}
}
