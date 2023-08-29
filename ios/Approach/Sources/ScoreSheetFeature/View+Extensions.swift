import AssetsLibrary
import SwiftUI
import ViewsLibrary

extension View {
	func topBorder(visible: Bool = true) -> some View {
		overlay(
			Rectangle()
				.frame(width: nil, height: 1, alignment: .top)
				.foregroundColor(visible ? Asset.Colors.ScoreSheet.Border.default.swiftUIColor : Color.clear),
			alignment: .top
		)
	}

	func bottomBorder(visible: Bool = true) -> some View {
		overlay(
			Rectangle()
				.frame(width: nil, height: 1, alignment: .bottom)
				.foregroundColor(visible ? Asset.Colors.ScoreSheet.Border.default.swiftUIColor : Color.clear),
			alignment: .bottom
		)
	}

	func leadingBorder(visible: Bool = true) -> some View {
		overlay(
			Rectangle()
				.frame(width: 1, height: nil, alignment: .leading)
				.foregroundColor(visible ? Asset.Colors.ScoreSheet.Border.default.swiftUIColor : Color.clear),
			alignment: .leading
		)
	}

	func trailingBorder(visible: Bool = true) -> some View {
		overlay(
			Rectangle()
				.frame(width: 1, height: nil, alignment: .trailing)
				.foregroundColor(visible ? Asset.Colors.ScoreSheet.Border.default.swiftUIColor : Color.clear),
			alignment: .trailing
		)
	}

	func applyCornerRadius(
		isFirstFrame: Bool,
		isLastFrame: Bool,
		toTop: Bool
	) -> some View {
		var corners: UIRectCorner = []
		if isFirstFrame {
			corners.insert(toTop ? .topLeft : .bottomLeft)
		}
		if isLastFrame {
			corners.insert(toTop ? .topRight : .bottomRight)
		}

		return self.clipShape(RoundedCorner(corners: corners))
	}
}
