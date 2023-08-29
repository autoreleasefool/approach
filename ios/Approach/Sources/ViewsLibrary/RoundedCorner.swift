import SwiftUI

public struct RoundedCorner: Shape {
	let radius: CGFloat
	let corners: UIRectCorner

	public init(_ radius: CGFloat = .infinity, corners: UIRectCorner = .allCorners) {
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
