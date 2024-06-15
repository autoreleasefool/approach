import AssetsLibrary
import SwiftUI
import ViewsLibrary

struct ChipButton: View {
	let icon: SFSymbol
	let title: String
	@Binding var isOn: Bool

	var body: some View {
		Button {
			isOn.toggle()
		} label: {
			Chip(
				title: title,
				icon: icon,
				accessory: isOn ? .radioBoxSelected : .radioBox,
				style: isOn ? .primary : .plain
			)
		}
		.buttonStyle(.borderless)
	}
}
