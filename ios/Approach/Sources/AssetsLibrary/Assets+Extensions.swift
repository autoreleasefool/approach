import SwiftUI

extension ImageAsset: Equatable {
	public static func == (lhs: Self, rhs: Self) -> Bool {
		lhs.name == rhs.name
	}
}

extension View {
	public func background(_ asset: ColorAsset) -> some View {
		self.background(asset.swiftUIColor)
	}

	public func foregroundColor(_ asset: ColorAsset) -> some View {
		self.foregroundColor(asset.swiftUIColor)
	}

	public func tint(_ asset: ColorAsset) -> some View {
		self.tint(asset.swiftUIColor)
	}
}
