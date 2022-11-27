import SwiftUI
import ThemesLibrary

public struct DeleteButton: View {
	let perform: () -> Void

	public init(perform: @escaping () -> Void) {
		self.perform = perform
	}

	public var body: some View {
		Button(role: .destructive, action: perform) {
			Label("Delete", systemImage: "trash")
		}
		.tint(.appDestructive)
	}
}
