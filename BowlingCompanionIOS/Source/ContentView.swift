import AppFeature
import ComposableArchitecture
import FeatureFlagServiceInterface
import SwiftUI

struct ContentView: View {
	let store: Store = {
		@Dependency(\.featureFlags) var featureFlags: FeatureFlagService
		return .init(
			initialState: App.State(
				hasDeveloperFeature: featureFlags.isEnabled(.developerOptions)
			),
			reducer: App()._printChanges()
		)
	}()

	var body: some View {
		NavigationView {
			AppView(store: store)
		}
	}
}

#if DEBUG
struct ContentViewPreviews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}
#endif
