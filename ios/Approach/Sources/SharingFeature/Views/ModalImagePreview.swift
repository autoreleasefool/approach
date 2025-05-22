import AssetsLibrary
import SwiftUI

struct ModalImagePreview: View {
	let image: UIImage
	let namespace: Namespace.ID
	let onClose: () -> Void

	@State private var currentZoom = 0.0
	@State private var totalZoom = 1.0

	@State private var offset = CGSize.zero
	@State private var lastOffset = CGSize.zero

	var body: some View {
		VStack(spacing: 0) {
			Spacer()

			Image(uiImage: image)
				.resizable()
				.scaledToFit()
				.matchedGeometryEffect(id: "Preview", in: namespace)
				.scaleEffect(currentZoom + totalZoom)
				.offset(offset)
				.gesture(
					magnifyGesture
						.simultaneously(with: dragOffsetGesture)
				)
				.onTapGesture(perform: onClose)

			Spacer()
		}
		.contentShape(Rectangle())
		.onTapGesture(perform: onClose)
		.overlay(alignment: .topLeading) {
			CloseButton(action: onClose)
				.padding()
		}
	}

	private var magnifyGesture: some Gesture {
		MagnifyGesture()
			.onChanged { value in
				currentZoom = min(max(value.magnification, 0.2), 5.0) - 1
			}
			.onEnded { _ in
				totalZoom += currentZoom
				currentZoom = 0
			}
	}

	private var dragOffsetGesture: some Gesture {
		DragGesture()
			.onChanged { value in
				offset = CGSize(
					width: lastOffset.width + value.translation.width,
					height: lastOffset.height + value.translation.height
				)
			}
			.onEnded { _ in
				lastOffset = offset
			}
	}
}

private struct CloseButton: View {
	let action: () -> Void

	var body: some View {
		Button(action: action) {
			Image(systemName: "xmark.circle")
				.resizable()
				.scaledToFit()
				.frame(width: .smallIcon, height: .smallIcon)
				.padding()
		}
		.buttonStyle(.plain)
	}
}

#Preview {
	@Previewable @Namespace var previewNamespace

	ModalImagePreview(image: Asset.Media.EmptyState.bowlers.image, namespace: previewNamespace) {}
}
