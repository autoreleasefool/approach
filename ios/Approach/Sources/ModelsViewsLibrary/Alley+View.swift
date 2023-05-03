import AssetsLibrary
import ModelsLibrary
import SwiftUI

extension Alley {
	public struct View: SwiftUI.View {
		let alley: Alley.Summary

		public init(alley: Alley.Summary) {
			self.alley = alley
		}

		public var body: some SwiftUI.View {
			VStack(alignment: .leading, spacing: .unitSpacing) {
				Text(alley.name)
					.font(.headline)

				if let subtitle = alley.location?.subtitle, !subtitle.isEmpty {
					Text(subtitle)
						.font(.body)
						.padding(.top, .unitSpacing)
				}
			}
		}
	}
}
