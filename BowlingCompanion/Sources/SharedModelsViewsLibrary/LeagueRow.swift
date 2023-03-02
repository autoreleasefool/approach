import SharedModelsLibrary
import SwiftUI

public struct LeagueRow: View {
	let league: League

	public init(league: League) {
		self.league = league
	}

	public var body: some View {
		Text(league.name)
			.frame(maxWidth: .infinity, alignment: .leading)
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
					excludeFromStatistics: .include,
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
					excludeFromStatistics: .include,
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
					excludeFromStatistics: .exclude,
					alley: nil)
				)
			}
			.listRowBackground(Color(uiColor: .secondarySystemBackground))
		}
		.scrollContentBackground(.hidden)
	}
}
#endif
