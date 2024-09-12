import StringsLibrary
import SwiftUI

struct DataSection: View {
	let isImportButtonVisible: Bool
	let onTapImportButton: () -> Void
	let onTapExportButton: () -> Void

	var body: some View {
		Section(Strings.Settings.Data.title) {
			if isImportButtonVisible {
				Button(Strings.Settings.Data.import, action: onTapImportButton)
					.buttonStyle(.navigation)
			}

			Button(Strings.Settings.Data.export, action: onTapExportButton)
				.buttonStyle(.navigation)
		}
	}
}
