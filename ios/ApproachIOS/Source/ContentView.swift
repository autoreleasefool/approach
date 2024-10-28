import AppFeature
import ComposableArchitecture
import FeatureFlagsLibrary
import SwiftUI

#if DEBUG
import DatabaseServiceInterface
import DatabaseMockingServiceInterface
import Dependencies
import TestDatabaseUtilitiesLibrary
#endif

public struct ContentView: View {
	@MainActor static let store: Store = {
		#if DEBUG
		return .init(
			initialState: App.State(),
			reducer: {
				App()
					._printChanges()
					.dependency(DatabaseMockingService(mockDatabase: {
						@Dependency(DatabaseService.self) var database
						let writer = database.writer()
						_ = try generatePopulatedDatabase(db: writer)
					}))
			}
		)
		#else
		return .init(
			initialState: App.State(),
			reducer: App()
		)
		#endif
	}()

	public var body: some View {
		AppView(store: Self.store)
	}
}

#if DEBUG
#Preview {
	ContentView()
}
#endif
