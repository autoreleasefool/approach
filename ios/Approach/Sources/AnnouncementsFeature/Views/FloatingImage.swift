import AssetsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary

struct FloatingImage: View {
	let image: ImageAsset

	@State private var imageSize: CGSize = .zero

	var body: some View {
		VStack(spacing: .standardSpacing) {
			image.swiftUIImage
				.resizable()
				.scaledToFit()
				.measure(key: ImageSizeKey.self, to: $imageSize)
				.frame(maxWidth: .infinity)
				.phaseAnimator([0, 1]) { content, phase in
					content
						.offset(y: phase == 0 ? -16 : 0)
				} animation: { _ in
						.bouncy(duration: 0.6)
				}

			Ellipse()
				.fill(.black.opacity(0.5))
				.frame(width: (imageSize.width * 4) / 5, height: .extraTinyIcon)
				.blur(radius: .standardRadius)
		}
	}
}

private struct ImageSizeKey: PreferenceKey, CGSizePreferenceKey {}

#Preview {
	FloatingImage(image: Asset.Media.Achievements.tenYears)
}
