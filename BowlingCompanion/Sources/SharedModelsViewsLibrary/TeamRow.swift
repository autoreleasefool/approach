import AssetsLibrary
import SharedModelsLibrary
import SwiftUI
import ViewsLibrary

public struct TeamRow: View {
	let team: Team
	let onEdit: (() -> Void)?
	let onDelete: (() -> Void)?

	public init(team: Team, onEdit: (() -> Void)? = nil, onDelete: (() -> Void)? = nil) {
		self.team = team
		self.onEdit = onEdit
		self.onDelete = onDelete
	}

	public var body: some View {
		Text(team.name)
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
struct TeamRowPreview: PreviewProvider {
	static var previews: some View {
		List {
			Section {
				TeamRow(team: .init(id: .init(), name: "Junior Boys, 2022"))
				TeamRow(team: .init(id: .init(), name: "The Family"))
			}
			.listRowBackground(Color(uiColor: .secondarySystemBackground))
		}
		.scrollContentBackground(.hidden)
	}
}
#endif
