import AssetsLibrary
import SwiftUI

struct Shield: View {
	var body: some View {
		GeometryReader { proxy in
			let size = proxy.size.width
			let midPoint = size / 2

			ZStack {
				Color.appShieldBackground
					.frame(width: size, height: size)
					.position(x: midPoint, y: midPoint)
				Color.appShieldFrame
					.frame(width: 8, height: size)
					.position(x: 4, y: midPoint)
				Color.appShieldFrame
					.frame(width: 8, height: size)
					.position(x: size - 4, y: midPoint)
				ZStack {
					Color.appShieldProtector
						.frame(width: size, height: 68)
						.position(x: midPoint, y: 34)
					Color.appShieldFrame
						.frame(width: size, height: 8)
						.position(x: midPoint, y: 4)

					Image(uiImage: .shieldName)
						.frame(height: 44)
						.position(x: midPoint, y: 34)
				}
				.frame(width: size, height: 68)
				.position(x: midPoint, y: size - 34)
			}
		}
	}
}
