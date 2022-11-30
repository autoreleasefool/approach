import SharedModelsLibrary
import SwiftUI
import ThemesLibrary
import ViewsLibrary

public struct LeagueRow: View {
	let league: League
	let onEdit: (() -> Void)?
	let onDelete: (() -> Void)?

	public init(league: League, onEdit: (() -> Void)? = nil, onDelete: (() -> Void)? = nil) {
		self.league = league
		self.onEdit = onEdit
		self.onDelete = onDelete
	}

	public var body: some View {
		AvatarView(size: .medium, title: league.name)
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
