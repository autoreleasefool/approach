import AssetsLibrary
import SwiftUI
import ViewsLibrary

public struct ShortTipView: View {
	let tip: Tip
	let onDismiss: () -> Void

	public init(tip: Tip, onDismiss: @escaping () -> Void) {
		self.tip = tip
		self.onDismiss = onDismiss
	}

	public var body: some View {
		HStack(alignment: .center, spacing: 0) {
			Text(tip.title)
				.font(.caption)
				.frame(maxWidth: .infinity, alignment: .leading)
			Button(action: onDismiss) {
				Image(systemSymbol: .xmark)
					.resizable()
					.scaledToFit()
					.frame(width: .smallIcon, height: .smallSpacing)
					.padding(.leading)
			}
			.buttonStyle(TappableElement())
		}
		.frame(maxWidth: .infinity)
		.onTapGesture(perform: onDismiss)
	}
}

#if DEBUG
#Preview {
	List {
		Section {
			ShortTipView(tip: .init(title: "This is a short and sweet tip")) { }
		}
	}
}
#endif
