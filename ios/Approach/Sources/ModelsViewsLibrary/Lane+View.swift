import AssetsLibrary
import ModelsLibrary
import SwiftUI

extension Lane {
	public struct View: SwiftUI.View {
		let lane: Lane.Summary

		public init(lane: Lane.Summary) {
			self.lane = lane
		}

		public var body: some SwiftUI.View {
			HStack(alignment: .center, spacing: .standardSpacing) {
				Text(lane.label)
					.frame(maxWidth: .infinity, alignment: .leading)
				// TODO: choose a better icon for the wall indicator
				if lane.position != .noWall {
					Image(systemName: "decrease.quotelevel")
						.opacity(0.7)
				}
			}
		}
	}
}
