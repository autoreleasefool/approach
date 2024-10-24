import StringsLibrary
import SwiftUI
import ViewsLibrary

struct ImportOverwriteWarningBanner: View {
	let progress: Import.Progress

	var body: some View {
		if case .notStarted = progress {
			Banner(
				.titleAndMessage(
					Strings.Import.Instructions.overwrite,
					Strings.Import.Instructions.notRecover
				),
				style: .warning
			)
		}
	}
}
