import AssetsLibrary
import SwiftUI

struct ModalImagePreview: View {
	let image: UIImage
	let namespace: Namespace.ID
	let onClose: () -> Void

	@State private var currentZoom = 0.0
	@State private var totalZoom = 1.0

	var body: some View {
		VStack(spacing: 0) {
			Spacer()

			Image(uiImage: image)
				.resizable()
				.scaledToFit()
				.matchedGeometryEffect(id: "Preview", in: namespace)
				.scaleEffect(currentZoom + totalZoom)
				.gesture(
					MagnifyGesture()
						.onChanged { value in
							currentZoom = min(max(value.magnification, 0.2), 5.0) - 1
						}
						.onEnded { _ in
							totalZoom += currentZoom
							currentZoom = 0
						}
				)

			Spacer()
		}
		.contentShape(Rectangle())
		.onTapGesture(perform: onClose)
	}
}

private struct CloseButton: View {
	let action: () -> Void

	var body: some View {
		Image(systemSymbol: .xmarkCircle)
			.font(.title)
			.foregroundStyle(.white)
			.padding(.largeSpacing)
			.onTapGesture(perform: action)
	}
}
