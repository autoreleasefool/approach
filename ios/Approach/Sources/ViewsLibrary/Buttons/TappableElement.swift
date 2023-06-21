import AssetsLibrary
import SwiftUI

public struct TappableElement: ButtonStyle {
	let mode: Mode

	public init(_ background: ColorAsset, pressed: ColorAsset) {
		self.mode = .color(background: background.swiftUIColor, pressed: pressed.swiftUIColor)
	}

	public init(opacity: Double = 0.6) {
		self.mode = .opacity(opacity)
	}

	@ViewBuilder
	public func makeBody(configuration: Configuration) -> some View {
		switch mode {
		case let .opacity(opacity):
			configuration.label
				.opacity(configuration.isPressed ? opacity : 1.0)
		case let .color(background, pressed):
			configuration.label
				.background(configuration.isPressed ? pressed : background)
		}
	}
}

extension TappableElement {
	enum Mode {
		case opacity(Double)
		case color(background: Color, pressed: Color)
	}
}
