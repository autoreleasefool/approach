import AssetsLibrary
import SwiftUI

public struct FilterButton: View {
	let isActive: Bool
	let perform: () -> Void

	public init(isActive: Bool, perform: @escaping () -> Void) {
		self.isActive = isActive
		self.perform = perform
	}

	public var body: some View {
		Button(action: perform) {
			Image(systemName: isActive ? "line.3.horizontal.decrease.circle.fill" : "line.3.horizontal.decrease.circle")
		}
	}
}
