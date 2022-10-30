import AppFeature
import ComposableArchitecture
import SwiftUI

struct ContentView: View {
	let store = Store(
		initialState: App.State(),
		reducer: App()._printChanges()
	)

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
