import SwiftUI

extension ImageAsset: Equatable {
	public static func == (lhs: Self, rhs: Self) -> Bool {
		lhs.name == rhs.name
	}
}

extension ColorAsset: Equatable {
	public static func == (lhs: ColorAsset, rhs: ColorAsset) -> Bool {
		lhs.name == rhs.name
	}
}

extension View {
	public func background(_ asset: ColorAsset) -> some View {
		self.background(asset.swiftUIColor)
	}

	public func foregroundStyle(_ asset: ColorAsset) -> some View {
		self.foregroundStyle(asset.swiftUIColor)
	}

	public func tint(_ asset: ColorAsset) -> some View {
		self.tint(asset.swiftUIColor)
	}
}
