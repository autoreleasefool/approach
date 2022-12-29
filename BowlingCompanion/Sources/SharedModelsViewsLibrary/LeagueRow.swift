import SharedModelsLibrary
import SwiftUI
import AssetsLibrary
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
struct LeagueRowPreview: PreviewProvider {
	static var previews: some View {
		List {
			Section {
				LeagueRow(league: .init(
					bowler: .init(),
					id: .init(),
					name: "Beer League, 2022-2023",
					recurrence: .repeating,
					numberOfGames: 4,
					additionalPinfall: nil,
					additionalGames: nil,
					alley: nil)
				)
				LeagueRow(league: .init(
					bowler: .init(),
					id: .init(),
					name: "Majors, 2022-2023",
					recurrence: .repeating,
					numberOfGames: 4,
					additionalPinfall: nil,
					additionalGames: nil,
					alley: nil)
				)
				LeagueRow(league: .init(
					bowler: .init(),
					id: .init(),
					name: "Majors, 2021-2022",
					recurrence: .repeating,
					numberOfGames: 4,
					additionalPinfall: nil,
					additionalGames: nil,
					alley: nil)
				)
			}
			.listRowBackground(Color(uiColor: .secondarySystemBackground))
		}
		.scrollContentBackground(.hidden)
	}
}
#endif
