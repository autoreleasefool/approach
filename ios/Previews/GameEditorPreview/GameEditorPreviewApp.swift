import ComposableArchitecture
import GamesEditorFeature
import SharedModelsLibrary
import SwiftUI

@main
struct GameEditorPreviewApp: App {
	let store: Store = {
		return .init(
			initialState: GamesEditor.State(
				bowlers: Bowler.mocks,
				bowlerGames: [Bowler.mocks.first!.id: Game.mocks.map(\.id)],
				currentBowler: Bowler.mocks.first!.id,
				currentGame: Game.mocks.first!.id
			),
			reducer: GamesEditor()._printChanges()
		)
	}()

	var body: some Scene {
		WindowGroup {
			NavigationView {
				GamesEditorView(store: store)
			}
		}
	}
}

extension Bowler {
	public static let mocks: IdentifiedArrayOf<Self> = .init(uniqueElements: [
		.init(
			id: UUID(uuidString: "00000000-0000-0000-0000-000000000001")!,
			name: "Joseph",
			avatar: .text("JR", .red())
		)
	])
}

extension Game {
	public static let mocks: [Self] = [
		.init(
			series: UUID(uuidString: "00000000-0000-0000-0000-000000000001")!,
			id: UUID(uuidString: "00000000-0000-0000-0000-000000000002")!,
			ordinal: 1,
			locked: .unlocked,
			manualScore: nil,
			excludeFromStatistics: .include
		),
		.init(
			series: UUID(uuidString: "00000000-0000-0000-0000-000000000001")!,
			id: UUID(uuidString: "00000000-0000-0000-0000-000000000003")!,
			ordinal: 2,
			locked: .unlocked,
			manualScore: nil,
			excludeFromStatistics: .include
		),
		.init(
			series: UUID(uuidString: "00000000-0000-0000-0000-000000000001")!,
			id: UUID(uuidString: "00000000-0000-0000-0000-000000000004")!,
			ordinal: 3,
			locked: .unlocked,
			manualScore: nil,
			excludeFromStatistics: .include
		)
	]
}
