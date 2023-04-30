import ComposableArchitecture
import DatabaseServiceInterface
import GamesEditorFeature
import GRDB
import ModelsLibrary
import SwiftUI
import TestDatabaseUtilitiesLibrary

@main
struct GameEditorPreviewApp: App {
	let store: Store = {
		return .init(
			initialState: GamesEditor.State(
				bowlers: [.init(id: UUID(0), name: "Joseph")],
				bowlerGames: [UUID(0): [UUID(0), UUID(1)]],
				currentBowler: UUID(0),
				currentGame: UUID(0)
			),
			reducer: GamesEditor()._printChanges()
				.dependency(\.database, {
					let db: any DatabaseWriter
					do {
						db = try initializeDatabase(
							withLocations: .default,
							withAlleys: .default,
							withLanes: .default,
							withBowlers: .default,
							withGear: .default,
							withLeagues: .default,
							withSeries: .default,
							withGames: .default,
							withFrames: .default
						)
					} catch {
						fatalError("Could not initialize database: \(error)")
					}

					return .init(reader: { db }, writer: { db })
				}())
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
