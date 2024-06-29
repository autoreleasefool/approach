import SwiftUI

public struct CenteredScrollView<Content: View>: View {
	@ScaledMetric private var unit: CGFloat = 20
	@ViewBuilder let content: Content

	public init(@ViewBuilder content: @escaping () -> Content) {
		self.content = content()
	}

	public var body: some View {
		GeometryReader { proxy in
			ScrollView {
				content
					.padding(.horizontal, padding(for: proxy.size.width))
					.frame(width: proxy.size.width)
					.frame(minHeight: proxy.size.height)
			}
		}
	}

	private func padding(for width: CGFloat) -> CGFloat {
		let idealWidth = 70 * unit / 2

		guard width >= idealWidth else {
				return 0
		}

		return round((width - idealWidth) / 2)
	}
}
