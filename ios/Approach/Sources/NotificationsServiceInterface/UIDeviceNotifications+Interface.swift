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
	public static var testValue: Self {
		Self(
			orientationDidChange: {
				unimplemented("\(Self.self).orientationDidChange", placeholder: .never)
			}
		)
	}
}
