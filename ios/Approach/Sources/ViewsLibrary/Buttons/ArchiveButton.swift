import AssetsLibrary
import StringsLibrary
import SwiftUI

public struct ArchiveButton: View {
	let role: ButtonRole?
	let perform: () -> Void

	public init(role: ButtonRole? = .destructive, perform: @escaping () -> Void) {
		self.role = role
		self.perform = perform
	}

	public var body: some View {
		Button(role: role, action: perform) {
			Label(Strings.Action.archive, systemImage: "archivebox")
				.foregroundStyle(Asset.Colors.Destructive.default)
		}
		.tint(Asset.Colors.Destructive.default)
	}
}

public struct UnarchiveButton: View {
	let perform: () -> Void

	public init(perform: @escaping () -> Void) {
		self.perform = perform
	}

	public var body: some View {
		Button(action: perform) {
			Label(Strings.Action.restore, systemImage: "arrow.uturn.backward")
		}
		.tint(Asset.Colors.Action.default)
	}
}
