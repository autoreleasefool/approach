import AssetsLibrary
import SharedModelsLibrary
import SwiftUI
import ViewsLibrary

public struct OpponentRow: View {
	let opponent: Opponent
	let onEdit: (() -> Void)?
	let onDelete: (() -> Void)?

	public init(opponent: Opponent, onEdit: (() -> Void)? = nil, onDelete: (() -> Void)? = nil) {
		self.opponent = opponent
		self.onEdit = onEdit
		self.onDelete = onDelete
	}

	public var body: some View {
		Text(opponent.name)
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

#if DEBUG
struct OpponentRowPreview: PreviewProvider {
	static var previews: some View {
		List {
			Section {
				OpponentRow(opponent: .init(id: .init(), name: "Joseph"))
				OpponentRow(opponent: .init(id: .init(), name: "Sarah"))
				OpponentRow(opponent: .init(id: .init(), name: "Audriana Roque"))
			}
			.listRowBackground(Color(uiColor: .secondarySystemBackground))
		}
		.scrollContentBackground(.hidden)
	}
}
#endif
