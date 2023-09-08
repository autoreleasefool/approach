import SwiftUI

extension View {
	private func matchDimension<K>(
		byKey: K.Type = K.self,
		to: Binding<K.Value>,
		proxyReader: @escaping (GeometryProxy) -> some View
	) -> some View where K: PreferenceKey, K.Value == CGFloat {
		self
			.overlay(GeometryReader(content: proxyReader))
			.onPreferenceChange(byKey) {
				guard to.wrappedValue == .zero else { return }
				to.wrappedValue = $0
			}
	}

	public func matchWidth<K>(
		byKey: K.Type = K.self,
		to: Binding<K.Value>
	) -> some View where K: PreferenceKey, K.Value == CGFloat {
		self.matchDimension(byKey: byKey, to: to) { proxy in
			Color.clear.preference(key: byKey, value: proxy.size.width)
		}
		.frame(width: to.wrappedValue > .zero ? to.wrappedValue : nil)
	}

	public func matchHeight<K>(
		byKey: K.Type = K.self,
		to: Binding<K.Value>
	) -> some View where K: PreferenceKey, K.Value == CGFloat {
		self.matchDimension(byKey: byKey, to: to) { proxy in
			Color.clear.preference(key: byKey, value: proxy.size.height)
		}
		.frame(height: to.wrappedValue > .zero ? to.wrappedValue : nil)
	}
}

public protocol MatchDimensionPreferenceKey {}

public extension PreferenceKey where Self: MatchDimensionPreferenceKey {
	static var defaultValue: CGFloat { .zero }
	static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
		value = max(value, nextValue())
	}
}
