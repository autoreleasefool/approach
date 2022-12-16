import SharedModelsLibrary
import SwiftUI
import ThemesLibrary
import ViewsLibrary

public struct GearRow: View {
	let gear: Gear
	let onEdit: (() -> Void)?
	let onDelete: (() -> Void)?

	public init(gear: Gear, onEdit: (() -> Void)? = nil, onDelete: (() -> Void)? = nil) {
		self.gear = gear
		self.onEdit = onEdit
		self.onDelete = onDelete
	}

	public var body: some View {
		Label(gear.name, systemImage: gear.kind.image)
			.swipeActions(allowsFullSwipe: true) {
				if let onEdit {
					EditButton(perform: onEdit)
				}

				if let onDelete {
					DeleteButton(perform: onDelete)
				}
			}
	}
}


#if DEBUG
struct GearRowPreview: PreviewProvider {
	static var previews: some View {
		List {
			Section {
				GearRow(gear: .init(bowler: .init(), id: .init(), name: "Yellow", kind: .bowlingBall))
				GearRow(gear: .init(bowler: .init(), id: .init(), name: "Bowling School, 2022", kind: .towel))
			}
			.listRowBackground(Color(uiColor: .secondarySystemBackground))
		}
		.scrollContentBackground(.hidden)
	}
}
#endif
