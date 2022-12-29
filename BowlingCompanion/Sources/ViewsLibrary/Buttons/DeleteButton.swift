import StringsLibrary
import SwiftUI
import AssetsLibrary

public struct DeleteButton: View {
	let perform: () -> Void

	public init(perform: @escaping () -> Void) {
		self.perform = perform
	}

	public var body: some View {
		Button(role: .destructive, action: perform) {
			Label(Strings.Action.delete, systemImage: "trash")
		}
		.tint(.appDestructive)
	}
}
