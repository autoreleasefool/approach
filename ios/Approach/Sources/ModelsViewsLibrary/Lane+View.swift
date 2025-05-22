import AssetsLibrary
import ModelsLibrary
import SwiftUI

extension Lane {
	public struct View: SwiftUI.View {
		let label: String
		let position: Lane.Position

		public init(_ lane: Lane.Summary) {
			self.init(label: lane.label, position: lane.position)
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
					Image(systemName: "arrow.left.to.line.compact")
						.foregroundColor(.gray)
				case .rightWall:
					Image(systemName: "arrow.right.to.line.compact")
						.foregroundColor(.gray)
				case .noWall:
					EmptyView()
				}
			}
		}
	}
}

#if DEBUG
#Preview {
	List {
		Lane.View(label: "1", position: .leftWall)
		Lane.View(label: "2", position: .noWall)
		Lane.View(label: "3", position: .noWall)
		Lane.View(label: "4", position: .rightWall)
	}
}
#endif
