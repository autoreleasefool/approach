import AssetsLibrary
import AutomaticBackupsFeature
import DateTimeLibrary
import StringsLibrary
import SwiftUI

struct DataSection: View {
	let isBackupsButtonVisible: Bool
	let daysSinceLastBackup: DaysSince
	let daysSinceLastExport: DaysSince
	let onTapImportButton: () -> Void
	let onTapExportButton: () -> Void
	let onTapBackupsButton: () -> Void

	var body: some View {
		Section(Strings.Settings.Data.title) {
			Button(Strings.Settings.Data.import, action: onTapImportButton)
				.buttonStyle(.navigation)

			Button(action: onTapExportButton) {
				HStack {
					let (warningSymbol, warningSymbolColor) = daysSinceLastExport.warningSymbol()
					Image(systemSymbol: warningSymbol)
						.foregroundStyle(warningSymbolColor)

					Text(Strings.Settings.Data.export)
				}
			}
			.buttonStyle(.navigation)

			if isBackupsButtonVisible {
				Button(action: onTapBackupsButton) {
					HStack {
						let (warningSymbol, warningSymbolColor) = daysSinceLastBackup.warningSymbol()
						Image(systemSymbol: warningSymbol)
							.foregroundStyle(warningSymbolColor)

						Text(Strings.Settings.Data.automaticBackups)
					}
				}
				.buttonStyle(.navigation)
			}
		}
	}
}

#Preview {
	List {
		DataSection(
			isBackupsButtonVisible: true,
			daysSinceLastBackup: .days(15),
			daysSinceLastExport: .never,
			onTapImportButton: {},
			onTapExportButton: {},
			onTapBackupsButton: {}
		)
	}
}
