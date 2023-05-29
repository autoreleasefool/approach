import Dependencies
@testable import NotificationsService
@testable import NotificationsServiceInterface
import XCTest

@MainActor
final class UIDeviceNotificationsTests: XCTestCase {
	@Dependency(\.uiDeviceNotifications) var uiDeviceNotifications

	func testReceivesNotifications() async throws {
		let expectation = self.expectation(description: "received notification")
		expectation.expectedFulfillmentCount = 2

		Task.detached {
			await withDependencies {
				$0.uiDeviceNotifications = .liveValue
			} operation: {
				for await _ in await self.uiDeviceNotifications.orientationDidChange() {
					expectation.fulfill()
				}
			}
		}

		try await Task.sleep(for: .seconds(1))
		NotificationCenter.default.post(name: UIDevice.orientationDidChangeNotification, object: nil)

		await fulfillment(of: [expectation])
	}
}
