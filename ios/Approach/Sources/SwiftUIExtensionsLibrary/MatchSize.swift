import SwiftUI

extension View {
	public func matchWidth<K>(
		byKey: K.Type = K.self,
		to: Binding<K.Value>
	) -> some View where K: PreferenceKey, K.Value == CGFloat {
		overlay(
			GeometryReader { proxy in
				Color.clear.preference(key: byKey, value: proxy.size.width)
			}
		)
		.onPreferenceChange(byKey) {
			guard to.wrappedValue == .zero else { return }
			to.wrappedValue = $0
		}
	}

	public func matchHeight<K>(
		byKey: K.Type = K.self,
		to: Binding<K.Value>
	) -> some View where K: PreferenceKey, K.Value == CGFloat {
		overlay(
			GeometryReader { proxy in
				Color.clear.preference(key: byKey, value: proxy.size.height)
			}
		)
		.onPreferenceChange(byKey) {
			guard to.wrappedValue == .zero else { return }
			to.wrappedValue = $0
		}
	}
}

public protocol MatchHeightPreferenceKey {}
public protocol MatchWidthPreferenceKey {}

public extension PreferenceKey where Self: MatchWidthPreferenceKey {
	static var defaultValue: CGFloat { .zero }
	static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
		value = max(value, nextValue())
	}
}

public extension PreferenceKey where Self: MatchHeightPreferenceKey {
	static var defaultValue: CGFloat { .zero }
	static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
		value = max(value, nextValue())
	}
}
