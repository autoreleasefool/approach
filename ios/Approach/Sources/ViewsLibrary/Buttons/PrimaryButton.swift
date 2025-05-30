import AssetsLibrary
import SwiftUI

public struct PrimaryButton: ViewModifier {
	public init() {}

	public func body(content: Content) -> some View {
		content
			.buttonStyle(.borderedProminent)
			.controlSize(.large)
			.foregroundStyle(.white)
			.tint(Asset.Colors.Action.default)
	}
}
