import SwiftUI
import ViewsLibrary

public struct BasicTipView: View {
	let tip: Tip
	let onDismiss: () -> Void

	public init(tip: Tip, onDismiss: @escaping () -> Void) {
		self.tip = tip
		self.onDismiss = onDismiss
	}

	public var body: some View {
		VStack(alignment: .leading, spacing: 0) {
			HStack(alignment: .center, spacing: 0) {
				Text(tip.title)
					.font(.headline)
					.frame(maxWidth: .infinity, alignment: .leading)
				Button(action: onDismiss) {
					Image(systemName: "xmark")
						.resizable()
						.scaledToFit()
						.frame(width: .smallIcon, height: .smallSpacing)
						.padding(.vertical)
						.padding(.leading)
				}
				.buttonStyle(TappableElement())
			}
			.frame(maxWidth: .infinity)

			Text(tip.message)
				.frame(maxWidth: .infinity, alignment: .leading)
		}
	}
}

#if DEBUG
struct BasicTipViewPreview: PreviewProvider {
	static var previews: some View {
		List {
			Section {
				BasicTipView(tip: .statisticsDetails) { }
			}
		}
	}
}
#endif
