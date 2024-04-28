import AssetsLibrary
import SwiftUI

public struct ShareButton: View {
	let perform: () -> Void

	public init(perform: @escaping () -> Void) {
		self.perform = perform
	}

	public var body: some View {
		Button(action: perform) {
			Image(systemSymbol: .squareAndArrowUp)
		}
	}
}
