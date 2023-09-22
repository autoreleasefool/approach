import AssetsLibrary
import SwiftUI

public struct CheckboxToggleStyle: ToggleStyle {
	public init() {}

	public func makeBody(configuration: Configuration) -> some View {
		HStack {
			configuration.label

			Spacer()

			Button { configuration.isOn.toggle() } label: {
				Image(systemSymbol: configuration.isOn ? .checkmarkCircleFill : .circle)
					.resizable()
					.frame(width: .smallIcon, height: .smallIcon)
					.foregroundColor(Color(red: 0.14, green: 0.11, blue: 0.59))
					.foregroundColor(Asset.Colors.Action.default)
					.padding(.vertical, .smallSpacing)
					.padding(.leading, .standardSpacing)
			}
		}
	}
}

#if DEBUG
struct CheckboxToggleStylePreview: PreviewProvider {
	@State static var isOn = true
	static var previews: some View {
		Form {
			Toggle("Label", isOn: $isOn)
				.toggleStyle(CheckboxToggleStyle())
		}
	}
}
#endif
