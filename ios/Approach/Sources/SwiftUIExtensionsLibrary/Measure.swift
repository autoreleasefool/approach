import SwiftUI

extension View {
	public func measure<K>(key: K.Type = K.self, to: Binding<K.Value>) -> some View where K: PreferenceKey, K.Value == CGSize {
		overlay(
			GeometryReader { proxy in
				Color.clear.preference(key: key, value: proxy.size)
			}
		)
		.onPreferenceChange(key) { to.wrappedValue = $0 }
	}
}
