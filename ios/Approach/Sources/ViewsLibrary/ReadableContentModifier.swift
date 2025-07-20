import SwiftUI

public struct ReadableContentModifier: ViewModifier {
	@Environment(\.horizontalSizeClass) private var hClass
	@Environment(\.verticalSizeClass) private var vClass

	public func body(content: Content) -> some View {
		content
			.containerRelativeFrame([.horizontal]) { length, axis in
				guard axis == .horizontal else { return length }
				if vClass == .regular && hClass == .regular {
					return length * 0.52
				} else {
					return length
				}
			}
	}
}

extension View {
	public func readableContentGuide() -> some View {
		modifier(ReadableContentModifier())
	}
}
