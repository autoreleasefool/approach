import AssetsLibrary
import SwiftUI
import ViewsLibrary

extension View {
	func borders(
		top: Bool = false,
		trailing: Bool = false,
		bottom: Bool = false,
		leading: Bool = false,
		color: ColorAsset = Asset.Colors.ScoreSheet.Border.default,
		thickness: CGFloat = 1
	) -> some View {
		self.overlay(
			top
			? Rectangle().frame(width: nil, height: thickness, alignment: .top).foregroundColor(color.swiftUIColor)
			: nil,
			alignment: .top
		)
		.overlay(
			trailing
			? Rectangle().frame(width: thickness, height: nil, alignment: .trailing).foregroundColor(color.swiftUIColor)
			: nil,
			alignment: .trailing
		)
		.overlay(
			bottom
			? Rectangle().frame(width: nil, height: thickness, alignment: .bottom).foregroundColor(color.swiftUIColor)
			: nil,
			alignment: .bottom
		)
		.overlay(
			leading
			? Rectangle().frame(width: thickness, height: nil, alignment: .leading).foregroundColor(color.swiftUIColor)
			: nil,
			alignment: .leading
		)
	}

	func roundCorners(
		topLeading: Bool = false,
		topTrailing: Bool = false,
		bottomLeading: Bool = false,
		bottomTrailing: Bool = false
	) -> some View {
		var corners: UIRectCorner = []
		if topLeading { corners.insert(.topLeft) }
		if topTrailing { corners.insert(.topRight) }
		if bottomLeading { corners.insert(.bottomLeft) }
		if bottomTrailing { corners.insert(.bottomRight) }
		return self.clipShape(RoundedCorner(corners: corners))
	}
}
