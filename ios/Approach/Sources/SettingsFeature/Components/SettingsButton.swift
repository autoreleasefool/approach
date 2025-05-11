import SwiftUI

struct SettingsButton: View {
	let title: String
	let subtitle: String?
	let action: () -> Void

	init(
		title: String,
		subtitle: String? = nil,
		action: @escaping () -> Void
	) {
		self.title = title
		self.subtitle = subtitle
		self.action = action
	}

	var body: some View {
		Button(action: action) {
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
		.buttonStyle(.navigation)
	}
}
