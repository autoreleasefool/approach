import StringsLibrary
import SwiftUI

struct AppInfoSection: View {
	let appVersion: String
	let onTapVersionNumber: () -> Void

	var body: some View {
		Section {
			Button(action: onTapVersionNumber) {
				LabeledContent(Strings.Settings.AppInfo.version, value: appVersion)
					.contentShape(Rectangle())
			}
			.buttonStyle(.plain)
		} header: {
			Text(Strings.Settings.AppInfo.title)
		} footer: {
			Text(Strings.Settings.AppInfo.copyright)
				.font(.caption)
		}
	}
}
