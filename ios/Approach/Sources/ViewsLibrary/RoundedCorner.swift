import AssetsLibrary
import SwiftUI

extension View {
	public func roundCorners(
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
		return clipShape(RoundedCorner(corners: corners))
	}
}

public struct RoundedCorner: Shape {
	let radius: CGFloat
	let corners: UIRectCorner

	public init(_ radius: CGFloat = .standardRadius, corners: UIRectCorner = .allCorners) {
		self.radius = radius
		self.corners = corners
	}

	public func path(in rect: CGRect) -> Path {
		let path = UIBezierPath(
			roundedRect: rect,
			byRoundingCorners: corners,
			cornerRadii: CGSize(width: radius, height: radius)
		)
		return Path(path.cgPath)
	}
}
