import AssetsLibrary
import ModelsLibrary
import ModelsViewsLibrary
import SwiftUI

extension Gear {
	public struct ViewWithAvatar: SwiftUI.View {
		let gear: Gear.Summary

		public init(_ gear: Gear.Summary) {
			self.gear = gear
		}

		public var body: some SwiftUI.View {
			HStack(alignment: .center) {
				AvatarView(gear.avatar, size: .smallIcon)
				Gear.View(gear)
			}
		}
	}
}
