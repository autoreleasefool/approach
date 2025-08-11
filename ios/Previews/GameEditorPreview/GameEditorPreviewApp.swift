import AppPreviewFeature
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
			initialState: GamesEditorNext.State(
				bowlerIds: [UUID(0), UUID(1)],
				bowlerGameIds: [
					UUID(0): [UUID(1), UUID(2)],
					UUID(1): [UUID(4), UUID(5)],
				],
				initialBowlerId: UUID(0),
				initialGameId: UUID(1)
			),
			reducer: {
				GamesEditorNext()
					._printChanges()
			}, withDependencies: {
				$0.analytics = .mock
				$0.breadcrumbs = .mock
				$0.database = .defaults
				$0.errors = .mock
				$0.userDefaults = .mock
				$0.gameAnalytics = .init(trackEvent: { _ in }, resetGameSessionID: {})
				$0.featureFlags = .allEnabled
			}
		)
	}()

	public init() {}

	public var body: some Scene {
		WindowGroup {
			NavigationStack {
				GamesEditorNextView(store: store)
			}
		}
	}
}
