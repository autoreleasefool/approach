import AssetsLibrary
import SwiftUI

struct CheckboxToggleStyle: ToggleStyle {
	func makeBody(configuration: Configuration) -> some View {
		HStack {
			configuration.label

			Spacer()

			Button { configuration.isOn.toggle() } label: {
				Image(systemName: configuration.isOn ? "checkmark.circle.fill" : "circle")
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
#Preview {
	@Previewable @State var isOn = true

	Form {
		Toggle("Label", isOn: $isOn)
			.toggleStyle(CheckboxToggleStyle())
	}
}
#endif
