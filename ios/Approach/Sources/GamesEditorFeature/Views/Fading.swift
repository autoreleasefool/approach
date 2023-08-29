import SwiftUI

extension View {
	func faded() -> some View {
		self.overlay(
			Rectangle()
				.fill(.linearGradient(
					.init(stops: [
						.init(color: .black, location: 0.0),
						.init(color: .black.opacity(0.3), location: 0.125),
						.init(color: .black.opacity(0.5), location: 0.40),
						.init(color: .black, location: 0.5),
						.init(color: .black.opacity(0.3), location: 0.57),
						.init(color: .black.opacity(0.6), location: 0.875),
						.init(color: .black, location: 1.0),
					]),
					startPoint: .top,
					endPoint: .bottom
				)
			)
		)
	}
}
