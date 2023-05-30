import ComposableArchitecture
import FeatureFlagsServiceInterface
import SettingsFeature
import XCTest

@MainActor
final class SettingsTests: XCTestCase {
	func testShowsFeatures() {
		withDependencies {
			$0.featureFlags.isEnabled = { _ in true }
		} operation: {
			let store = TestStore(
				initialState: Settings.State(),
				reducer: Settings()
			)

			XCTAssertTrue(store.state.showsFeatures)
		}
	}
}
