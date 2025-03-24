import SwiftUI

struct InteractiveAchievement: View {
	let image: Image
	let isEnabled: Bool

	@State private var valueTranslation: CGSize = .zero
	@State private var isDragging = false

	init(_ image: Image, isEnabled: Bool) {
		self.image = image
		self.isEnabled = isEnabled
	}

	var body: some View {
		ZStack {
			image
				.resizable()
				.scaledToFit()
				.frame(maxWidth: .infinity, maxHeight: .infinity)
		}
		.rotation3DEffect(
			.degrees(isDragging ? 20 : 0),
			axis: (x: -valueTranslation.height, y: valueTranslation.width, z: 0)
		)
		.gesture(
			DragGesture()
				.onChanged { value in
					withAnimation {
						valueTranslation = value.translation
						isDragging = true
					}
				}
				.onEnded { _ in
					withAnimation {
						valueTranslation = .zero
						isDragging = false
					}
				},
			isEnabled: isEnabled
		)
	}
}
