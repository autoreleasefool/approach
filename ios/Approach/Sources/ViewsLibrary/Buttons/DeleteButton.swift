import AssetsLibrary
import StringsLibrary
import SwiftUI

public struct DeleteButton: View {
	let role: ButtonRole?
	let perform: () -> Void

	public init(role: ButtonRole? = .destructive, perform: @escaping () -> Void) {
		self.role = role
		self.perform = perform
	}

	public var body: some View {
		Button(role: role, action: perform) {
			Label(Strings.Action.delete, systemImage: "trash")
				.foregroundStyle(Asset.Colors.Destructive.default)
		}
		.tint(Asset.Colors.Destructive.default)
	}
}
