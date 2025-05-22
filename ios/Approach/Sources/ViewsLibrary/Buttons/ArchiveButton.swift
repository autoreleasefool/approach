import AssetsLibrary
import StringsLibrary
import SwiftUI

public struct ArchiveButton: View {
	let perform: () -> Void

	public init(perform: @escaping () -> Void) {
		self.perform = perform
	}

	public var body: some View {
		Button(role: .destructive, action: perform) {
			Label(Strings.Action.archive, systemImage: "archivebox")
				.foregroundColor(Asset.Colors.Destructive.default)
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
