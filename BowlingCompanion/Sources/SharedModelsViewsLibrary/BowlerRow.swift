import SharedModelsLibrary
import SwiftUI
import ThemesLibrary
import ViewsLibrary

public struct BowlerRow: View {
	let bowler: Bowler
	let onEdit: (() -> Void)?
	let onDelete: (() -> Void)?

	public init(bowler: Bowler, onEdit: (() -> Void)? = nil, onDelete: (() -> Void)? = nil) {
		self.bowler = bowler
		self.onEdit = onEdit
		self.onDelete = onDelete
	}

	public var body: some View {
		AvatarView(size: .medium, title: bowler.name)
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
