import AssetsLibrary
import SwiftUI

struct SettingsLink<Value: Hashable>: View {
	let title: String
	let subtitle: String?
	let destination: Value

	init(
		title: String,
		subtitle: String? = nil,
		destination: Value
	) {
		self.title = title
		self.subtitle = subtitle
		self.destination = destination
	}

	var body: some View {
		NavigationLink(value: destination) {
			if let subtitle {
				VStack(alignment: .leading, spacing: .unitSpacing) {
					Text(title)
					Text(subtitle)
						.font(.footnote)
				}
			} else {
				Text(title)
			}
		}
	}
}
