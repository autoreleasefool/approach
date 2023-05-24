import SwiftUI

extension View {
	public func measure<K>(
		key: K.Type = K.self,
		to: Binding<K.Value>,
		exactlyOnce: Bool = false
	) -> some View where K: PreferenceKey, K.Value == CGSize {
		overlay(
			GeometryReader { proxy in
				Color.clear.preference(key: key, value: proxy.size)
			}
		)
		.onPreferenceChange(key) {
			guard !exactlyOnce || to.wrappedValue == .zero else { return }
			to.wrappedValue = $0
		}
	}
}

public protocol CGSizePreferenceKey {}

public extension PreferenceKey where Self: CGSizePreferenceKey {
	static var defaultValue: CGSize { .zero }
	static func reduce(value: inout CGSize, nextValue: () -> CGSize) {
		value = nextValue()
	}
}
