import AvatarServiceInterface
import IdentifiedCollections
import ModelsLibrary
import ModelsViewsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct GearSummarySection: View {
	let gear: IdentifiedArrayOf<Gear.Summary>
	let onEdit: () -> Void
	let onDelete: (Gear.ID) -> Void

	public var body: some View {
		Section {
			if gear.isEmpty {
				Text(Strings.Game.Editor.Fields.Gear.help)
			} else {
				ForEach(gear) { gear in
					Gear.ViewWithAvatar(gear)
						.swipeActions(allowsFullSwipe: false) {
							DeleteButton { onDelete(gear.id) }
						}
				}
			}
		} header: {
			HStack(alignment: .firstTextBaseline) {
				Text(Strings.Gear.List.title)
				Spacer()
				Button(action: onEdit) {
					Text(Strings.Action.select)
						.font(.caption)
				}
			}
		} footer: {
			if !gear.isEmpty {
				Text(Strings.Game.Editor.Fields.Gear.help)
			}
		}
	}
}
