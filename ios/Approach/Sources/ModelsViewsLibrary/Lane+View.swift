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
				switch position {
				case .leftWall:
					Image(systemSymbol: .arrowLeftToLineCompact)
						.foregroundColor(.gray)
				case .rightWall:
					Image(systemSymbol: .arrowRightToLineCompact)
						.foregroundColor(.gray)
				case .noWall:
					EmptyView()
				}
			}
		}
	}
}
