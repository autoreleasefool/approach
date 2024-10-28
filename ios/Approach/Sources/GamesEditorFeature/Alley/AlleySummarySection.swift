import AssetsLibrary
import GamesRepositoryInterface
import IdentifiedCollections
import ModelsLibrary
import ModelsViewsLibrary
import StringsLibrary
import SwiftUI

public struct AlleySummarySection: View {
	let alleyInfo: Game.Edit.AlleyInfo?
	let lanes: IdentifiedArrayOf<Lane.Summary>
	let action: () -> Void

	public var body: some View {
		Section {
			if alleyInfo == nil {
				Text(Strings.Game.Editor.Fields.Alley.noneSelected)
			} else if lanes.isEmpty {
				Text(Strings.Game.Editor.Fields.Alley.Lanes.help)
			} else {
				ForEach(lanes) { lane in
					Lane.View(lane)
				}
			}
		} header: {
			HStack(alignment: .firstTextBaseline) {
				if let name = alleyInfo?.name {
					Text(name)
					Spacer()
					Button(action: action) {
						Text(Strings.Action.select)
							.font(.caption)
					}
				} else {
					Text(Strings.Alley.title)
				}
			}
		} footer: {
			if !lanes.isEmpty {
				Text(Strings.Game.Editor.Fields.Alley.Lanes.help)
			}
		}
	}
}

#if DEBUG
#Preview {
	List {
		AlleySummarySection(alleyInfo: nil, lanes: []) { }

		AlleySummarySection(alleyInfo: .init(id: UUID(0), name: "Skyview Lanes"), lanes: []) { }

		AlleySummarySection(alleyInfo: .init(id: UUID(0), name: "Skyview Lanes"), lanes: [
			.init(id: UUID(0), label: "1", position: .leftWall),
			.init(id: UUID(1), label: "2", position: .noWall),
		]) { }
	}
}
#endif
