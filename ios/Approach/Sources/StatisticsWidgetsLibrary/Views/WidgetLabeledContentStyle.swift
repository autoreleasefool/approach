import AssetsLibrary
import SwiftUI

public struct WidgetLabeledContentStyle: LabeledContentStyle {
	let labelColor: ColorAsset
	let contentColor: ColorAsset

	init(labelColor: ColorAsset, contentColor: ColorAsset) {
		self.labelColor = labelColor
		self.contentColor = contentColor
	}

	public func makeBody(configuration: Configuration) -> some View {
		HStack(alignment: .firstTextBaseline) {
			configuration.label
				.foregroundColor(labelColor)
			Spacer()
			configuration.content
				.foregroundColor(contentColor)
				.multilineTextAlignment(.trailing)
		}
		.font(.caption)
	}
}
