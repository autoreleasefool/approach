import ComposableArchitecture
import GamesEditorFeature
import SharedModelsLibrary
import SwiftUI

@main
struct GameEditorPreviewApp: App {
	let store: Store = {
		return .init(
			initialState: GamesEditor.State(games: .init(uniqueElements: Game.mocks), current: Game.mocks.first!.id),
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
