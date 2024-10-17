import AssetsLibrary
import DateTimeLibrary
import ImportExportServiceInterface
import StringsLibrary
import SwiftUI
import ViewsLibrary

struct BackupFileView: View {
	let file: BackupFile
	let isLatest: Bool

	var body: some View {
		HStack(spacing: .standardSpacing) {
			Image(systemSymbol: .checkmarkIcloud)
				.resizable()
				.scaledToFit()
				.frame(maxWidth: .smallIcon, maxHeight: .smallIcon)
				.foregroundStyle(Asset.Colors.Success.default.swiftUIColor)

			VStack(alignment: .leading, spacing: .tinySpacing) {
				LabeledContent(file.dateCreated.longFormat, value: Strings.Backups.List.fileSize(file.fileSizeMb))

				if isLatest {
					Chip(title: Strings.Backups.List.latest, size: .compact, style: .primary)
				}
			}
		}
	}
}

extension BackupFile {
	var fileSizeMb: String {
		formatAsMb(fileSizeBytes: fileSizeBytes)
	}
}
