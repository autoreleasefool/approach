import SwiftUI

extension View {
	func compactList() -> some View {
		self.modifier(CompactListModifier())
	}
}

struct CompactListModifier: ViewModifier {
	func body(content: Content) -> some View {
		if #available(iOS 17.0, *) {
			content.listSectionSpacing(.compact)
		} else {
			content
		}
	}
}
