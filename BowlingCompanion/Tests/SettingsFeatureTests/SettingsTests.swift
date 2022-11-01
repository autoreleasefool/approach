import ComposableArchitecture
import SettingsFeature
import XCTest

@MainActor
final class SettingsTests: XCTestCase {
	func testPlaceholder() async {
		let store = TestStore(
			initialState: Settings.State(),
			reducer: Settings()
		)

		await store.send(.placeholder).finish()
	}
}
