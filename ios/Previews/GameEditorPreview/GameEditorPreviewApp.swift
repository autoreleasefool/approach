import ComposableArchitecture
import DatabaseServiceInterface
import GamesEditorFeature
import GRDB
import ModelsLibrary
import SwiftUI
import TestDatabaseUtilitiesLibrary

@main
public struct GameEditorPreviewApp: App {
	let store: Store = {
		return .init(
			initialState: GamesEditor.State(
				bowlerIds: [UUID(0), UUID(1)],
				bowlerGameIds: [
					UUID(0): [UUID(1), UUID(2)],
					UUID(1): [UUID(4), UUID(5)],
				],
				initialBowlerId: UUID(0),
				initialGameId: UUID(1)
			),
			reducer: {
				GamesEditor()//._printChanges()
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
			}
		)
	}()

	public init() {}

	public var body: some Scene {
		WindowGroup {
			NavigationStack {
				GamesEditorView(store: store)
			}
		}
	}
}
