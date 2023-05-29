import Dependencies
import Foundation
import SwiftUI

public struct UIDeviceNotifications: Sendable {
	public var orientationDidChange: @Sendable () -> AsyncStream<UIDeviceOrientation>

	public init(
		orientationDidChange: @escaping @Sendable () -> AsyncStream<UIDeviceOrientation>
	) {
		self.orientationDidChange = orientationDidChange
	}
}

extension UIDeviceNotifications: TestDependencyKey {
	public static var testValue = Self(
		orientationDidChange: { unimplemented("\(Self.self).orientationDidChange") }
	)
}

extension DependencyValues {
	public var uiDeviceNotifications: UIDeviceNotifications {
		get { self[UIDeviceNotifications.self] }
		set { self[UIDeviceNotifications.self] = newValue }
	}
}
