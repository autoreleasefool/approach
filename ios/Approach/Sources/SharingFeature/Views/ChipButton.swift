import AssetsLibrary
import SwiftUI
import ViewsLibrary

struct ChipButton: View {
	let systemImage: String
	let title: String
	@Binding var isOn: Bool

	var body: some View {
		Button {
			isOn.toggle()
		} label: {
			Chip(
				title: title,
				systemImage: systemImage,
				accessory: isOn ? .radioBoxSelected : .radioBox,
				style: isOn ? .primary : .plain
			)
		}
		.buttonStyle(.borderless)
	}
}

#Preview {
	HStack {
		ChipButton(
			systemImage: "star",
			title: "Star",
			isOn: .constant(false)
		)

		ChipButton(
			systemImage: "star",
			title: "Star",
			isOn: .constant(false)
		)

		ChipButton(
			systemImage: "star",
			title: "Star",
			isOn: .constant(false)
		)
	}
}
