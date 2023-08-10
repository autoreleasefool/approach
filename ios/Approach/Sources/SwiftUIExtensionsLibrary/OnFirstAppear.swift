import SwiftUI

public struct OnFirstAppearModifier: ViewModifier {
	@State var didAppear = false
	let onFirstAppear: () -> Void

	public init(onFirstAppear: @escaping () -> Void) {
		self.onFirstAppear = onFirstAppear
	}

	public func body(content: Content) -> some View {
		content
			.onAppear {
				guard !didAppear else { return }
				didAppear = true
				onFirstAppear()
			}
	}
}

extension View {
	public func onFirstAppear(perform: @escaping () -> Void) -> some View {
		self.modifier(OnFirstAppearModifier(onFirstAppear: perform))
	}
}
