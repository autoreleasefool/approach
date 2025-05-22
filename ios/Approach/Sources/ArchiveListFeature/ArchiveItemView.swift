import AssetsLibrary
import ModelsLibrary
import StringsLibrary
import SwiftUI

public struct ArchiveItemView: View {
	let item: ArchiveItem

	public var body: some View {
		HStack {
			Image(systemName: item.id.systemImage)

			VStack(alignment: .leading) {
				Text(item.title)
					.bold()

				Text(item.subtitle)
					.font(.caption)

				Text(Strings.Archive.List.archivedOn(item.archivedOn?.longFormat ?? Strings.unknown))
					.font(.caption)
					.opacity(0.7)
			}
		}
	}
}

extension ArchiveItemID {
	var systemImage: String {
		switch self {
		case .bowler: "person.fill"
		case .league: "repeat"
		case .series: "calendar"
		case .game: "figure.bowling"
		}
	}
}
