import AssetsLibrary
import SwiftUI

struct BadgeView: View {
	let title: String
	let onTap: () -> Void

	var body: some View {
		VStack {
			Text(title)
				.bold()
		}
		.padding()
		.clipShape(RoundedRectangle(cornerRadius: .standardRadius))
		.onTapGesture(perform: onTap)
	}
}