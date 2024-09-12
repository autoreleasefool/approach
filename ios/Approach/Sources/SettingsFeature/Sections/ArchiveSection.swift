import StringsLibrary
import SwiftUI

struct ArchiveSection: View {
	let onTapArchiveButton: () -> Void

	var body: some View {
		Section {
			Button(action: onTapArchiveButton) {
				Text(Strings.Settings.Archive.title)
			}
			.buttonStyle(.navigation)
		} footer: {
			Text(Strings.Settings.Archive.footer)
		}
	}
}
