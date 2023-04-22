import AssetsLibrary
import ModelsLibrary
import SwiftUI

extension Lane {
	public struct View: SwiftUI.View {
		let label: String
		let position: Lane.Position

		public init(lane: Lane.Summary) {
			self.label = lane.label
			self.position = lane.position
		}

		public init(label: String, position: Lane.Position) {
			self.label = label
			self.position = position
		}

		public var body: some SwiftUI.View {
			HStack(alignment: .center, spacing: .standardSpacing) {
				Text(label)
					.frame(maxWidth: .infinity, alignment: .leading)
				// TODO: choose a better icon for the wall indicator
				if position != .noWall {
					Image(systemName: "decrease.quotelevel")
						.opacity(0.7)
				}
			}
		}
	}
}
