import SwiftUI

extension View {
	public func compactList() -> some View {
		self.modifier(CompactListModifier())
	}
}

public struct CompactListModifier: ViewModifier {
	public init() {}

	public func body(content: Content) -> some View {
		if #available(iOS 17.0, *) {
			content.listSectionSpacing(.compact)
		} else {
			content
		}
	}
}
