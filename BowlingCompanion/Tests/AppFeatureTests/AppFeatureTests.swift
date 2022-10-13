import AppFeature
import ComposableArchitecture
import XCTest

@MainActor
final class AppFeatureTests: XCTestCase {
	func testViewAppears() async {
		let store = TestStore(
			initialState: App.State(),
			reducer: App()
		)

		await store.send(.onAppear).finish()
	}
}
