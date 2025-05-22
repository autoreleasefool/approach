import AssetsLibrary
import SwiftUI

public struct SortButton: View {
	let isActive: Bool
	let perform: () -> Void

	public init(isActive: Bool, perform: @escaping () -> Void) {
		self.isActive = isActive
		self.perform = perform
	}

	public var body: some View {
		Button(action: perform) {
			Image(systemName: isActive ? "arrow.up.arrow.down.square.fill" : "arrow.up.arrow.down.square")
		}
	}
}
