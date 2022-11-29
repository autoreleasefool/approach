import AppFeature
import ComposableArchitecture
import XCTest

@MainActor
final class AppFeatureTests: XCTestCase {
	func testSelectsTab() async {
		let store = TestStore(
			initialState: App.State(hasDeveloperFeature: false),
			reducer: App()
		)

		await store.send(.selectedTab(.settings)) {
			$0.selectedTab = .settings
		}
	}
}
