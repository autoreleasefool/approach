import AssetsLibrary
import SwiftUI

public struct ListEmptyContent: View {
	public let image: UIImage
	public let title: String
	public let message: String?
	public let style: Style
	public let action: EmptyContentAction

	public init(
		_ image: UIImage,
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
		VStack {
			Spacer()

			Image(uiImage: image)
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
			.background(style == .error ? Color.appErrorLight : Color.appPrimaryLight)
			.cornerRadius(.standardRadius)
			.padding(.bottom, .smallSpacing)

			action
		}
		.padding()
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
		.tint(.appAction)
	}
}
