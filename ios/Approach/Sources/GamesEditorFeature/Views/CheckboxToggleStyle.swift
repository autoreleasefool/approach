import AssetsLibrary
import SwiftUI

struct CheckboxToggleStyle: ToggleStyle {
	func makeBody(configuration: Configuration) -> some View {
		HStack {
			configuration.label

			Spacer()

			Button { configuration.isOn.toggle() } label: {
				Image(systemSymbol: configuration.isOn ? .checkmarkCircleFill : .circle)
					.resizable()
					.frame(width: .smallIcon, height: .smallIcon)
					.foregroundColor(Asset.Colors.Action.default)
					.padding(.vertical, .smallSpacing)
					.padding(.leading, .standardSpacing)
			}
		}
	}
}

extension ToggleStyle where Self == CheckboxToggleStyle {
	static var checkboxToggle: CheckboxToggleStyle { .init() }
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
