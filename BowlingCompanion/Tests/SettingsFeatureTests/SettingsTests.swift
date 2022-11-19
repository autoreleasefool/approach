import ComposableArchitecture
import FeatureFlagServiceInterface
import SettingsFeature
import XCTest

@MainActor
final class SettingsTests: XCTestCase {
	func testShowsFeatures() {
		DependencyValues.withValues {
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
