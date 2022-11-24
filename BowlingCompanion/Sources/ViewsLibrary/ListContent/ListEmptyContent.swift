import SwiftUI
import ThemesLibrary

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
		ScrollView {
			VStack {
				Image(uiImage: image)
					.resizable()
					.scaledToFit()
//					.cornerRadius(Theme.Spacing.standard)
					.padding(.bottom, Theme.Spacing.small)

				VStack(spacing: Theme.Spacing.small) {
					Text(title)
						.font(.headline)
						.foregroundColor(style == .error ? Theme.Colors.error : Theme.Colors.text)

					if let message {
						Text(message)
							.font(.body)
							.foregroundColor(Theme.Colors.text)
							.multilineTextAlignment(.center)
					}
				}
				.padding()
				.frame(maxWidth: .infinity)
				.background(style == .error ? Theme.Colors.errorLight : Theme.Colors.primaryLight)
				.cornerRadius(Theme.Spacing.small)
				.padding(.bottom, Theme.Spacing.small)

				action
			}.padding()
		}
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
		.foregroundColor(Theme.Colors.textOnAction)
		.tint(Theme.Colors.action)
	}
}
