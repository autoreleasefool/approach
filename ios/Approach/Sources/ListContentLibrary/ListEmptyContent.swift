import AssetsLibrary
import SwiftUI

public struct ListEmptyContent: View {
	@ScaledMetric private var unit: CGFloat = 20

	public let image: ImageAsset
	public let title: String
	public let message: String?
	public let style: Style
	public let action: EmptyContentAction

	public init(
		_ image: ImageAsset,
		title: String,
		message: String? = nil,
		style: Style = .empty,
		@ViewBuilder action: () -> EmptyContentAction
	) {
		self.image = image
		self.title = title
		self.message = message
		self.style = style
		self.action = action()
	}

	public var body: some View {
		GeometryReader { proxy in
			VStack {
				Spacer()

				image.swiftUIImage
					.resizable()
					.scaledToFit()
					.padding(.bottom, .smallSpacing)

				Spacer()

				VStack(spacing: .smallSpacing) {
					Text(title)
						.font(.headline)

					if let message {
						Text(message)
							.multilineTextAlignment(.center)
					}
				}
				.padding()
				.frame(maxWidth: .infinity)
				.background(style == .error ? Asset.Colors.Error.light : Asset.Colors.Primary.light)
				.cornerRadius(.standardRadius)
				.padding(.bottom, .smallSpacing)

				action
			}
			.padding()
			.padding(.horizontal, padding(for: proxy.size.width))
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

extension ListEmptyContent {
	public enum Style {
		case empty
		case error
	}
}

public struct EmptyContentAction: View {
	public let title: String
	public let perform: () -> Void

	public init(title: String, perform: @escaping () -> Void) {
		self.title = title
		self.perform = perform
	}

	public var body: some View {
		Button(action: perform) {
			Text(title)
				.frame(maxWidth: .infinity)
		}
		.buttonStyle(.borderedProminent)
		.controlSize(.large)
		.foregroundColor(.white)
		.tint(Asset.Colors.Action.default)
	}
}
