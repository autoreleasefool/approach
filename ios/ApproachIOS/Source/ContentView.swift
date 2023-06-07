import AppFeature
import ComposableArchitecture
import FeatureFlagsServiceInterface
import SwiftUI

public struct ContentView: View {
	let store: Store = {
		return .init(
			initialState: App.State(),
			reducer: App()//._printChanges()
		)
	}()

	public var body: some View {
		AppView(store: store)
	}
}

#if DEBUG
struct ContentViewPreviews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}
#endif
