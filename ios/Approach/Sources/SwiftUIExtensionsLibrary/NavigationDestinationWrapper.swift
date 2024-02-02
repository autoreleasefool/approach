import SwiftUI

extension View {
	@ViewBuilder
	public func navigationDestinationWrapper<D: Hashable, C: View>(
		item: Binding<D?>,
		@ViewBuilder destination: @escaping (D) -> C
	) -> some View {
		if #available(iOS 17, macOS 14, tvOS 17, visionOS 1, watchOS 10, *) {
			navigationDestination(item: item, destination: destination)
		} else {
			navigationDestination(
				isPresented: Binding(
					get: { item.wrappedValue != nil },
					set: { isPresented, transaction in
						if !isPresented {
							item.transaction(transaction).wrappedValue = nil
						}
					}
				)
			) {
				if let item = item.wrappedValue {
					destination(item)
				}
			}
		}
	}
}
