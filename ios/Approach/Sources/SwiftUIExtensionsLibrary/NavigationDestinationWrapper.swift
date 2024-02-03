import SwiftUI

extension View {
	@available(iOS, introduced: 16, deprecated: 17)
	@available(macOS, introduced: 13, deprecated: 14)
	@available(tvOS, introduced: 16, deprecated: 17)
	@available(watchOS, introduced: 9, deprecated: 10)
	@ViewBuilder
	public func navigationDestinationWrapper<D: Hashable, C: View>(
		item: Binding<D?>,
		@ViewBuilder destination: @escaping (D) -> C
	) -> some View {
		navigationDestination(isPresented: item.isPresented) {
			if let item = item.wrappedValue {
				destination(item)
			}
		}
	}
}

fileprivate extension Optional where Wrapped: Hashable {
	var isPresented: Bool {
		get { self != nil }
		set { if !newValue { self = nil } }
	}
}
