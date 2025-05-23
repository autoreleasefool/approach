import AssetsLibrary
import SwiftUI
import ViewsLibrary

public struct BasicTipView: View {
	let tip: Tip
	let isDismissable: Bool
	let onDismiss: () -> Void

	public init(tip: Tip, isDismissable: Bool = true, onDismiss: @escaping () -> Void) {
		self.tip = tip
		self.isDismissable = isDismissable
		self.onDismiss = onDismiss
	}

	public var body: some View {
		ZStack(alignment: .topTrailing) {
			VStack(alignment: .leading) {
				HStack(alignment: .top, spacing: 0) {
					Text(tip.title)
						.font(.headline)
						.frame(maxWidth: .infinity, alignment: .leading)

					if isDismissable {
						Spacer(minLength: .extraTinyIcon + .smallerIcon)
					}
				}
				.frame(maxWidth: .infinity)

				if let message = tip.message {
					Text(message)
						.frame(maxWidth: .infinity, alignment: .leading)
						.fixedSize(horizontal: false, vertical: true)
				}
			}

			if isDismissable {
				Button(action: onDismiss) {
					Image(systemName: "xmark")
						.resizable()
						.scaledToFit()
						.frame(width: .extraTinyIcon, height: .extraTinyIcon, alignment: .topTrailing)
						.frame(width: .smallerIcon, height: .smallerIcon, alignment: .topTrailing)
						.contentShape(Rectangle())
				}
				.buttonStyle(TappableElement())
			}
		}
	}
}

// swiftlint:disable line_length
#if DEBUG
#Preview {
	List {
		Section {
			BasicTipView(tip: .init(title: "Title", message: "Message")) { }
		}

		Section {
			BasicTipView(tip: .init(title: "Title", message: "Message"), isDismissable: false) { }
		}

		Section {
			BasicTipView(tip: .init(title: "This is a title", message: "And this is a message.")) { }
		}

		Section {
			BasicTipView(tip: .init(title: "This is a title", message: "And this is a message."), isDismissable: false) { }
		}

		Section {
			BasicTipView(tip: .init(title: "Extremely long title that makes it all the way over to the close icon and extends onto many lines", message: "Even longer message that could go on for as many as three or four or five or size or seven lines oh nevermind only four")) { }
		}

		Section {
			BasicTipView(tip: .init(title: "Extremely long title that makes it all the way over to the close icon and extends onto many lines", message: "Even longer message that could go on for as many as three or four or five or size or seven lines oh nevermind only four"), isDismissable: false) { }
		}
	}
}
#endif
// swiftlint:enable line_length
