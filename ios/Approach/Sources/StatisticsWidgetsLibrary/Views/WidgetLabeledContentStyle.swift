import AssetsLibrary
import SwiftUI

public struct WidgetLabeledContentStyle: LabeledContentStyle {
	let labelColor: ColorAsset
	let contentColor: ColorAsset

	public func makeBody(configuration: Configuration) -> some View {
		HStack(alignment: .firstTextBaseline) {
			configuration.label
				.foregroundStyle(labelColor)
			Spacer()
			configuration.content
				.foregroundStyle(contentColor)
				.multilineTextAlignment(.trailing)
		}
		.font(.caption)
	}
}
