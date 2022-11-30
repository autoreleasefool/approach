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
		Text(gear.name)
			.frame(maxWidth: .infinity, alignment: .leading)
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
