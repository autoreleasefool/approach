import AssetsLibrary
import ModelsLibrary
import StringsLibrary
import SwiftUI

public struct ArchiveItemView: View {
	let item: ArchiveItem

	public var body: some View {
		HStack {
			Image(systemSymbol: item.id.icon)

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
	var icon: SFSymbol {
		switch self {
		case .bowler: return .personFill
		case .league: return .repeat
		case .series: return .calendar
		case .game: return .figureBowling
		}
	}
}
