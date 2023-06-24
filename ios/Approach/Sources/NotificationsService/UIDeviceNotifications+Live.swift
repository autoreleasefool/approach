import Dependencies
import Foundation
import NotificationsServiceInterface
import SwiftUI

extension UIDeviceNotifications: DependencyKey {
	public static var liveValue = Self(
		orientationDidChange: {
			AsyncStream(
				UIDeviceOrientation.self,
				bufferingPolicy: .bufferingNewest(1)
			) { continuation in
				let task = Task { @MainActor in
					continuation.yield(UIDevice.current.orientation)
					for await _ in NotificationCenter.default
						.notifications(named: UIDevice.orientationDidChangeNotification)
						.map({ $0.name }) {
						continuation.yield(UIDevice.current.orientation)
					}
				}

				continuation.onTermination = { _ in task.cancel() }
			}
		}
	)
}
