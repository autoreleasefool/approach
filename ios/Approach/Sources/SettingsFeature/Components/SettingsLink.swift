import AssetsLibrary
import SwiftUI

struct SettingsLink<Value: Hashable>: View {
	let title: String
	let subtitle: String?
	let badge: Int?
	let destination: Value

	init(
		title: String,
		subtitle: String? = nil,
		badge: Int? = nil,
		destination: Value
	) {
		self.title = title
		self.subtitle = subtitle
		self.badge = badge
		self.destination = destination
	}

	var body: some View {
		NavigationLink(value: destination) {
			HStack(alignment: .center, spacing: 0) {
				VStack(alignment: .leading, spacing: .unitSpacing) {
					Text(title)

					if let subtitle {
						Text(subtitle)
							.font(.footnote)
					}
				}

				if let badge, badge > 0 {
					Spacer(minLength: .standardSpacing)

					Text("\(badge)")
						.foregroundStyle(.secondary)
				}
			}
		}
	}
}
