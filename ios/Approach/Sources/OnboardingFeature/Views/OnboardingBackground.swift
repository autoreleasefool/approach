import AssetsLibrary
import SwiftUI

struct OnboardingBackground: View {
	var body: some View {
		Asset.Media.Onboarding.background.swiftUIImage
			.resizable(resizingMode: .tile)
			.frame(maxWidth: .infinity, maxHeight: .infinity)
	}
}

struct OnboardingContainer: View {
	let fadedEdges: Set<VerticalEdge>

	init(fadedEdges: Set<VerticalEdge> = []) {
		self.fadedEdges = fadedEdges
	}

	var body: some View {
		VStack(spacing: 0) {
			if fadedEdges.contains(.top) {
				Rectangle()
					.fill(.linearGradient(
						.init(colors: [
							Asset.Colors.Onboarding.background.swiftUIColor.opacity(0.0),
							Asset.Colors.Onboarding.background.swiftUIColor,
						]),
						startPoint: .top,
						endPoint: .bottom
					))
					.frame(height: .extraLargeSpacing)
			}

			Rectangle().fill(Asset.Colors.Onboarding.background.swiftUIColor)

			if fadedEdges.contains(.bottom) {
				Rectangle()
					.fill(.linearGradient(
						.init(colors: [
							Asset.Colors.Onboarding.background.swiftUIColor.opacity(0.0),
							Asset.Colors.Onboarding.background.swiftUIColor,
						]),
						startPoint: .bottom,
						endPoint: .top
					))
					.frame(height: .extraLargeSpacing)
			}
		}
	}
}
